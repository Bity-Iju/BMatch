import { View, Text, Platform } from "react-native";
import React from "react";
import { Picker } from "@react-native-picker/picker";

interface SelectProps {
  label: string;
  selectedValue: string;
  onValueChange: (itemValue: string) => void;
  options: string[];
  placeholder?: string;
  error?: string;
}

export const Select: React.FC<SelectProps> = ({
  label,
  selectedValue,
  onValueChange,
  options,
  placeholder = "Select an option",
  error
}) => {
  return (
    <View className="mb-6">
      <Text className="text-navyblue font-bold mb-2 ml-1 text-sm tracking-wide uppercase opacity-70">
        {label}
      </Text>
      <View className={`bg-surface border-2 ${error ? "border-red-500" : "border-border"} rounded-2xl overflow-hidden`}>
        <Picker
          selectedValue={selectedValue}
          onValueChange={onValueChange}
          style={{
            height: Platform.OS === "ios" ? 150 : 55,
            width: "100%",
            color: "#000080",
          }}
          dropdownIconColor="#000080"
        >
          <Picker.Item label={placeholder} value="" color="#94A3B8" />
          {options.map((option) => (
            <Picker.Item key={option} label={option} value={option} color="#000080" />
          ))}
        </Picker>
      </View>
      {error && <Text className="text-red-500 text-xs mt-1 ml-2 font-medium">{error}</Text>}
    </View>
  );
};
