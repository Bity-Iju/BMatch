import { View, Text, TextInput, TextInputProps } from "react-native";
import React from "react";

interface InputProps extends TextInputProps {
  label: string;
  error?: string;
}

export const Input: React.FC<InputProps> = ({ label, error, ...props }) => {
  return (
    <View className="mb-6">
      <Text className="text-navyblue font-bold mb-2 ml-1 text-sm tracking-wide uppercase opacity-70">{label}</Text>
      <TextInput
        className={`bg-surface border-2 ${
          error ? "border-red-500" : "border-border"
        } rounded-2xl px-5 py-4 text-gray-900 text-base focus:border-navyblue`}
        placeholderTextColor="#94A3B8"
        {...props}
      />
      {error && <Text className="text-red-500 text-xs mt-1 ml-2 font-medium">{error}</Text>}
    </View>
  );
};
