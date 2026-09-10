import { View, Text, TouchableOpacity, SafeAreaView, Alert, KeyboardAvoidingView, Platform, ScrollView } from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import React, { useState, useEffect } from "react";
import { Input } from "../../src/components/Input";
import { Button } from "../../src/components/Button";
import { Footer } from "../../src/components/Footer";
import { supabase } from "../../src/lib/supabase";

export default function LoginScreen() {
  const router = useRouter();
  const params = useLocalSearchParams();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (params.email && params.password) {
      setEmail(params.email as string);
      setPassword(params.password as string);
      // Auto-trigger login if coming from registration
      handleLogin(params.email as string, params.password as string);
    }
  }, [params]);

  const handleLogin = async (overrideEmail?: string, overridePassword?: string) => {
    const finalEmail = overrideEmail || email;
    const finalPassword = overridePassword || password;

    if (!finalEmail || !finalPassword) {
      Alert.alert("Error", "Please fill in all fields.");
      return;
    }
    setIsLoading(true);
    try {
      const { data, error } = await supabase.auth.signInWithPassword({
        email: finalEmail,
        password: finalPassword,
      });

      if (error) throw error;

      if (data.session) {
        // Track app access
        await supabase.from('profiles').update({
          last_seen_at: new Date().toISOString()
        }).eq('id', data.user.id);

        router.replace("/(tabs)/discover");
      }
    } catch (error: any) {
      Alert.alert("Login Error", error.message || "Invalid credentials.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <SafeAreaView className="flex-1 bg-surface">
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        className="flex-1"
      >
        <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
          <View className="flex-1 px-8 py-12">
            <View className="items-center mb-12">
              <View className="w-24 h-24 bg-white rounded-3xl items-center justify-center mb-6 shadow-modern border border-border">
                <Text className="text-navyblue text-4xl font-bold">B</Text>
              </View>
              <Text className="text-3xl font-extrabold text-navyblue">Welcome Back</Text>
              <Text className="text-muted mt-2 text-lg">Secure access to your account</Text>
            </View>

            <View className="bg-white p-6 rounded-3xl shadow-modern border border-border">
              <Input
                label="Email or Phone Number"
                placeholder="Enter your email"
                value={email}
                onChangeText={setEmail}
                autoCapitalize="none"
                keyboardType="email-address"
              />
              <Input
                label="Password"
                placeholder="••••••••"
                secureTextEntry
                value={password}
                onChangeText={setPassword}
              />

              <TouchableOpacity className="self-end mb-8">
                <Text className="text-oceanblue font-semibold">Forgot Password?</Text>
              </TouchableOpacity>

              <Button
                title="Sign In"
                onPress={handleLogin}
                loading={isLoading}
              />

              <View className="flex-row justify-center mt-8">
                <Text className="text-muted text-base">New to BMatch? </Text>
                <TouchableOpacity onPress={() => router.push("/(auth)/consent")}>
                  <Text className="text-navyblue font-bold text-base border-b border-navyblue">Join Now</Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>
          <Footer />
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
