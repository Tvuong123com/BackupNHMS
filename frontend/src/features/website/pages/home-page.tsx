import { HeroSection } from "../components/home/hero-section";
import { CareOverviewSection } from "../components/home/care-overview";
import { CareJustForYouSection } from "../components/home/care-just-for-you";
import { StatsSection } from "../components/home/stats-section";
import { CommunitySection } from "../components/home/community-section";
import { CtaBannerSection } from "../components/home/cta-banner";
import { AiCareNavigator } from "../components/ai/ai-care-navigator";

export default function HomePage() {
  return (
    <>
      <HeroSection />
      <CareOverviewSection />
      <section className="py-16 bg-[#f8f9ff]">
        <div className="max-w-4xl mx-auto px-6">
          <AiCareNavigator sourceContext="home" />
        </div>
      </section>
      <CareJustForYouSection />
      <StatsSection />
      <CommunitySection />
      <CtaBannerSection />
    </>
  );
}
