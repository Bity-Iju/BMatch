import { View, Text, SafeAreaView, FlatList, Image, TouchableOpacity, Alert } from "react-native";
import React from "react";
import { useRouter } from "expo-router";
import { Footer } from "../../src/components/Footer";

const MOCK_CHATS = [
  {
    id: "1",
    name: "Amina",
    lastMessage: "Looking forward to our chat!",
    time: "10:30 AM",
    avatar: "https://randomuser.me/api/portraits/women/44.jpg",
    unread: 2,
  },
  {
    id: "2",
    name: "John",
    lastMessage: "Hello, how are you today?",
    time: "Yesterday",
    avatar: "https://randomuser.me/api/portraits/men/32.jpg",
    unread: 0,
  },
];

export default function MessagesScreen() {
  const router = useRouter();

  return (
    <SafeAreaView className="flex-1 bg-white">
      <FlatList
        data={MOCK_CHATS}
        keyExtractor={(item) => item.id}
        ListFooterComponent={<Footer />}
        renderItem={({ item }) => (
          <TouchableOpacity
            className="flex-row items-center px-6 py-4 border-b border-gray-50"
            onPress={() => Alert.alert("Feature", "Chat screen coming soon!")}
          >
            <Image source={{ uri: item.avatar }} className="w-14 h-14 rounded-full" />
            <View className="flex-1 ml-4">
              <View className="flex-row justify-between items-center">
                <Text className="text-lg font-bold text-gray-900">{item.name}</Text>
                <Text className="text-gray-400 text-sm">{item.time}</Text>
              </View>
              <View className="flex-row justify-between items-center mt-1">
                <Text className="text-gray-500 flex-1 mr-2" numberOfLines={1}>
                  {item.lastMessage}
                </Text>
                {item.unread > 0 && (
                  <View className="bg-navyblue rounded-full px-2 py-0.5">
                    <Text className="text-white text-xs font-bold">{item.unread}</Text>
                  </View>
                )}
              </View>
            </View>
          </TouchableOpacity>
        )}
      />
    </SafeAreaView>
  );
}
