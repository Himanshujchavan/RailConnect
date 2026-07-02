/** @type {import('tailwindcss').Config} */
export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        primary: { DEFAULT: "#2563EB", dark: "#1D4ED8" },
        secondary: "#0F172A",
        accent: "#22C55E",
        warning: "#F59E0B",
        danger: "#EF4444",
        bg: "#F8FAFC",
        card: "#FFFFFF",
        deep: "#020617",
        border: "#E2E8F0",
        muted: "#64748B",
      },
      fontFamily: {
        display: ["Poppins", "sans-serif"],
        body: ["Inter", "sans-serif"],
        mono: ["JetBrains Mono", "monospace"],
      },
      borderRadius: {
        card: "14px",
      },
      boxShadow: {
        card: "0 1px 2px rgba(15,23,42,.04), 0 8px 24px -12px rgba(15,23,42,.12)",
        lift: "0 20px 40px -16px rgba(15,23,42,.22)",
      },
      keyframes: {
        shimmer: { "0%": { backgroundPosition: "100% 0" }, "100%": { backgroundPosition: "-100% 0" } },
      },
      animation: { shimmer: "shimmer 1.4s infinite" },
    },
  },
  plugins: [],
};
