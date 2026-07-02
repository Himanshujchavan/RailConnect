import { createContext, useContext, useState, useMemo } from "react";

const AppContext = createContext(null);

const todayISO = () => new Date().toISOString().slice(0, 10);

export function AppProvider({ children }) {
  const [search, setSearch] = useState({
    from: "Mumbai",
    to: "Delhi",
    date: todayISO(),
    pax: 1,
    cls: "SL",
  });
  const [selectedTrain, setSelectedTrain] = useState(null);
  const [selectedClass, setSelectedClass] = useState(null);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [passengers, setPassengers] = useState([]);
  const [fare, setFare] = useState({ base: 1450, tax: 72, coupon: 0 });
  const [pnr, setPnr] = useState(null);
  const [user] = useState({ name: "Aarav Sharma", email: "aarav.sharma@email.com" });

  const value = useMemo(
    () => ({
      search, setSearch,
      selectedTrain, setSelectedTrain,
      selectedClass, setSelectedClass,
      selectedSeats, setSelectedSeats,
      passengers, setPassengers,
      fare, setFare,
      pnr, setPnr,
      user,
    }),
    [search, selectedTrain, selectedClass, selectedSeats, passengers, fare, pnr, user]
  );

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useApp() {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error("useApp must be used within AppProvider");
  return ctx;
}
