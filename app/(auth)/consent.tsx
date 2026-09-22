import { View, Text, SafeAreaView, ScrollView, StatusBar } from "react-native";
import { useRouter } from "expo-router";
import React from "react";
import { Button } from "../../src/components/Button";
import { Footer } from "../../src/components/Footer";

export default function ConsentScreen() {
  const router = useRouter();

  return (
    <SafeAreaView className="flex-1 bg-surface">
      <StatusBar barStyle="dark-content" />
      <View className="flex-1 px-8 py-10">
        <Text className="text-3xl font-extrabold text-navyblue mb-6 tracking-tight">
          Consent & Safety
        </Text>

        <View className="flex-1 bg-white p-6 rounded-3xl shadow-modern border border-border mb-8">
          <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
            <Text className="text-gray-700 leading-7 text-base mb-6">
              BMatch is a secure matchmaking community designed for HIV-positive individuals. By proceeding, you acknowledge and agree to the following:
            </Text>

            <View className="space-y-6">
              <ConsentItem
                number="1"
                text="You are HIV-positive and seeking a meaningful relationship within this community."
              />
              <ConsentItem
                number="2"
                text="You will provide accurate information, including your Healthcare Facility and ART ID for verification."
              />
              <ConsentItem
                number="3"
                text="BMatch is a strictly safe space; harassment or false representation will lead to immediate account termination."
              />
              <ConsentItem
                number="4"
                text="You are responsible for the security of your account and the data you share."
              />
              <ConsentItem
                number="5"
                text="The application is a tool for connection and compatibility; users must exercise caution in all interactions."
              />
            </View>

            <Text className="text-muted leading-6 text-sm mt-8 italic">
              By tapping "I Agree", you confirm that you have read and understood these professional standards.
            </Text>
          </ScrollView>
        </View>

        <View className="flex-row gap-x-4 mb-6">
          <View className="flex-1">
            <Button
              title="Decline"
              variant="outline"
              onPress={() => router.back()}
            />
          </View>
          <View className="flex-1">
            <Button
              title="I Agree"
              onPress={() => router.push("/(auth)/register")}
            />
          </View>
        </View>
        <Footer />
      </View>
    </SafeAreaView>
  );
}

function ConsentItem({ number, text }: { number: string; text: string }) {
  return (
    <View className="flex-row items-start mb-4">
      <View className="w-8 h-8 bg-blue-50 rounded-lg items-center justify-center mr-3 mt-0.5">
        <Text className="text-navyblue font-bold">{number}</Text>
      </View>
      <Text className="flex-1 text-gray-700 leading-6 text-base">{text}</Text>
    </View>
  );
}
