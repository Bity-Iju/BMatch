import { useLocalSearchParams, useRouter } from "expo-router";
import React, { useEffect, useState } from "react";
import { Alert, Pressable, SafeAreaView, ScrollView, Switch, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { supabase } from "../../src/lib/supabase";

export default function ChatSettingsScreen() {
  const router = useRouter();
  const { conversationId, name } = useLocalSearchParams<{ conversationId: string; name: string }>();
  const [disappearing, setDisappearing] = useState(false);
  const [locked, setLocked] = useState(false);
  const [userId, setUserId] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      const { data: { user } } = await supabase.auth.getUser();
      setUserId(user?.id || null);
      if (user && conversationId) {
        const { data } = await supabase.from("chat_settings").select("disappearing_days, locked").eq("conversation_id", conversationId).eq("user_id", user.id).maybeSingle();
        setDisappearing(data?.disappearing_days === 7);
        setLocked(Boolean(data?.locked));
      }
    })();
  }, [conversationId]);

  const save = async (nextDisappearing = disappearing, nextLocked = locked) => {
    if (!userId || !conversationId) return;
    const { error } = await supabase.from("chat_settings").upsert({ conversation_id: conversationId, user_id: userId, disappearing_days: nextDisappearing ? 7 : null, locked: nextLocked }, { onConflict: "conversation_id,user_id" });
    if (error) Alert.alert("Could not save setting", error.message);
  };

  const clearChat = () => Alert.alert("Clear chat", "Remove this conversation from your device?", [
    { text: "Cancel", style: "cancel" },
    { text: "Clear", style: "destructive", onPress: async () => {
      if (conversationId) await supabase.from("messages").delete().eq("conversation_id", conversationId);
      router.back();
    }},
  ]);

  return <SafeAreaView className="flex-1 bg-surface">
    <View className="bg-white px-4 py-4 flex-row items-center border-b border-gray-100"><Pressable onPress={() => router.back()}><Ionicons name="arrow-back" size={25} color="#000080" /></Pressable><Text className="text-xl font-bold text-navyblue ml-4">Chat settings</Text></View>
    <ScrollView className="p-5">
      <Text className="text-gray-500 mb-4">Settings for your chat with {name || "this match"}</Text>
      <SettingRow icon="timer-outline" title="Disappearing messages" description="Messages disappear after 7 days" value={disappearing} onValueChange={(value) => { setDisappearing(value); save(value, locked); }} />
      <SettingRow icon="lock-closed-outline" title="Lock chat" description="Require device security before opening" value={locked} onValueChange={(value) => { setLocked(value); save(disappearing, value); }} />
      <Pressable onPress={clearChat} className="bg-white rounded-2xl p-5 mt-5 flex-row items-center"><Ionicons name="trash-outline" size={24} color="#EF4444" /><Text className="text-red-500 font-bold text-base ml-4">Clear chat</Text></Pressable>
    </ScrollView>
  </SafeAreaView>;
}

function SettingRow({ icon, title, description, value, onValueChange }: { icon: any; title: string; description: string; value: boolean; onValueChange: (value: boolean) => void }) {
  return <View className="bg-white rounded-2xl p-4 mb-3 flex-row items-center"><Ionicons name={icon} size={24} color="#000080" /><View className="flex-1 ml-4"><Text className="font-bold text-gray-800">{title}</Text><Text className="text-gray-500 mt-1">{description}</Text></View><Switch value={value} onValueChange={onValueChange} trackColor={{ false: "#CBD5E1", true: "#A5B4FC" }} thumbColor={value ? "#000080" : "#F8FAFC"} /></View>;
}
