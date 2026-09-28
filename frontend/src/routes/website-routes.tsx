import { type RouteObject } from "react-router";
import { WebsiteLayout } from "@/layouts/website-layout";
import HomePage from "@/features/website/pages/home-page";
import AboutPage from "@/features/website/pages/about-page";
import ServicesPage from "@/features/website/pages/services-page";
import ActivitiesPage from "@/features/website/pages/activities-page";
import ContactPage from "@/features/website/pages/contact-page";
import AdmissionsPage from "@/features/website/pages/admissions-page";
import FacilitiesPage from "@/features/website/pages/facilities-page";
import TeamPage from "@/features/website/pages/team-page";
// Service detail pages
import SkilledNursingPage from "@/features/website/pages/services/skilled-nursing-page";
import MemoryCarePage from "@/features/website/pages/services/memory-care-page";
import PhysicalTherapyPage from "@/features/website/pages/services/physical-therapy-page";
import RecreationalTherapyPage from "@/features/website/pages/services/recreational-therapy-page";
import FamilySupportPage from "@/features/website/pages/services/family-support-page";

export const websiteRoutes: RouteObject = {
  element: <WebsiteLayout />,
  children: [
    { path: "/", element: <HomePage /> },
    { path: "/about", element: <AboutPage /> },
    { path: "/services", element: <ServicesPage /> },
    { path: "/services/skilled-nursing", element: <SkilledNursingPage /> },
    { path: "/services/memory-care", element: <MemoryCarePage /> },
    { path: "/services/physical-therapy", element: <PhysicalTherapyPage /> },
    { path: "/services/recreational-therapy", element: <RecreationalTherapyPage /> },
    { path: "/services/family-support", element: <FamilySupportPage /> },
    { path: "/activities", element: <ActivitiesPage /> },
    { path: "/contact", element: <ContactPage /> },
    { path: "/admissions", element: <AdmissionsPage /> },
    { path: "/facilities", element: <FacilitiesPage /> },
    { path: "/team", element: <TeamPage /> },
  ],
};
