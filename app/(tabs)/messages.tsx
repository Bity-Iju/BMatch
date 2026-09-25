import { View, Text, SafeAreaView, FlatList, Image, TouchableOpacity, ActivityIndicator } from "react-native";
import React, { useCallback, useState } from "react";
import { useFocusEffect, useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { supabase } from "../../src/lib/supabase";

type MatchChat = { id: string; userId: string; name: string; avatar?: string | null };

export default function MessagesScreen() {
  const router = useRouter();
  const [chats, setChats] = useState<MatchChat[]>([]);
  const [loading, setLoading] = useState(true);

  const loadMatches = useCallback(async () => {
    setLoading(true);
    const { data: { user } } = await supabase.auth.getUser();
    if (!user) return;

    const { data: matches, error } = await supabase
      .from("matches")
      .select("id, first_user_id, second_user_id")
      .eq("status", "active")
      .or(`first_user_id.eq.${user.id},second_user_id.eq.${user.id}`);

    if (error) {
      console.error("Unable to load matches:", error.message);
      setLoading(false);
      return;
    }

    const otherIds = (matches || []).map((match) => ({
      match,
      userId: match.first_user_id === user.id ? match.second_user_id : match.first_user_id,
    }));
    const { data: profiles } = otherIds.length
      ? await supabase.from("profiles").select("id, full_name, avatar, avatar_url").in("id", otherIds.map((item) => item.userId))
      : { data: [] };

    setChats(otherIds.map(({ match, userId }) => {
      const profile = profiles?.find((item) => item.id === userId);
      return {
        id: match.id,
        userId,
        name: profile?.full_name || "Your match",
        avatar: profile?.avatar || profile?.avatar_url,
      };
    }));
    setLoading(false);
  }, []);

  useFocusEffect(useCallback(() => {
    loadMatches();
  }, [loadMatches]));

  if (loading) {
    return <View className="flex-1 justify-center items-center bg-white"><ActivityIndicator size="large" color="#000080" /></View>;
  }
  return (
    <SafeAreaView className="flex-1 bg-white">
      <View className="px-6 pt-5 pb-2 flex-row items-center justify-between">
        <View>
          <Text className="text-2xl font-extrabold text-navyblue">Chats</Text>
          <Text className="text-gray-500 mt-1">Only people you are matched with appear here.</Text>
        </View>
        <Ionicons name="shield-checkmark-outline" size={24} color="#000080" />
      </View>
      <FlatList
        data={chats}
        keyExtractor={(item) => item.id}
        ListEmptyComponent={
          <View className="items-center px-8 py-24">
            <Ionicons name="chatbubbles-outline" size={56} color="#CBD5E1" />
            <Text className="text-xl font-bold text-gray-700 mt-5">No matched chats yet</Text>
            <Text className="text-gray-500 text-center mt-2">When a match is active, you can start a private chat here.</Text>
          </View>
        }
        renderItem={({ item }) => (
          <TouchableOpacity
            className="flex-row items-center px-6 py-4 border-b border-gray-50"
            onPress={() => router.push({ pathname: "/chat/[matchId]", params: { matchId: item.id, userId: item.userId, name: item.name, avatar: item.avatar || "" } })}
          >
            {item.avatar ? <Image source={{ uri: item.avatar }} className="w-14 h-14 rounded-full" /> : (
              <View className="w-14 h-14 rounded-full bg-blue-50 items-center justify-center"><Ionicons name="person" size={26} color="#4D7C9E" /></View>
            )}
            <View className="flex-1 ml-4">
              <Text className="text-lg font-bold text-gray-900">{item.name}</Text>
              <Text className="text-gray-500 mt-1">Open secure conversation</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#94A3B8" />
          </TouchableOpacity>
        )}
      />
    </SafeAreaView>
  );
}
