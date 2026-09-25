import { useLocalSearchParams, useRouter } from "expo-router";
import React, { useEffect, useRef, useState } from "react";
import { ActivityIndicator, Alert, FlatList, Image, KeyboardAvoidingView, Linking, Platform, Pressable, SafeAreaView, Text, TextInput, View } from "react-native";
import { Audio } from "expo-av";
import * as Contacts from "expo-contacts";
import * as DocumentPicker from "expo-document-picker";
import * as ImagePicker from "expo-image-picker";
import * as Location from "expo-location";
import { Ionicons } from "@expo/vector-icons";
import { supabase } from "../../src/lib/supabase";

type Message = { id: string; sender_id: string; message_text: string; attachment_path: string | null; message_type: string; metadata: any; created_at: string };

export default function ChatScreen() {
  const router = useRouter();
  const { matchId, userId, name, avatar } = useLocalSearchParams<{ matchId: string; userId: string; name: string; avatar?: string }>();
  const [conversationId, setConversationId] = useState<string | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [text, setText] = useState("");
  const [userIdValue, setUserIdValue] = useState<string | null>(null);
  const [recording, setRecording] = useState<Audio.Recording | null>(null);
  const listRef = useRef<FlatList<Message>>(null);

  useEffect(() => {
    let channel: ReturnType<typeof supabase.channel> | undefined;
    (async () => {
      const { data: { user } } = await supabase.auth.getUser();
      if (!user || !userId || !matchId) return;
      setUserIdValue(user.id);
      const id = await getOrCreateConversation(user.id, userId);
      setConversationId(id);
      if (!id) return;
      const { data } = await supabase.from("messages").select("*").eq("conversation_id", id).order("created_at", { ascending: true });
      setMessages(data || []);
      channel = supabase.channel(`conversation:${id}`).on("postgres_changes", { event: "INSERT", schema: "public", table: "messages", filter: `conversation_id=eq.${id}` }, (payload) => {
        setMessages((current) => current.some((item) => item.id === payload.new.id) ? current : [...current, payload.new as Message]);
      }).subscribe();
    })();
    return () => { if (channel) supabase.removeChannel(channel); };
  }, [matchId, userId]);

  const sendMessage = async (messageText: string, type = "text", metadata: any = {}, attachmentPath: string | null = null) => {
    if (!conversationId || !userIdValue || (!messageText.trim() && !attachmentPath)) return;
    const { error } = await supabase.from("messages").insert({ conversation_id: conversationId, sender_id: userIdValue, message_text: messageText.trim() || " ", message_type: type, metadata, attachment_path: attachmentPath });
    if (error) Alert.alert("Message failed", error.message);
    else setText("");
  };

  const uploadAsset = async (uri: string, name: string, mimeType: string) => {
    if (!userIdValue) return;
    const response = await fetch(uri);
    const arrayBuffer = await response.arrayBuffer();
    const path = `${userIdValue}/${Date.now()}-${name.replace(/[^a-zA-Z0-9._-]/g, "_")}`;
    const { error } = await supabase.storage.from("chat-media").upload(path, arrayBuffer, { contentType: mimeType, upsert: false });
    if (error) throw error;
    const messageType = mimeType.startsWith("audio/") ? "voice" : mimeType.startsWith("image/") || mimeType.startsWith("video/") ? "media" : "file";
    await sendMessage(name, messageType, { mimeType }, path);
  };

  const openAttachment = async (path: string | null) => {
    if (!path) return;
    const { data, error } = await supabase.storage.from("chat-media").createSignedUrl(path, 60 * 10);
    if (error || !data?.signedUrl) return Alert.alert("Attachment unavailable", error?.message || "This file is no longer available.");
    await Linking.openURL(data.signedUrl);
  };

  const chooseMedia = async () => {
    const permission = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (!permission.granted) return Alert.alert("Permission needed", "Allow gallery access to share photos and videos.");
    const result = await ImagePicker.launchImageLibraryAsync({ mediaTypes: ImagePicker.MediaTypeOptions.All, quality: 0.8 });
    if (!result.canceled) {
      const asset = result.assets[0];
      try { await uploadAsset(asset.uri, asset.fileName || `media-${Date.now()}`, asset.mimeType || "application/octet-stream"); } catch (error: any) { Alert.alert("Upload failed", error.message); }
    }
  };

  const chooseFile = async () => {
    const result = await DocumentPicker.getDocumentAsync({ copyToCacheDirectory: true });
    if (!result.canceled) {
      const asset = result.assets[0];
      try { await uploadAsset(asset.uri, asset.name, asset.mimeType || "application/octet-stream"); } catch (error: any) { Alert.alert("Upload failed", error.message); }
    }
  };

  const shareLocation = async () => {
    const permission = await Location.requestForegroundPermissionsAsync();
    if (permission.status !== "granted") return Alert.alert("Permission needed", "Allow location access to share your current location.");
    const location = await Location.getCurrentPositionAsync({});
    await sendMessage("Current location", "location", { latitude: location.coords.latitude, longitude: location.coords.longitude });
  };

  const shareContact = async () => {
    const permission = await Contacts.requestPermissionsAsync();
    if (permission.status !== "granted") return Alert.alert("Permission needed", "Allow contacts access to choose a contact.");
    const result = await Contacts.presentContactPickerAsync();
    if (result) await sendMessage(result.name, "contact", { contact: result });
  };

  const startRecording = async () => {
    if (recording) {
      await recording.stopAndUnloadAsync();
      const uri = recording.getURI();
      setRecording(null);
      if (uri) try { await uploadAsset(uri, `voice-${Date.now()}.m4a`, "audio/m4a"); } catch (error: any) { Alert.alert("Voice message failed", error.message); }
      return;
    }
    const permission = await Audio.requestPermissionsAsync();
    if (!permission.granted) return Alert.alert("Permission needed", "Allow microphone access to record a voice message.");
    await Audio.setAudioModeAsync({ allowsRecordingIOS: true, playsInSilentModeIOS: true });
    const result = await Audio.Recording.createAsync(Audio.RecordingOptionsPresets.HIGH_QUALITY);
    setRecording(result.recording);
  };

  const showAttachmentMenu = () => Alert.alert("Share", "Choose what to send", [
    { text: "Photo or video", onPress: chooseMedia },
    { text: "File", onPress: chooseFile },
    { text: "Current location", onPress: shareLocation },
    { text: "Contact", onPress: shareContact },
    { text: "Cancel", style: "cancel" },
  ]);

  return (
    <SafeAreaView className="flex-1 bg-surface">
      <View className="bg-white px-4 py-3 flex-row items-center border-b border-gray-100">
        <Pressable onPress={() => router.back()}><Ionicons name="arrow-back" size={25} color="#000080" /></Pressable>
        {avatar ? <Image source={{ uri: avatar }} className="w-10 h-10 rounded-full ml-3" /> : <View className="w-10 h-10 rounded-full bg-blue-50 items-center justify-center ml-3"><Ionicons name="person" size={20} color="#4D7C9E" /></View>}
        <Text className="flex-1 ml-3 text-lg font-bold text-navyblue">{name || "Match"}</Text>
        <Pressable onPress={() => router.push({ pathname: "/chat/settings", params: { conversationId, name } })}><Ionicons name="settings-outline" size={24} color="#000080" /></Pressable>
      </View>
      <KeyboardAvoidingView className="flex-1" behavior={Platform.OS === "ios" ? "padding" : undefined} keyboardVerticalOffset={90}>
        <FlatList ref={listRef} data={messages} keyExtractor={(item) => item.id} contentContainerStyle={{ padding: 16 }} onContentSizeChange={() => listRef.current?.scrollToEnd({ animated: true })} renderItem={({ item }) => (
          <View className={`mb-3 max-w-[82%] rounded-2xl px-4 py-3 ${item.sender_id === userIdValue ? "self-end bg-navyblue" : "self-start bg-white border border-gray-100"}`}>
            {item.message_type === "location" ? <Pressable onPress={() => Linking.openURL(`https://maps.google.com/?q=${item.metadata?.latitude},${item.metadata?.longitude}`)}><Text className={item.sender_id === userIdValue ? "text-white" : "text-navyblue"}>📍 Current location</Text></Pressable> : item.message_type === "contact" ? <Text className={item.sender_id === userIdValue ? "text-white" : "text-navyblue"}>👤 {item.message_text}</Text> : item.attachment_path ? <Pressable onPress={() => openAttachment(item.attachment_path)}><Text className={item.sender_id === userIdValue ? "text-white" : "text-navyblue"}>{item.message_type === "voice" ? "🎙️ Voice message" : item.message_type === "media" ? "🖼️ " : "📎 "}{item.message_text}</Text></Pressable> : <Text className={item.sender_id === userIdValue ? "text-white" : "text-gray-800"}>{item.message_text}</Text>}
          </View>
        )} />
        <View className="bg-white border-t border-gray-100 px-3 py-2 flex-row items-end">
          <Pressable onPress={showAttachmentMenu} className="p-2"><Ionicons name="add-circle-outline" size={28} color="#000080" /></Pressable>
          <TextInput value={text} onChangeText={setText} placeholder="Message" multiline className="flex-1 bg-gray-100 rounded-2xl px-4 py-2 max-h-24" />
          {text.trim() ? <Pressable onPress={() => sendMessage(text)} className="p-2"><Ionicons name="send" size={25} color="#000080" /></Pressable> : <Pressable onPress={startRecording} className="p-2"><Ionicons name={recording ? "stop-circle" : "mic"} size={27} color={recording ? "#EF4444" : "#000080"} /></Pressable>}
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

async function getOrCreateConversation(currentUserId: string, otherUserId: string) {
  const { data: mine } = await supabase.from("conversation_members").select("conversation_id").eq("user_id", currentUserId);
  for (const membership of mine || []) {
    const { data: other } = await supabase.from("conversation_members").select("conversation_id").eq("conversation_id", membership.conversation_id).eq("user_id", otherUserId).maybeSingle();
    if (other) return membership.conversation_id;
  }
  const { data: conversation, error } = await supabase.from("conversations").insert({ creator_id: currentUserId }).select("id").single();
  if (error || !conversation) return null;
  const { error: memberError } = await supabase.from("conversation_members").insert([
    { conversation_id: conversation.id, user_id: currentUserId },
    { conversation_id: conversation.id, user_id: otherUserId },
  ]);
  return memberError ? null : conversation.id;
}
