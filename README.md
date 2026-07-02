# RailConnect

RailConnect is a Vite + React frontend for a train booking experience. It uses plain JavaScript, Tailwind CSS, and a lightweight component stack for the booking flow, account pages, dashboards, and feature screens.


## Tech Stack

React Router DOM, Axios, TanStack Query, TanStack Table, React Hook Form + Zod, Framer Motion, Lucide React, Sonner, next-themes, Recharts, React Day Picker, Embla Carousel, qrcode.react, jsPDF + html2canvas, react-loading-skeleton, clsx, class-variance-authority, tailwind-merge, and Tailwind CSS.

## Getting Started

Install dependencies and start the development server:

```bash
npm install
npm run dev
```

Open the local URL Vite prints in the terminal, usually http://localhost:5173.

## Production Build

```bash
npm run build
npm run preview
```

## Environment Setup

The app reads `VITE_API_BASE_URL` for the backend API base URL. If it is not set, the frontend falls back to `/api`.

Example:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

## Features

- Home page with route search, featured trains, testimonials, FAQ, and animated sections
- Authentication screens for login, registration, password reset, and forgot password
- Search results, train details, passenger details, review, payment, and booking success flow
- PNR status lookup and dashboard views with charts and booking tables
- Extra account and admin-style feature pages for wallet, coupons, notifications, profile, settings, help, and analytics
- Shared layout with navbar, footer, page transitions, and toast notifications

## App Structure

- `src/lib/api.js` contains the shared Axios client and bearer-token interceptor
- `src/context/AppContext.jsx` stores booking state across the flow
- `src/data/trains.js` provides mock train data that can be swapped for real API calls later
- `src/components/ui/` contains small reusable UI primitives used across the app

## Notes

- The project currently ships as a frontend-only implementation with mock data in a few screens.
- Custom UI primitives are hand-rolled rather than pulled from a generator, so they stay easy to extend inside this repo.
