import { Link } from "react-router";

// ─── Shared layout for all service detail pages ───────────────────────────────
interface ServiceHeroProps {
  badge: string;
  title: string;
  subtitle: string;
  description: string;
  heroImg: string;
  accentColor: string;
  accentLight: string;
  stats: { value: string; label: string; icon: string }[];
}

export function ServiceHero({
  badge, title, subtitle, description, heroImg, accentColor, accentLight, stats,
}: ServiceHeroProps) {
  return (
    <section className="relative overflow-hidden py-24" style={{ background: `linear-gradient(135deg, ${accentLight} 0%, #ffffff 60%)` }}>
      <div className="absolute inset-0 pointer-events-none opacity-30">
        <div className="absolute -top-32 -right-32 w-96 h-96 rounded-full blur-3xl" style={{ background: accentColor }} />
      </div>
      <div className="max-w-7xl mx-auto px-6 relative z-10 grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
        <div>
          <div className="flex items-center gap-3 mb-6">
            <Link to="/services" className="flex items-center gap-1.5 text-sm text-gray-400 hover:text-gray-600 transition-colors">
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
              All Services
            </Link>
            <span className="text-gray-300">/</span>
            <span className="text-sm font-semibold" style={{ color: accentColor }}>{badge}</span>
          </div>
          <h1 className="text-5xl lg:text-6xl font-bold text-[#0f1a3e] leading-tight">{title}</h1>
          <p className="mt-3 text-xl font-medium" style={{ color: accentColor }}>{subtitle}</p>
          <p className="mt-5 text-gray-500 text-lg leading-relaxed">{description}</p>
          <div className="mt-8 flex flex-wrap gap-4">
            <Link to="/contact" className="inline-flex items-center gap-2 px-7 py-4 text-white font-semibold rounded-2xl shadow-lg hover:-translate-y-0.5 transition-all duration-200" style={{ background: accentColor }}>
              Get Started
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </Link>
            <Link to="/admissions" className="inline-flex items-center gap-2 px-7 py-4 font-semibold rounded-2xl border-2 bg-white transition-all duration-200 hover:-translate-y-0.5" style={{ borderColor: `${accentColor}40`, color: accentColor }}>
              Learn About Admissions
            </Link>
          </div>
          <div className="mt-10 grid grid-cols-3 gap-4">
            {stats.map((s) => (
              <div key={s.label} className="text-center bg-white/70 backdrop-blur rounded-2xl p-4 border border-white shadow-sm">
                <div className="text-2xl mb-1">{s.icon}</div>
                <p className="text-xl font-bold text-[#0f1a3e]">{s.value}</p>
                <p className="text-xs text-gray-500 mt-0.5">{s.label}</p>
              </div>
            ))}
          </div>
        </div>
        <div className="relative">
          <div className="rounded-3xl overflow-hidden shadow-2xl" style={{ boxShadow: `0 30px 80px ${accentColor}30` }}>
            <img src={heroImg} alt={title} className="w-full h-[500px] object-cover" />
          </div>
          <div className="absolute -bottom-5 -left-5 bg-white rounded-2xl shadow-xl p-4 border border-gray-100">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl flex items-center justify-center" style={{ background: `${accentColor}15` }}>
                <svg className="w-5 h-5" fill="none" stroke={accentColor} viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <p className="text-sm font-bold text-[#0f1a3e]">Certified & Accredited</p>
                <p className="text-xs text-gray-400">CMS 5-Star Quality</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

interface WhatWeProvideProps {
  title?: string;
  items: { icon: string; title: string; desc: string }[];
  accentColor: string;
  accentLight: string;
}

export function WhatWeProvide({ title = "What We Provide", items, accentColor, accentLight }: WhatWeProvideProps) {
  return (
    <section className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-6">
        <div className="text-center mb-14">
          <h2 className="text-4xl font-bold text-[#0f1a3e]">{title}</h2>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {items.map((item) => (
            <div
              key={item.title}
              className="group rounded-2xl p-7 border-2 border-gray-100 hover:border-transparent hover:shadow-xl transition-all duration-300 hover:-translate-y-1"
              style={{ "--hover-bg": accentLight } as React.CSSProperties}
              onMouseEnter={(e) => (e.currentTarget.style.background = accentLight)}
              onMouseLeave={(e) => (e.currentTarget.style.background = "white")}
            >
              <div className="text-4xl mb-4">{item.icon}</div>
              <h3 className="text-lg font-bold text-[#0f1a3e] mb-2">{item.title}</h3>
              <p className="text-sm text-gray-500 leading-relaxed">{item.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

interface ProcessStepProps {
  steps: { number: string; title: string; desc: string }[];
  accentColor: string;
}

export function OurProcess({ steps, accentColor }: ProcessStepProps) {
  return (
    <section className="py-20 bg-[#f8f9ff] overflow-hidden">
      <div className="max-w-5xl mx-auto px-6">
        <div className="text-center mb-14">
          <h2 className="text-4xl font-bold text-[#0f1a3e]">Our Approach</h2>
        </div>
        <div className="relative">
          {/* Vertical Timeline Line */}
          <div className="absolute left-8 md:left-1/2 top-0 bottom-0 w-0.5 -translate-x-1/2 bg-gradient-to-b from-transparent via-gray-200 to-transparent" />
          
          <div className="space-y-12">
            {steps.map((s, i) => {
              const isEven = i % 2 === 0;
              return (
                <div
                  key={s.number}
                  className="relative flex flex-col md:flex-row items-start md:items-center justify-between w-full"
                >
                  {/* Left Column (Card for odd steps on desktop, empty for even steps) */}
                  <div className="hidden md:flex w-5/12 justify-end pr-8">
                    {!isEven ? (
                      <div className="w-full bg-white rounded-2xl p-6 border border-gray-100 hover:shadow-md transition-shadow text-right">
                        <h3 className="text-lg font-bold text-[#0f1a3e] mb-2">{s.title}</h3>
                        <p className="text-sm text-gray-500 leading-relaxed">{s.desc}</p>
                      </div>
                    ) : null}
                  </div>

                  {/* Center Column (Number Badge) */}
                  <div className="absolute left-0 md:relative md:left-auto md:mx-auto z-10 w-16 h-16 flex items-center justify-center shrink-0">
                    <div
                      className="w-16 h-16 rounded-2xl flex items-center justify-center text-white font-bold text-lg shadow-lg"
                      style={{ background: accentColor }}
                    >
                      {s.number}
                    </div>
                  </div>

                  {/* Right Column (Card for even steps on desktop, and ALL steps on mobile) */}
                  <div className="w-full md:w-5/12 pl-24 md:pl-8 flex justify-start">
                    {isEven ? (
                      <div className="w-full bg-white rounded-2xl p-6 border border-gray-100 hover:shadow-md transition-shadow text-left">
                        <h3 className="text-lg font-bold text-[#0f1a3e] mb-2">{s.title}</h3>
                        <p className="text-sm text-gray-500 leading-relaxed">{s.desc}</p>
                      </div>
                    ) : (
                      // Mobile fallback: show card on the right column for odd steps
                      <div className="md:hidden w-full bg-white rounded-2xl p-6 border border-gray-100 hover:shadow-md transition-shadow text-left">
                        <h3 className="text-lg font-bold text-[#0f1a3e] mb-2">{s.title}</h3>
                        <p className="text-sm text-gray-500 leading-relaxed">{s.desc}</p>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </section>
  );
}

interface ServiceFaqProps {
  faqs: { q: string; a: string }[];
  accentColor: string;
}

export function ServiceFaq({ faqs, accentColor }: ServiceFaqProps) {
  const [open, setOpen] = React.useState<number | null>(null);
  return (
    <section className="py-20 bg-white">
      <div className="max-w-3xl mx-auto px-6">
        <div className="text-center mb-12">
          <h2 className="text-4xl font-bold text-[#0f1a3e]">Frequently Asked Questions</h2>
        </div>
        <div className="space-y-4">
          {faqs.map((faq, i) => (
            <div
              key={i}
              className="border-2 rounded-2xl overflow-hidden transition-all duration-300 bg-white"
              style={{ borderColor: open === i ? `${accentColor}40` : "#f3f4f6" }}
            >
              <button
                onClick={() => setOpen(open === i ? null : i)}
                className="w-full flex items-center justify-between p-6 text-left"
              >
                <span className="font-semibold text-[#0f1a3e] text-sm pr-4">{faq.q}</span>
                <svg
                  className={`w-5 h-5 shrink-0 transition-transform duration-300 ${open === i ? "rotate-180" : ""}`}
                  fill="none"
                  stroke={accentColor}
                  viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              {open === i && (
                <div className="px-6 pb-6 text-gray-500 text-sm leading-relaxed border-t border-gray-50 pt-4">
                  {faq.a}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export function ServiceCta({ title, desc, accentColor }: { title: string; desc: string; accentColor: string }) {
  return (
    <section className="py-20" style={{ background: `linear-gradient(135deg, ${accentColor} 0%, #0f1a3e 100%)` }}>
      <div className="max-w-3xl mx-auto px-6 text-center">
        <h2 className="text-4xl font-bold text-white">{title}</h2>
        <p className="mt-4 text-white/70 text-lg">{desc}</p>
        <div className="mt-8 flex flex-wrap justify-center gap-4">
          <Link to="/contact" className="px-8 py-4 bg-white font-bold rounded-2xl hover:bg-gray-50 transition-all shadow-lg text-sm" style={{ color: accentColor }}>
            Schedule a Consultation
          </Link>
          <Link to="/admissions" className="px-8 py-4 bg-white/10 text-white font-semibold rounded-2xl border border-white/20 hover:bg-white/20 transition-all text-sm">
            Admission Process
          </Link>
        </div>
      </div>
    </section>
  );
}

import React from "react";
