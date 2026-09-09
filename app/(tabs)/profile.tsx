import { View, Text, SafeAreaView, Image, TouchableOpacity, ScrollView } from "react-native";
import React from "react";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Footer } from "../../src/components/Footer";

export default function ProfileScreen() {
  const router = useRouter();

  return (
    <SafeAreaView className="flex-1 bg-surface">
      <ScrollView contentContainerStyle={{ flexGrow: 1 }} showsVerticalScrollIndicator={false}>
        <View className="flex-1">
          <View className="items-center py-10 bg-white border-b border-border shadow-sm">
            <View className="relative">
              <Image
                source={{ uri: "https://randomuser.me/api/portraits/men/1.jpg" }}
                className="w-32 h-32 rounded-full border-4 border-surface"
              />
              <TouchableOpacity className="absolute bottom-0 right-0 bg-navyblue p-2 rounded-full border-4 border-white">
                <Ionicons name="camera" size={20} color="white" />
              </TouchableOpacity>
            </View>
            <Text className="text-2xl font-extrabold text-navyblue mt-4">Samuel Okon</Text>
            <Text className="text-muted font-medium">Lagos, Ikeja • 28 years old</Text>
          </View>

          <View className="p-8">
            <Text className="text-xl font-bold text-navyblue mb-6">Account Information</Text>

            <View className="bg-white p-6 rounded-3xl shadow-modern border border-border">
              <ProfileItem icon="person-outline" label="Full Name" value="Samuel Okon" />
              <ProfileItem icon="mail-outline" label="Email" value="samuel@example.com" />
              <ProfileItem icon="call-outline" label="Phone" value="+234 801 234 5678" />
              <ProfileItem icon="business-outline" label="Facility" value="General Hospital Ikeja" />
            </View>

            <Text className="text-xl font-bold text-navyblue mt-10 mb-6">Settings</Text>

            <View className="bg-white p-2 rounded-3xl shadow-modern border border-border">
              <TouchableOpacity
                onPress={() => router.push("/(auth)/login")}
                className="flex-row items-center p-4"
              >
                <View className="w-10 h-10 bg-red-50 rounded-xl items-center justify-center">
                  <Ionicons name="log-out-outline" size={22} color="#EF4444" />
                </View>
                <Text className="flex-1 ml-4 text-red-500 font-bold text-lg">Logout</Text>
                <Ionicons name="chevron-forward" size={20} color="#94A3B8" />
              </TouchableOpacity>
            </View>
          </View>
        </View>
        <Footer />
      </ScrollView>
    </SafeAreaView>
  );
}

function ProfileItem({ icon, label, value }: { icon: any; label: string; value: string }) {
  return (
    <View className="flex-row items-center py-4 border-b border-gray-50 last:border-b-0">
      <View className="w-12 h-12 bg-blue-50 rounded-xl items-center justify-center mr-4">
        <Ionicons name={icon} size={22} color="#000080" />
      </View>
      <View className="flex-1">
        <Text className="text-muted text-xs uppercase font-bold tracking-wider">{label}</Text>
        <Text className="text-navyblue font-bold text-base mt-0.5">{value}</Text>
      </View>
    </View>
  );
}
