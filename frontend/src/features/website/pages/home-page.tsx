import { HeroSection } from "../components/home/hero-section";
import { CareOverviewSection } from "../components/home/care-overview";
import { CareJustForYouSection } from "../components/home/care-just-for-you";
import { StatsSection } from "../components/home/stats-section";
import { CommunitySection } from "../components/home/community-section";
import { CtaBannerSection } from "../components/home/cta-banner";

export default function HomePage() {
  return (
    <>
      <HeroSection />
      <CareOverviewSection />
      <CareJustForYouSection />
      <StatsSection />
      <CommunitySection />
      <CtaBannerSection />
    </>
  );
}
