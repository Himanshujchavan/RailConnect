import { Routes, Route, useLocation } from "react-router-dom";
import { AnimatePresence } from "framer-motion";
import { Toaster } from "sonner";

import Navbar from "./components/layout/Navbar";
import Footer from "./components/layout/Footer";
import PageTransition from "./components/ui/PageTransition";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import About from "./pages/About";
import Contact from "./pages/Contact";
import SearchResults from "./pages/SearchResults";
import TrainDetails from "./pages/TrainDetails";
import PassengerDetails from "./pages/PassengerDetails";
import BookingReview from "./pages/BookingReview";
import Payment from "./pages/Payment";
import BookingSuccess from "./pages/BookingSuccess";
import PNRStatus from "./pages/PNRStatus";
import Dashboard from "./pages/Dashboard";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import FeaturePage from "./pages/FeaturePage";
import NotFound from "./pages/NotFound";

// Pages that render their own header/nav (no shared chrome)
const BARE_PAGES = ["/login", "/register", "/forgot-password", "/reset-password", "/success", "/booking/success", "/dashboard", "/admin"];

export default function App() {
  const location = useLocation();
  const bare = BARE_PAGES.some((path) => location.pathname === path || location.pathname.startsWith(`${path}/`));

  return (
    <div className="min-h-screen flex flex-col">
      <Toaster richColors position="top-center" />
      {!bare && <Navbar />}

      <main className="flex-1 flex flex-col">
        <AnimatePresence mode="wait">
          <Routes location={location} key={location.pathname}>
            <Route path="/" element={<PageTransition><Home /></PageTransition>} />
            <Route path="/login" element={<PageTransition><Login /></PageTransition>} />
            <Route path="/register" element={<PageTransition><Register /></PageTransition>} />
            <Route path="/forgot-password" element={<PageTransition><ForgotPassword /></PageTransition>} />
            <Route path="/reset-password" element={<PageTransition><ResetPassword /></PageTransition>} />
            <Route path="/about" element={<PageTransition><About /></PageTransition>} />
            <Route path="/contact" element={<PageTransition><Contact /></PageTransition>} />
            <Route path="/search" element={<PageTransition><SearchResults /></PageTransition>} />
            <Route path="/search/result" element={<PageTransition><SearchResults /></PageTransition>} />
            <Route path="/train" element={<PageTransition><TrainDetails /></PageTransition>} />
            <Route path="/train/:id" element={<PageTransition><TrainDetails /></PageTransition>} />
            <Route path="/booking/seat" element={<PageTransition><TrainDetails /></PageTransition>} />
            <Route path="/passengers" element={<PageTransition><PassengerDetails /></PageTransition>} />
            <Route path="/booking/passenger" element={<PageTransition><PassengerDetails /></PageTransition>} />
            <Route path="/review" element={<PageTransition><BookingReview /></PageTransition>} />
            <Route path="/booking/review" element={<PageTransition><BookingReview /></PageTransition>} />
            <Route path="/payment" element={<PageTransition><Payment /></PageTransition>} />
            <Route path="/booking/payment" element={<PageTransition><Payment /></PageTransition>} />
            <Route path="/success" element={<PageTransition><BookingSuccess /></PageTransition>} />
            <Route path="/booking/success" element={<PageTransition><BookingSuccess /></PageTransition>} />
            <Route path="/pnr" element={<PageTransition><PNRStatus /></PageTransition>} />
            <Route path="/dashboard" element={<PageTransition><Dashboard /></PageTransition>} />
            <Route path="/journey" element={<PageTransition><FeaturePage pageKey="journey" /></PageTransition>} />
            <Route path="/journey/:id" element={<PageTransition><FeaturePage pageKey="journey-detail" /></PageTransition>} />
            <Route path="/booking/history" element={<PageTransition><FeaturePage pageKey="history" /></PageTransition>} />
            <Route path="/wallet" element={<PageTransition><FeaturePage pageKey="wallet" /></PageTransition>} />
            <Route path="/coupons" element={<PageTransition><FeaturePage pageKey="coupons" /></PageTransition>} />
            <Route path="/notification" element={<PageTransition><FeaturePage pageKey="notifications" /></PageTransition>} />
            <Route path="/profile" element={<PageTransition><FeaturePage pageKey="profile" /></PageTransition>} />
            <Route path="/profile/passenger" element={<PageTransition><FeaturePage pageKey="passengers" /></PageTransition>} />
            <Route path="/profile/address" element={<PageTransition><FeaturePage pageKey="address" /></PageTransition>} />
            <Route path="/settings" element={<PageTransition><FeaturePage pageKey="settings" /></PageTransition>} />
            <Route path="/help" element={<PageTransition><FeaturePage pageKey="help" /></PageTransition>} />
            <Route path="/reviews" element={<PageTransition><FeaturePage pageKey="reviews" /></PageTransition>} />
            <Route path="/admin" element={<PageTransition><FeaturePage pageKey="admin" /></PageTransition>} />
            <Route path="/admin/trains" element={<PageTransition><FeaturePage pageKey="admin-trains" /></PageTransition>} />
            <Route path="/admin/bookings" element={<PageTransition><FeaturePage pageKey="admin-bookings" /></PageTransition>} />
            <Route path="/admin/passengers" element={<PageTransition><FeaturePage pageKey="admin-passengers" /></PageTransition>} />
            <Route path="/admin/routes" element={<PageTransition><FeaturePage pageKey="admin-routes" /></PageTransition>} />
            <Route path="/admin/reports" element={<PageTransition><FeaturePage pageKey="admin-reports" /></PageTransition>} />
            <Route path="/admin/analytics" element={<PageTransition><FeaturePage pageKey="admin-analytics" /></PageTransition>} />
            <Route path="*" element={<PageTransition><NotFound /></PageTransition>} />
          </Routes>
        </AnimatePresence>
      </main>

      {!bare && <Footer />}
    </div>
  );
}
