import { Outlet } from "react-router";
import { WebsiteNavbar } from "@/features/website/components/layout/website-navbar";
import { WebsiteFooter } from "@/features/website/components/layout/website-footer";

export function WebsiteLayout() {
  return (
    <div className="min-h-screen flex flex-col bg-white">
      <WebsiteNavbar />
      <main className="flex-1 pt-[72px]">
        <Outlet />
      </main>
      <WebsiteFooter />
    </div>
  );
}
