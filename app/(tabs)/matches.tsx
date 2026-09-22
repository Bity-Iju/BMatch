import { View, Text, SafeAreaView, ScrollView } from "react-native";
import React from "react";
import { Footer } from "../../src/components/Footer";
import { Ionicons } from "@expo/vector-icons";

export default function MatchesScreen() {
  return (
    <SafeAreaView className="flex-1 bg-surface">
      <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
        <View className="flex-1 items-center justify-center px-8 py-20">
          <View className="w-24 h-24 bg-blue-50 rounded-full items-center justify-center mb-6">
            <Ionicons name="heart" size={48} color="#000080" />
          </View>
          <Text className="text-2xl font-extrabold text-navyblue text-center">Your Matches</Text>
          <Text className="text-muted text-center mt-4 text-lg">
            When you find a mutual connection, they will appear here. Start discovering to find your match!
          </Text>
        </View>
        <Footer />
      </ScrollView>
    </SafeAreaView>
  );
}
