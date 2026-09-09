import { Text, TouchableOpacity, TouchableOpacityProps, ActivityIndicator } from "react-native";
import React from "react";

interface ButtonProps extends TouchableOpacityProps {
  title: string;
  variant?: "primary" | "secondary" | "outline";
  loading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  title,
  variant = "primary",
  loading,
  className,
  ...props
}) => {
  const variants = {
    primary: "bg-navyblue",
    secondary: "bg-dustyblue",
    outline: "border-2 border-navyblue bg-transparent",
  };

  const textVariants = {
    primary: "text-white",
    secondary: "text-white",
    outline: "text-navyblue",
  };

  return (
    <TouchableOpacity
      activeOpacity={0.7}
      disabled={loading}
      className={`py-4 px-6 rounded-2xl items-center justify-center flex-row ${variants[variant]} ${className}`}
      {...props}
    >
      {loading ? (
        <ActivityIndicator color={variant === "outline" ? "#000080" : "white"} />
      ) : (
        <Text className={`text-lg font-bold ${textVariants[variant]}`}>
          {title}
        </Text>
      )}
    </TouchableOpacity>
  );
};
