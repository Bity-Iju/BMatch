import "../global.css";
import { Stack } from "expo-router";
import { useEffect } from "react";
import { useFonts } from "expo-font";
import * as SplashScreen from "expo-splash-screen";
import * as Updates from "expo-updates";
import { Alert } from "react-native";

export {
  // Catch any errors thrown by the Layout component.
  ErrorBoundary,
} from "expo-router";

export const initialRouteName = "(auth)/welcome";

// Prevent the splash screen from auto-hiding before asset loading is complete.
SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
  const [loaded, error] = useFonts({
    // Add custom fonts here if needed
  });

  useEffect(() => {
    if (error) throw error;
  }, [error]);

  useEffect(() => {
    if (loaded) {
      SplashScreen.hideAsync();
      onFetchUpdateAsync();
    }
  }, [loaded]);

  const onFetchUpdateAsync = async () => {
    if (__DEV__) return; // Don't check for updates in development mode

    try {
      const update = await Updates.checkForUpdateAsync();

      if (update.isAvailable) {
        Alert.alert(
          "Update Available",
          "A new version of BMatch is available. Would you like to update now?",
          [
            { text: "Later", style: "cancel" },
            {
              text: "Update Now",
              onPress: async () => {
                await Updates.fetchUpdateAsync();
                await Updates.reloadAsync();
              }
            },
          ]
        );
      }
    } catch (error) {
      // You can also add log error to your server here
      console.error(`Error fetching latest Expo update: ${error}`);
    }
  };

  if (!loaded) {
    return null;
  }

  return <RootLayoutNav />;
}

function RootLayoutNav() {
  return (
    <Stack>
      <Stack.Screen name="(auth)" options={{ headerShown: false }} />
      <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
    </Stack>
  );
}
