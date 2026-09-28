import { createBrowserRouter } from "react-router";
import { adminRoutes } from "./admin-routes";
import { websiteRoutes } from "./website-routes";
import { LoginPage } from "@/features/auth/pages/login-page";
import { ResetPasswordPage } from "@/features/auth/pages/reset-password-page";

export const router = createBrowserRouter([
  // Public website (/, /about, /services, /activities, /contact)
  websiteRoutes,
  // Auth
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/forgot-password",
    element: <ResetPasswordPage />,
  },
  // Admin app
  adminRoutes,
]);
