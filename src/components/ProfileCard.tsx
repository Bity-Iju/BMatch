import { View, Text, Image, TouchableOpacity } from "react-native";
import React from "react";
import { Ionicons } from "@expo/vector-icons";

interface ProfileCardProps {
  user: {
    name: string;
    age: number;
    state: string;
    lga: string;
    religion: string;
    avatar: string;
  };
}

export const ProfileCard: React.FC<ProfileCardProps> = ({ user }) => {
  return (
    <View className="bg-white rounded-3xl shadow-lg overflow-hidden border border-gray-100">
      <View className="relative h-96 w-full bg-gray-200">
        <Image
          source={{ uri: user.avatar || "https://via.placeholder.com/400" }}
          className="h-full w-full"
          resizeMode="cover"
        />
        <View className="absolute bottom-0 left-0 right-0 p-6 bg-gradient-to-t from-black/60 to-transparent">
          <Text className="text-white text-3xl font-bold">
            {user.name}, {user.age}
          </Text>
          <Text className="text-white/90 text-lg">
            {user.state}, {user.lga}
          </Text>
          <View className="flex-row mt-2">
            <View className="bg-white/20 px-3 py-1 rounded-full">
              <Text className="text-white text-sm">{user.religion}</Text>
            </View>
          </View>
        </View>
      </View>

      <View className="flex-row justify-around py-6">
        <TouchableOpacity className="w-16 h-16 bg-gray-100 rounded-full items-center justify-center border border-gray-200">
          <Ionicons name="close" size={32} color="#4B5563" />
        </TouchableOpacity>
        <TouchableOpacity className="w-16 h-16 bg-pink-100 rounded-full items-center justify-center border border-pink-200">
          <Ionicons name="heart" size={32} color="#E91E63" />
        </TouchableOpacity>
      </View>
    </View>
  );
};
