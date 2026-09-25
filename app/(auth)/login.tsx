import { View, Text, TouchableOpacity, SafeAreaView, Alert, KeyboardAvoidingView, Platform, ScrollView, Image } from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import React, { useState, useEffect } from "react";
import { Input } from "../../src/components/Input";
import { Button } from "../../src/components/Button";
import { Footer } from "../../src/components/Footer";
import { supabase } from "../../src/lib/supabase";
import * as SecureStore from "expo-secure-store";

export default function LoginScreen() {
  const router = useRouter();
  const params = useLocalSearchParams();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);

  useEffect(() => {
    if (params.email && params.password) {
      setEmail(params.email as string);
      setPassword(params.password as string);
      setRememberMe(true);
      // Auto-trigger login if coming from registration
      handleLogin(params.email as string, params.password as string);
      return;
    }

    const restoreCredentials = async () => {
      const savedEmail = await SecureStore.getItemAsync("bmatch_saved_email");
      const savedPassword = await SecureStore.getItemAsync("bmatch_saved_password");
      if (savedEmail && savedPassword) {
        setEmail(savedEmail);
        setPassword(savedPassword);
        setRememberMe(true);
        await handleLogin(savedEmail, savedPassword);
      }
    };
    restoreCredentials();
  }, [params]);

  const handleLogin = async (overrideEmail?: string, overridePassword?: string) => {
    const finalEmail = (overrideEmail || email).trim().toLowerCase();
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
        if (rememberMe) {
          await SecureStore.setItemAsync("bmatch_saved_email", finalEmail);
          await SecureStore.setItemAsync("bmatch_saved_password", finalPassword);
        } else {
          await SecureStore.deleteItemAsync("bmatch_saved_email");
          await SecureStore.deleteItemAsync("bmatch_saved_password");
        }

        // Track app access
        await supabase.from('profiles').update({
          last_seen_at: new Date().toISOString()
        }).eq('id', data.user.id);

        router.replace("/(tabs)/discover");
      }
    } catch (error: any) {
      const message = error?.code === "invalid_credentials" || error?.message?.toLowerCase().includes("invalid login credentials")
        ? "The email or password is incorrect. Check your details or use Forgot Password to reset it."
        : error?.message || "Unable to sign in. Please try again.";
      Alert.alert("Unable to sign in", message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgotPassword = async () => {
    const normalizedEmail = email.trim().toLowerCase();
    if (!normalizedEmail) {
      Alert.alert("Enter your email", "Enter your account email first, then tap Forgot Password.");
      return;
    }

    setIsLoading(true);
    try {
      const { error } = await supabase.auth.resetPasswordForEmail(normalizedEmail);
      if (error) throw error;
      Alert.alert("Reset email sent", "Check your inbox for a secure password reset link.");
    } catch (error: any) {
      Alert.alert("Could not send reset email", error?.message || "Please try again.");
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
              <View className="w-24 h-24 bg-white rounded-3xl items-center justify-center mb-6 shadow-modern border border-border overflow-hidden">
                <Image source={require("../../assets/logo.png")} className="w-20 h-20" resizeMode="contain" />
              </View>
              <Text className="text-3xl font-extrabold text-navyblue">Welcome back to BMatch</Text>
              <Text className="text-muted mt-2 text-center">Sign in to continue to your professional community</Text>
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

              <TouchableOpacity className="self-end mb-8" onPress={handleForgotPassword}>
                <Text className="text-oceanblue font-semibold">Forgot Password?</Text>
              </TouchableOpacity>

              <TouchableOpacity
                className="flex-row items-center mb-6"
                onPress={() => setRememberMe((value) => !value)}
                accessibilityRole="checkbox"
                accessibilityState={{ checked: rememberMe }}
              >
                <View className={`w-5 h-5 rounded border items-center justify-center ${rememberMe ? "bg-navyblue border-navyblue" : "border-gray-300"}`}>
                  {rememberMe && <Text className="text-white text-xs font-bold">✓</Text>}
                </View>
                <Text className="text-gray-600 ml-3">Remember my email and password on this device</Text>
              </TouchableOpacity>

              <Button
                title="Sign In"
                onPress={() => handleLogin()}
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
