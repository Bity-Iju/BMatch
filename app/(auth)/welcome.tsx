import { View, Text, SafeAreaView, Image, StatusBar, ScrollView } from "react-native";
import { useRouter } from "expo-router";
import React from "react";
import { Button } from "../../src/components/Button";
import { Footer } from "../../src/components/Footer";

export default function WelcomeScreen() {
  const router = useRouter();

  return (
    <SafeAreaView className="flex-1 bg-surface">
      <StatusBar barStyle="dark-content" />
      <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
        <View className="flex-1 px-8 justify-around py-10">
          <View className="items-center">
            <View className="w-48 h-48 bg-white rounded-3xl items-center justify-center mb-8 shadow-modern border border-border overflow-hidden">
              <Image
                source={require("../../assets/logo.png")}
                className="w-full h-full"
                resizeMode="contain"
              />
            </View>
            <Text className="text-5xl font-extrabold text-navyblue text-center tracking-tight">
              BMatch
            </Text>
            <Text className="text-xl text-muted text-center mt-4 px-2 leading-7">
              Find your life partner in a safe, secure and professional community.
            </Text>
          </View>

          <View className="w-full gap-y-4">
            <Button
              title="Get Started"
              onPress={() => router.push("/(auth)/consent")}
            />
            <Button
              title="Sign In"
              variant="outline"
              onPress={() => router.push("/(auth)/login")}
            />
          </View>
        </View>
        <Footer />
      </ScrollView>
    </SafeAreaView>
  );
}
