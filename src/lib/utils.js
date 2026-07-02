import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs) {
  return twMerge(clsx(inputs));
}

export function formatMoney(n) {
  return "₹" + Number(n).toLocaleString("en-IN");
}

export function generatePNR() {
  return Array.from({ length: 10 }, () => Math.floor(Math.random() * 10)).join("");
}
