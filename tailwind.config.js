/** @type {import('tailwindcss').Config} */
module.exports = {
  // NOTE: Update this to include the paths to all of your component files.
  content: ["./app/**/*.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        primary: "#000080", // Navy Blue
        secondary: "#4D7C9E", // Dusty Blue
        darkblue: "#003366",
        oceanblue: "#0077BE",
        navyblue: "#000080",
        skyblue: "#87CEEB",
        white: "#FFFFFF",
        surface: "#F8FAFC", // Light grayish-blue for modern background
        border: "#E2E8F0",
        muted: "#64748B",
      },
      borderRadius: {
        "2xl": "1.25rem",
        "3xl": "1.5rem",
      },
      boxShadow: {
        modern: "0 4px 6px -1px rgba(0, 51, 102, 0.1), 0 2px 4px -1px rgba(0, 51, 102, 0.06)",
      },
    },
  },
  plugins: [],
};
