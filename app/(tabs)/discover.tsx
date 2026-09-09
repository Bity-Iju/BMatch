import { View, Text, SafeAreaView, ScrollView } from "react-native";
import React from "react";
import { ProfileCard } from "../../src/components/ProfileCard";
import { Footer } from "../../src/components/Footer";

const MOCK_USERS = [
  {
    name: "Amina",
    age: 24,
    state: "Lagos",
    lga: "Ikeja",
    religion: "Muslim",
    avatar: "https://randomuser.me/api/portraits/women/44.jpg",
  },
  {
    name: "John",
    age: 29,
    state: "Abuja",
    lga: "Garki",
    religion: "Christianity",
    avatar: "https://randomuser.me/api/portraits/men/32.jpg",
  },
];

export default function DiscoverScreen() {
  return (
    <SafeAreaView className="flex-1 bg-surface">
      <ScrollView contentContainerStyle={{ flexGrow: 1 }} showsVerticalScrollIndicator={false}>
        <View className="flex-1 px-4 py-4">
          {MOCK_USERS.map((user, index) => (
            <View key={index} className="mb-6">
              <ProfileCard user={user} />
            </View>
          ))}

          {MOCK_USERS.length === 0 && (
            <View className="flex-1 items-center justify-center py-20">
              <Text className="text-muted text-lg">No more profiles found.</Text>
            </View>
          )}
        </View>
        <Footer />
      </ScrollView>
    </SafeAreaView>
  );
}
