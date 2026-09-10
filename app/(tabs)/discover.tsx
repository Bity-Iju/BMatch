import { View, Text, SafeAreaView, ScrollView, ActivityIndicator } from "react-native";
import React, { useEffect, useState } from "react";
import { ProfileCard } from "../../src/components/ProfileCard";
import { Footer } from "../../src/components/Footer";
import { supabase } from "../../src/lib/supabase";

export default function DiscoverScreen() {
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const { data: { user } } = await supabase.auth.getUser();

      const { data, error } = await supabase
        .from('profiles')
        .select('*')
        .neq('id', user?.id) // Don't show current user
        .order('created_at', { ascending: false });

      if (error) throw error;
      setUsers(data || []);
    } catch (error: any) {
      console.error("Error fetching users:", error.message);
    } finally {
      setLoading(false);
    }
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
        <View className="flex-1 px-4 py-4">
          <View className="mb-6">
            <Text className="text-2xl font-extrabold text-navyblue">Discover Matches</Text>
            <Text className="text-muted">Find compatible partners in your area</Text>
          </View>

          {users.map((user, index) => (
            <View key={user.id} className="mb-6">
              <ProfileCard user={{
                name: user.full_name,
                age: user.age_range,
                state: user.state,
                lga: user.lga,
                religion: user.religion,
                avatar: user.avatar || "https://ui-avatars.com/api/?name=" + encodeURIComponent(user.full_name) + "&background=000080&color=fff",
              }} />
            </View>
          ))}

          {users.length === 0 && (
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
