import { View, Text, SafeAreaView, ScrollView, Alert, TouchableOpacity } from "react-native";
import { useRouter } from "expo-router";
import React, { useState } from "react";
import { Input } from "../../src/components/Input";
import { Button } from "../../src/components/Button";
import { Footer } from "../../src/components/Footer";
import { Select } from "../../src/components/Select";
import { NIGERIA_STATES, STATE_LGA_MAPPING } from "../../src/constants/nigeria-data";
import { Ionicons } from "@expo/vector-icons";

export default function RegisterScreen() {
  const router = useRouter();
  const [step, setStep] = useState(1);
  const totalSteps = 6;

  // Form State
  const [formData, setFormData] = useState({
    fullName: "",
    age: "",
    sex: "",
    phone: "",
    email: "",
    password: "",
    religion: "",
    state: "",
    lga: "",
    address: "",
    facilityName: "",
    artId: "",
    avatar: null,
    hivStatusConfirmed: false,
    termsAccepted: false,
  });

  const isStepValid = () => {
    switch (step) {
      case 1:
        return formData.fullName && formData.age && formData.sex && formData.phone && formData.email && formData.password && formData.religion;
      case 2:
        return formData.state && formData.lga && formData.address;
      case 3:
        return formData.facilityName && formData.artId;
      case 4:
        return true; // Optional for now
      case 5:
        return formData.hivStatusConfirmed;
      case 6:
        return formData.termsAccepted;
      default:
        return false;
    }
  };

  const nextStep = () => {
    if (!isStepValid()) {
      Alert.alert("Required Fields", "Please complete all fields in this section before moving to the next step.");
      return;
    }

    if (step < totalSteps) {
      setStep(step + 1);
    } else {
      handleComplete();
    }
  };

  const prevStep = () => {
    if (step > 1) {
      setStep(step - 1);
    } else {
      router.back();
    }
  };

  const handleComplete = () => {
    Alert.alert(
      "Registration Successful",
      `Welcome to BMatch, ${formData.fullName}!\n\nA confirmation email has been sent to ${formData.email} with your login details:\n\nEmail: ${formData.email}\nPassword: ${formData.password.replace(/./g, '*')}\n\nPlease keep these safe.`,
      [
        { text: "Go to Dashboard", onPress: () => router.replace("/(tabs)/discover") }
      ]
    );
  };

  const updateField = (field: string, value: any) => {
    if (field === "state") {
      setFormData({ ...formData, state: value, lga: "" });
    } else {
      setFormData({ ...formData, [field]: value });
    }
  };

  const renderStep = () => {
    switch (step) {
      case 1:
        return (
          <View>
            <Text className="text-2xl font-extrabold text-navyblue mb-6">Personal Details</Text>
            <Input label="Full Name" placeholder="Bity Iju" value={formData.fullName} onChangeText={(v) => updateField("fullName", v)} />
            <Select
              label="Age Range"
              selectedValue={formData.age}
              onValueChange={(v) => updateField("age", v)}
              options={["15-20", "21-25", "26-30", "31-35", "36-40", "41+"]}
              placeholder="Select Age Range"
            />
            <Select
              label="Sex"
              selectedValue={formData.sex}
              onValueChange={(v) => updateField("sex", v)}
              options={["Male", "Female"]}
              placeholder="Select Sex"
            />
            <Input label="Phone Number" placeholder="+234..." keyboardType="phone-pad" value={formData.phone} onChangeText={(v) => updateField("phone", v)} />
            <Input label="Email" placeholder="bity@email.com" keyboardType="email-address" value={formData.email} onChangeText={(v) => updateField("email", v)} />
            <Input label="Password" placeholder="••••••••" secureTextEntry value={formData.password} onChangeText={(v) => updateField("password", v)} />
            <Select
              label="Religion"
              selectedValue={formData.religion}
              onValueChange={(v) => updateField("religion", v)}
              options={["Christianity", "Islam", "Traditionalist", "Others"]}
              placeholder="Select Religion"
            />
          </View>
        );
      case 2:
        return (
          <View>
            <Text className="text-2xl font-extrabold text-navyblue mb-6">Location</Text>
            <Select
              label="Nigerian State"
              selectedValue={formData.state}
              onValueChange={(v) => updateField("state", v)}
              options={NIGERIA_STATES}
              placeholder="Select State"
            />
            <Select
              label="LGA"
              selectedValue={formData.lga}
              onValueChange={(v) => updateField("lga", v)}
              options={formData.state ? STATE_LGA_MAPPING[formData.state] : []}
              placeholder={formData.state ? "Select LGA" : "Select State First"}
            />
            <Input label="Home Address" placeholder="123 Street Name" value={formData.address} onChangeText={(v) => updateField("address", v)} />
          </View>
        );
      case 3:
        return (
          <View>
            <Text className="text-2xl font-extrabold text-navyblue mb-6">Health Info</Text>
            <Input label="Healthcare Facility" placeholder="General Hospital" value={formData.facilityName} onChangeText={(v) => updateField("facilityName", v)} />
            <Input label="ART ID / Hospital Number" placeholder="GH-12345" value={formData.artId} onChangeText={(v) => updateField("artId", v)} />
          </View>
        );
      case 4:
        return (
          <View className="items-center py-10">
            <Text className="text-2xl font-extrabold text-navyblue mb-6">Profile Photo</Text>
            <TouchableOpacity className="w-48 h-40 bg-white rounded-3xl items-center justify-center border-2 border-dashed border-border shadow-modern">
              <Ionicons name="camera" size={40} color="#4D7C9E" />
              <Text className="text-dustyblue font-medium mt-2">Tap to upload</Text>
            </TouchableOpacity>
            <Text className="text-muted mt-6 text-center px-10 text-base leading-6">
              Upload a clear profile photo. Professional photos increase your chances of finding a partner.
            </Text>
          </View>
        );
      case 5:
        return (
          <View className="py-6">
            <Text className="text-2xl font-extrabold text-navyblue mb-6">HIV Status</Text>
            <TouchableOpacity
              onPress={() => updateField("hivStatusConfirmed", !formData.hivStatusConfirmed)}
              className={`flex-row items-center p-6 border-2 rounded-3xl ${formData.hivStatusConfirmed ? "border-navyblue bg-blue-50" : "border-border bg-white shadow-modern"}`}
            >
              <View className={`w-7 h-7 rounded-lg border-2 ${formData.hivStatusConfirmed ? "bg-navyblue border-navyblue" : "border-border"} items-center justify-center mr-4`}>
                {formData.hivStatusConfirmed && <Ionicons name="checkmark" size={18} color="white" />}
              </View>
              <Text className="flex-1 text-navyblue font-bold text-lg">
                I explicitly confirm that I am living with HIV.
              </Text>
            </TouchableOpacity>
            <Text className="text-muted mt-6 leading-6 text-base">
              BMatch is a safe space for people living with HIV. Your status is shared only with potential matches.
            </Text>
          </View>
        );
      case 6:
        return (
          <View className="py-6">
            <Text className="text-2xl font-extrabold text-navyblue mb-6">Terms & Safety</Text>
            <ScrollView className="h-60 bg-white border border-border p-4 rounded-3xl mb-8 shadow-modern">
              <Text className="text-muted leading-6">
                Welcome to BMatch. By using this platform, you agree to:
                {"\n\n"}1. Be respectful to all members.
                {"\n"}2. Maintain confidentiality of other members' status.
                {"\n"}3. Not share your ART ID or personal details outside secure chats.
                {"\n"}4. Verify your identity as requested.
                {"\n\n"}We prioritize your safety and privacy above all else.
              </Text>
            </ScrollView>
            <TouchableOpacity
              onPress={() => updateField("termsAccepted", !formData.termsAccepted)}
              className={`flex-row items-center p-6 border-2 rounded-3xl ${formData.termsAccepted ? "border-navyblue bg-blue-50" : "border-border bg-white shadow-modern"}`}
            >
              <View className={`w-7 h-7 rounded-lg border-2 ${formData.termsAccepted ? "bg-navyblue border-navyblue" : "border-border"} items-center justify-center mr-4`}>
                {formData.termsAccepted && <Ionicons name="checkmark" size={18} color="white" />}
              </View>
              <Text className="flex-1 text-navyblue font-bold text-lg">
                I accept the Terms and Conditions.
              </Text>
            </TouchableOpacity>
          </View>
        );
      default:
        return null;
    }
  };

  return (
    <SafeAreaView className="flex-1 bg-surface">
      <View className="flex-1 px-8 py-8">
        <View className="flex-row items-center justify-between mb-8">
          <TouchableOpacity onPress={prevStep} className="p-2 -ml-2">
            <Ionicons name="arrow-back" size={28} color="#000080" />
          </TouchableOpacity>
          <View className="items-end">
            <Text className="text-navyblue font-bold text-lg">Step {step}</Text>
            <Text className="text-muted text-xs uppercase tracking-widest">of {totalSteps}</Text>
          </View>
        </View>

        <View className="h-3 bg-white rounded-full mb-10 border border-border overflow-hidden">
          <View
            className="h-full bg-navyblue rounded-full"
            style={{ width: `${(step / totalSteps) * 100}%` }}
          />
        </View>

        <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
          {renderStep()}
        </ScrollView>

        <View className="pt-6 border-t border-border flex-row gap-x-4">
          {step > 1 && (
            <View className="flex-1">
              <Button
                title="Previous"
                onPress={prevStep}
                variant="outline"
              />
            </View>
          )}
          <View className="flex-1">
            <Button
              title={step === totalSteps ? "Create Account" : "Continue"}
              onPress={nextStep}
              variant={isStepValid() ? "primary" : "outline"}
              className={!isStepValid() ? "opacity-50" : ""}
            />
          </View>
        </View>
        <Footer />
      </View>
    </SafeAreaView>
  );
}
