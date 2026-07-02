export const TRAINS = [
  {
    id: 1,
    name: "Rajdhani Express",
    num: "12951",
    dep: "16:35",
    arr: "08:15",
    depSt: "BCT",
    arrSt: "NDLS",
    dur: "15h 40m",
    rating: 4.6,
    classes: [
      { c: "3A", price: 1850, seats: 42 },
      { c: "2A", price: 2650, seats: 18 },
      { c: "1A", price: 4200, seats: 6 },
    ],
  },
  {
    id: 2,
    name: "Duronto Express",
    num: "12213",
    dep: "23:00",
    arr: "21:10",
    depSt: "BCT",
    arrSt: "NDLS",
    dur: "22h 10m",
    rating: 4.3,
    classes: [
      { c: "SL", price: 820, seats: 120 },
      { c: "3A", price: 1650, seats: 38 },
      { c: "2A", price: 2400, seats: 14 },
    ],
  },
  {
    id: 3,
    name: "August Kranti Rajdhani",
    num: "12953",
    dep: "17:15",
    arr: "09:55",
    depSt: "BCT",
    arrSt: "NZM",
    dur: "16h 40m",
    rating: 4.7,
    classes: [
      { c: "3A", price: 1920, seats: 0 },
      { c: "2A", price: 2780, seats: 22 },
      { c: "1A", price: 4450, seats: 9 },
    ],
  },
  {
    id: 4,
    name: "Mumbai Rajdhani",
    num: "12952",
    dep: "06:00",
    arr: "21:35",
    depSt: "BCT",
    arrSt: "NDLS",
    dur: "15h 35m",
    rating: 4.5,
    classes: [
      { c: "SL", price: 790, seats: 6 },
      { c: "3A", price: 1780, seats: 55 },
      { c: "2A", price: 2560, seats: 31 },
    ],
  },
];

export const POPULAR_ROUTES = [
  { from: "Mumbai", to: "Delhi", price: 1450 },
  { from: "Delhi", to: "Kolkata", price: 1290 },
  { from: "Nagpur", to: "Pune", price: 560 },
  { from: "Bengaluru", to: "Chennai", price: 480 },
];

export const TESTIMONIALS = [
  { name: "Priya Menon", role: "Frequent traveller", quote: "Booking used to eat ten minutes of my morning. Now it takes one." },
  { name: "Rohit Verma", role: "Business commuter", quote: "The live seat map is the feature every rail app should have had years ago." },
  { name: "Sana Iqbal", role: "Family planner", quote: "Booking four seats together finally isn't a guessing game." },
];

// Simulated async calls so the app demonstrates real TanStack Query usage
// without needing a live backend. Swap these for src/lib/api.js calls later.
export function fetchPopularTrains() {
  return new Promise((resolve) => setTimeout(() => resolve(TRAINS.slice(0, 3)), 400));
}

export function fetchTrainsForSearch(params) {
  return new Promise((resolve) => setTimeout(() => resolve(TRAINS), 500));
}

export function fetchRecentBookings() {
  return new Promise((resolve) =>
    setTimeout(
      () =>
        resolve([
          { pnr: "4821093765", train: "Rajdhani Express", route: "BCT → NDLS", status: "Confirmed" },
          { pnr: "3092817465", train: "Duronto Express", route: "BCT → NDLS", status: "Confirmed" },
          { pnr: "1928374650", train: "Mumbai Rajdhani", route: "BCT → NDLS", status: "Waitlisted" },
        ]),
      350
    )
  );
}
