import { View, Text, SafeAreaView, Image, TouchableOpacity, ScrollView, Alert, ActivityIndicator } from "react-native";
import React, { useEffect, useState } from "react";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Footer } from "../../src/components/Footer";
import { supabase } from "../../src/lib/supabase";

export default function ProfileScreen() {
  const router = useRouter();
  const [profile, setProfile] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const { data: { user } } = await supabase.auth.getUser();
      if (!user) {
        router.replace("/(auth)/login");
        return;
      }

      const { data, error } = await supabase
        .from('profiles')
        .select('*')
        .eq('id', user.id)
        .single();

      if (error) throw error;
      setProfile(data);
    } catch (error: any) {
      console.error("Error fetching profile:", error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    Alert.alert("Logout", "Are you sure you want to log out?", [
      { text: "Cancel", style: "cancel" },
      {
        text: "Logout",
        style: "destructive",
        onPress: async () => {
          await supabase.auth.signOut();
          router.replace("/(auth)/login");
        }
      }
    ]);
  };

  if (loading) {
    return (
      <View className="flex-1 justify-center items-center bg-surface">
        <ActivityIndicator size="large" color="#000080" />
      </View>
    );
  }

  return (
    <SafeAreaView className="flex-1 bg-surface">
      <ScrollView contentContainerStyle={{ flexGrow: 1 }} showsVerticalScrollIndicator={false}>
        <View className="flex-1">
          <View className="items-center py-10 bg-white border-b border-border shadow-sm">
            <View className="relative">
              <View className="w-32 h-32 rounded-full border-4 border-surface bg-blue-50 items-center justify-center">
                {profile?.avatar ? (
                  <Image source={{ uri: profile.avatar }} className="w-full h-full rounded-full" />
                ) : (
                  <Ionicons name="person" size={60} color="#4D7C9E" />
                )}
              </View>
              <TouchableOpacity className="absolute bottom-0 right-0 bg-navyblue p-2 rounded-full border-4 border-white">
                <Ionicons name="camera" size={20} color="white" />
              </TouchableOpacity>
            </View>
            <Text className="text-2xl font-extrabold text-navyblue mt-4">{profile?.full_name || "User Name"}</Text>
            <Text className="text-muted font-medium">{profile?.state}, {profile?.lga} • {profile?.age_range} years</Text>
          </View>

          <View className="p-8">
            <Text className="text-xl font-bold text-navyblue mb-6">Account Information</Text>

            <View className="bg-white p-6 rounded-3xl shadow-modern border border-border">
              <ProfileItem icon="person-outline" label="Full Name" value={profile?.full_name} />
              <ProfileItem icon="mail-outline" label="Email" value={profile?.email} />
              <ProfileItem icon="call-outline" label="Phone" value={profile?.phone} />
              <ProfileItem icon="business-outline" label="Facility" value={profile?.facility_name} />
              <ProfileItem icon="finger-print-outline" label="ART ID" value={profile?.art_id} />
              <ProfileItem icon="location-outline" label="Address" value={profile?.address} />
              <ProfileItem icon="book-outline" label="Religion" value={profile?.religion} />
              <ProfileItem icon="transgender-outline" label="Sex" value={profile?.sex} />
            </View>

            <Text className="text-xl font-bold text-navyblue mt-10 mb-6">Settings</Text>

            <View className="bg-white p-2 rounded-3xl shadow-modern border border-border">
              <TouchableOpacity
                onPress={handleLogout}
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
        <Text className="text-navyblue font-bold text-base mt-0.5">{value || "Not set"}</Text>
      </View>
    </View>
  );
}
