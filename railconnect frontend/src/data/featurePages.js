export const FEATURE_PAGES = {
  wallet: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Wallet" }],
    title: "Wallet",
    subtitle: "Balance, rewards, refunds, and recharge history.",
    overview: [
      { label: "Balance", value: "₹320", note: "Ready to use" },
      { label: "Reward points", value: "1,240", note: "Earned on bookings" },
      { label: "Active coupons", value: "2", note: "Apply at checkout" },
    ],
    sections: [
      { title: "Transaction history", items: ["Refund from cancelled booking", "Ticket payment", "Wallet recharge"] },
      { title: "What you can do", items: ["Use wallet balance on payment", "Track refunds", "Redeem reward points"] },
    ],
    actions: [
      { label: "Booking history", to: "/booking/history" },
      { label: "Coupons", to: "/coupons" },
      { label: "Search train", to: "/search" },
    ],
  },
  coupons: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Coupons" }],
    title: "Coupons",
    subtitle: "Available, used, and expired offers.",
    overview: [
      { label: "Available", value: "3", note: "Ready to apply" },
      { label: "Used", value: "9", note: "Booking history" },
      { label: "Savings", value: "₹1,850", note: "This quarter" },
    ],
    sections: [
      { title: "Available coupons", items: ["RAIL100 - ₹100 off", "FESTIVE20 - 20% off on selected routes", "WEEKEND50 - Flat ₹50 off"] },
      { title: "Apply flow", items: ["Enter coupon code", "See discount instantly", "Proceed to payment"] },
    ],
    actions: [
      { label: "Apply in payment", to: "/booking/payment" },
      { label: "Wallet", to: "/wallet" },
      { label: "Search train", to: "/search" },
    ],
  },
  notifications: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Notifications" }],
    title: "Notifications",
    subtitle: "Booking confirmation, refunds, reminders, and offers.",
    overview: [
      { label: "Unread", value: "4", note: "Platform updates" },
      { label: "Booking alerts", value: "12", note: "This month" },
      { label: "Refund alerts", value: "3", note: "Auto credit" },
    ],
    sections: [
      { title: "Live updates", items: ["Booking confirmation", "Refund processed", "Train delay mock alert", "Platform change mock alert"] },
      { title: "Notification settings", items: ["Email", "SMS", "Push", "Offers"] },
    ],
    actions: [
      { label: "Settings", to: "/settings" },
      { label: "Booking history", to: "/booking/history" },
      { label: "Dashboard", to: "/dashboard" },
    ],
  },
  profile: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Profile" }],
    title: "Profile",
    subtitle: "Personal details, preferences, and security.",
    overview: [
      { label: "Name", value: "Aarav Sharma", note: "Verified user" },
      { label: "Language", value: "English", note: "Preferred" },
      { label: "Travel profile", value: "Complete", note: "Saved passengers ready" },
    ],
    sections: [
      { title: "Profile tabs", items: ["Personal information", "Travel preferences", "Emergency contact", "Notification settings", "Security"] },
      { title: "Connected pages", items: ["Saved passengers", "Address book", "Settings"] },
    ],
    actions: [
      { label: "Saved passengers", to: "/profile/passenger" },
      { label: "Address book", to: "/profile/address" },
      { label: "Settings", to: "/settings" },
    ],
  },
  passengers: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Saved passengers" }],
    title: "Saved passengers",
    subtitle: "Add, edit, and reuse traveller details.",
    overview: [
      { label: "Saved travellers", value: "4", note: "Fast autofill" },
      { label: "Primary traveller", value: "Himanshu", note: "Linked profile" },
      { label: "Last used", value: "Father", note: "For family trip" },
    ],
    sections: [
      { title: "CRUD actions", items: ["Add passenger", "Edit passenger", "Delete passenger", "Set relationship"] },
      { title: "Passenger details", items: ["Age", "Gender", "Berth preference", "ID proof mock"] },
    ],
    actions: [
      { label: "Profile", to: "/profile" },
      { label: "Booking review", to: "/booking/review" },
      { label: "Search train", to: "/search" },
    ],
  },
  address: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Address book" }],
    title: "Address book",
    subtitle: "Home, office, and other addresses.",
    overview: [
      { label: "Saved addresses", value: "3", note: "Autofill ready" },
      { label: "Primary", value: "Home", note: "Default shipping" },
      { label: "Office", value: "Yes", note: "Stored" },
    ],
    sections: [
      { title: "Address categories", items: ["Home", "Office", "Other"] },
      { title: "Actions", items: ["Add", "Edit", "Delete"] },
    ],
    actions: [
      { label: "Profile", to: "/profile" },
      { label: "Settings", to: "/settings" },
      { label: "Dashboard", to: "/dashboard" },
    ],
  },
  settings: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Settings" }],
    title: "Settings",
    subtitle: "Theme, language, notifications, privacy, and account actions.",
    overview: [
      { label: "Theme", value: "Light", note: "Toggle in navbar" },
      { label: "Language", value: "English", note: "Change any time" },
      { label: "Notifications", value: "Enabled", note: "Email + SMS" },
    ],
    sections: [
      { title: "Available settings", items: ["Theme", "Language", "Notifications", "Privacy", "Delete account", "Logout"] },
      { title: "Security", items: ["Session expiry handling", "Auto token refresh", "Protected routes"] },
    ],
    actions: [
      { label: "Profile", to: "/profile" },
      { label: "Login", to: "/login" },
      { label: "Home", to: "/" },
    ],
  },
  help: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Help center" }],
    title: "Help center",
    subtitle: "FAQ, raise a ticket, support chat, and policy links.",
    overview: [
      { label: "FAQ", value: "24/7", note: "Self-serve help" },
      { label: "Support chat", value: "Mock", note: "Instant response" },
      { label: "Response SLA", value: "< 2h", note: "Priority cases" },
    ],
    sections: [
      { title: "Help topics", items: ["FAQ", "Raise ticket", "Support chat", "Contact", "Terms", "Privacy"] },
      { title: "Common fixes", items: ["Payment failed", "PNR not found", "Ticket cancellation", "Refund pending"] },
    ],
    actions: [
      { label: "Contact", to: "/contact" },
      { label: "PNR status", to: "/pnr" },
      { label: "Home", to: "/" },
    ],
  },
  reviews: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Reviews" }],
    title: "Reviews",
    subtitle: "Rate journeys, write feedback, and view past reviews.",
    overview: [
      { label: "Average rating", value: "4.6", note: "From recent trips" },
      { label: "Reviews written", value: "18", note: "All time" },
      { label: "Photos uploaded", value: "7", note: "Optional" },
    ],
    sections: [
      { title: "Review actions", items: ["Rate journey", "Write review", "View reviews", "Upload images optional"] },
      { title: "Use cases", items: ["Post-booking feedback", "Coach cleanliness", "Platform experience"] },
    ],
    actions: [
      { label: "Booking history", to: "/booking/history" },
      { label: "Journey", to: "/journey" },
      { label: "Dashboard", to: "/dashboard" },
    ],
  },
  journey: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Journey" }],
    title: "Journey",
    subtitle: "Upcoming, current, past, and cancelled trips.",
    overview: [
      { label: "Upcoming", value: "1", note: "Next trip" },
      { label: "Current", value: "0", note: "Not on board" },
      { label: "Past", value: "12", note: "History available" },
    ],
    sections: [
      { title: "Journey cards", items: ["Countdown", "Platform", "Coach", "Seat", "Delay mock"] },
      { title: "Journey details", items: ["Stops", "Current location mock", "Coach position", "Passenger details"] },
    ],
    actions: [
      { label: "Booking history", to: "/booking/history" },
      { label: "PNR status", to: "/pnr" },
      { label: "Search train", to: "/search" },
    ],
  },
  "journey-detail": {
    crumbs: [{ label: "Home", to: "/" }, { label: "Journey", to: "/journey" }, { label: "Details" }],
    title: "Journey details",
    subtitle: "Live itinerary and platform information.",
    overview: [
      { label: "Current station", value: "Nashik", note: "Mock live update" },
      { label: "Delay", value: "12 min", note: "Current status" },
      { label: "Platform", value: "4", note: "Mock data" },
    ],
    sections: [
      { title: "Stops", items: ["Mumbai", "Nashik", "Bhusawal", "Nagpur", "Raipur", "Bilaspur", "Howrah"] },
      { title: "Trip controls", items: ["Coach position", "Passenger details", "Download ticket", "Share booking"] },
    ],
    actions: [
      { label: "Journey", to: "/journey" },
      { label: "Booking history", to: "/booking/history" },
      { label: "PNR status", to: "/pnr" },
    ],
  },
  history: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Booking history" }],
    title: "Booking history",
    subtitle: "Upcoming, completed, cancelled, and refunded bookings.",
    overview: [
      { label: "Upcoming", value: "2", note: "Active trips" },
      { label: "Completed", value: "10", note: "Past journeys" },
      { label: "Refunded", value: "1", note: "Wallet credited" },
    ],
    sections: [
      { title: "Filters", items: ["Search", "Filter", "Sort"] },
      { title: "Actions", items: ["Download ticket", "Cancel booking", "Rebook"] },
    ],
    actions: [
      { label: "Search train", to: "/search" },
      { label: "PNR status", to: "/pnr" },
      { label: "Wallet", to: "/wallet" },
    ],
  },
  admin: {
    crumbs: [{ label: "Home", to: "/" }, { label: "Admin" }],
    title: "Admin dashboard",
    subtitle: "Revenue, bookings, passengers, refunds, and occupancy.",
    overview: [
      { label: "Revenue", value: "₹4.2L", note: "This month" },
      { label: "Bookings", value: "1,284", note: "Rolling 30 days" },
      { label: "Occupancy", value: "78%", note: "Average" },
    ],
    sections: [
      { title: "Admin modules", items: ["Train management", "Booking management", "Passenger management", "Route management", "Reports", "Analytics"] },
      { title: "Charts", items: ["Daily revenue", "Monthly revenue", "Occupancy", "Top routes"] },
    ],
    actions: [
      { label: "Train management", to: "/admin/trains" },
      { label: "Reports", to: "/admin/reports" },
      { label: "Analytics", to: "/admin/analytics" },
    ],
  },
  "admin-trains": {
    crumbs: [{ label: "Home", to: "/" }, { label: "Admin", to: "/admin" }, { label: "Train management" }],
    title: "Train management",
    subtitle: "Add, edit, delete, coach, route, and schedule management.",
    overview: [
      { label: "Active trains", value: "128", note: "Managed inventory" },
      { label: "Routes", value: "42", note: "Configured" },
      { label: "Schedules", value: "96", note: "Live entries" },
    ],
    sections: [
      { title: "CRUD", items: ["Add train", "Edit train", "Delete train", "Coach management", "Route management", "Schedule management"] },
      { title: "Operational views", items: ["Seat map", "Fare table", "Availability", "Running days"] },
    ],
    actions: [
      { label: "Admin dashboard", to: "/admin" },
      { label: "Booking management", to: "/admin/bookings" },
      { label: "Route management", to: "/admin/routes" },
    ],
  },
  "admin-bookings": {
    crumbs: [{ label: "Home", to: "/" }, { label: "Admin", to: "/admin" }, { label: "Booking management" }],
    title: "Booking management",
    subtitle: "View bookings, cancel, refund, search, and inspect details.",
    overview: [
      { label: "Bookings", value: "1,284", note: "Visible in the panel" },
      { label: "Refunds", value: "38", note: "Processed this week" },
      { label: "Cancellations", value: "22", note: "Pending review" },
    ],
    sections: [
      { title: "Actions", items: ["View bookings", "Search", "Refund", "Cancel", "Booking details"] },
      { title: "Exports", items: ["CSV", "PDF", "Audit logs"] },
    ],
    actions: [
      { label: "Admin dashboard", to: "/admin" },
      { label: "Passenger management", to: "/admin/passengers" },
      { label: "Reports", to: "/admin/reports" },
    ],
  },
  "admin-passengers": {
    crumbs: [{ label: "Home", to: "/" }, { label: "Admin", to: "/admin" }, { label: "Passenger management" }],
    title: "Passenger management",
    subtitle: "Users, passengers, verification, and travel statistics.",
    overview: [
      { label: "Users", value: "8,420", note: "Registered" },
      { label: "Passengers", value: "12,018", note: "Saved travellers" },
      { label: "Verification", value: "96%", note: "Complete" },
    ],
    sections: [
      { title: "Management areas", items: ["Users", "Passengers", "Verification", "Travel statistics"] },
      { title: "Admin use cases", items: ["Search profile", "Audit booking history", "Resolve support tickets"] },
    ],
    actions: [
      { label: "Admin dashboard", to: "/admin" },
      { label: "Route management", to: "/admin/routes" },
      { label: "Analytics", to: "/admin/analytics" },
    ],
  },
  "admin-routes": {
    crumbs: [{ label: "Home", to: "/" }, { label: "Admin", to: "/admin" }, { label: "Route management" }],
    title: "Route management",
    subtitle: "Stations, routes, stops, distance, and travel time.",
    overview: [
      { label: "Stations", value: "211", note: "Known stops" },
      { label: "Routes", value: "42", note: "Configured" },
      { label: "Travel time", value: "Optimized", note: "Mock data" },
    ],
    sections: [
      { title: "Route data", items: ["Stations", "Routes", "Stops", "Distance", "Travel time"] },
      { title: "Connected modules", items: ["Train management", "Reports", "Analytics"] },
    ],
    actions: [
      { label: "Admin dashboard", to: "/admin" },
      { label: "Train management", to: "/admin/trains" },
      { label: "Reports", to: "/admin/reports" },
    ],
  },
  "admin-reports": {
    crumbs: [{ label: "Home", to: "/" }, { label: "Admin", to: "/admin" }, { label: "Reports" }],
    title: "Reports",
    subtitle: "Revenue, passengers, occupancy, refunds, export PDF or CSV.",
    overview: [
      { label: "Revenue report", value: "Ready", note: "Exportable" },
      { label: "Passenger report", value: "Ready", note: "Filtered" },
      { label: "Refund report", value: "Ready", note: "Audited" },
    ],
    sections: [
      { title: "Available reports", items: ["Revenue", "Passenger", "Occupancy", "Refund"] },
      { title: "Exports", items: ["PDF", "CSV"] },
    ],
    actions: [
      { label: "Admin dashboard", to: "/admin" },
      { label: "Analytics", to: "/admin/analytics" },
      { label: "Booking management", to: "/admin/bookings" },
    ],
  },
  "admin-analytics": {
    crumbs: [{ label: "Home", to: "/" }, { label: "Admin", to: "/admin" }, { label: "Analytics" }],
    title: "Analytics",
    subtitle: "Top routes, peak booking time, occupancy, fare, cancellations, revenue growth.",
    overview: [
      { label: "Peak time", value: "7 PM", note: "Highest demand" },
      { label: "Occupancy", value: "78%", note: "Average" },
      { label: "Cancellation", value: "4.1%", note: "Stable" },
    ],
    sections: [
      { title: "Charts", items: ["Top routes", "Peak booking time", "Average occupancy", "Average fare", "Cancellation %", "Revenue growth"] },
      { title: "Decision support", items: ["Capacity planning", "Dynamic pricing", "Promotion analysis"] },
    ],
    actions: [
      { label: "Admin dashboard", to: "/admin" },
      { label: "Reports", to: "/admin/reports" },
      { label: "Train management", to: "/admin/trains" },
    ],
  },
};
