import { Link } from "react-router";

const steps = [
  {
    number: "01",
    icon: "📋",
    title: "Pre-Admission Screening",
    subtitle: "Initial Assessment",
    desc: "We begin with a complimentary phone or in-person consultation to understand your loved one's medical history, current condition, daily needs, and personal preferences. Our Admission Staff will guide you through an initial eligibility screening.",
    items: ["Medical history review", "Current care needs assessment", "Insurance & financial review", "Preliminary eligibility determination"],
    color: "from-blue-500 to-blue-600",
    bg: "bg-blue-50",
    border: "border-blue-200",
  },
  {
    number: "02",
    icon: "🩺",
    title: "Clinical Assessment",
    subtitle: "Comprehensive Evaluation",
    desc: "Our Director of Nursing (DON) and clinical team conduct a thorough in-person assessment to determine the appropriate Level of Care (LOC), health status, cognitive function, and specific care requirements.",
    items: ["Physical & cognitive evaluation", "Level of Care (LOC) determination", "Medication review", "Therapy needs assessment"],
    color: "from-purple-500 to-purple-600",
    bg: "bg-purple-50",
    border: "border-purple-200",
  },
  {
    number: "03",
    icon: "📄",
    title: "Care Plan Development",
    subtitle: "Personalized Plan",
    desc: "Based on the assessment, our interdisciplinary team — including nurses, therapists, and dietitians — develops a personalized care plan tailored to your loved one's unique needs, goals, and preferences.",
    items: ["Individualized care goals", "Daily schedule & activities", "Dietary & nutrition plan", "Therapy & rehabilitation plan"],
    color: "from-green-500 to-green-600",
    bg: "bg-green-50",
    border: "border-green-200",
  },
  {
    number: "04",
    icon: "✅",
    title: "Admission & Move-In",
    subtitle: "Welcome Home",
    desc: "Once the care plan is finalized and paperwork is complete, we coordinate a smooth and comfortable move-in experience. Our team is with you every step of the way to ensure your loved one feels welcome.",
    items: ["Documentation & agreements", "Room preparation & setup", "Family orientation tour", "Introduction to care team"],
    color: "from-amber-500 to-amber-600",
    bg: "bg-amber-50",
    border: "border-amber-200",
  },
];

const requirements = [
  { icon: "🏥", title: "Medical Records", desc: "Recent hospital discharge summary, physician orders, and current medication list." },
  { icon: "📋", title: "Assessment Forms", desc: "Completed pre-admission questionnaire provided by our team." },
  { icon: "💳", title: "Insurance Information", desc: "Medicare, Medicaid, or private insurance cards and documentation." },
  { icon: "📜", title: "Legal Documents", desc: "Power of Attorney, advance directives, or living will if applicable." },
  { icon: "🆔", title: "Identification", desc: "Government-issued ID and Social Security card." },
  { icon: "👨‍👩‍👧", title: "Emergency Contacts", desc: "List of family members and emergency contact information." },
];

const faqs = [
  { q: "How long does the admission process take?", a: "The process typically takes 2–5 business days from initial contact to move-in, depending on the complexity of needs and document availability. Emergency admissions can often be arranged within 24–48 hours." },
  { q: "Can we visit before committing to admission?", a: "Absolutely. We encourage families to schedule a facility tour and meet with our care team before making any decisions. Tours are free and can be arranged within 24 hours." },
  { q: "What happens if care needs change after admission?", a: "Care plans are living documents. We review and update them at a minimum every 90 days, or immediately whenever there is a significant change in your loved one's condition." },
  { q: "Are there short-term or trial admissions available?", a: "Yes. We offer short-term rehabilitation stays, respite care for family caregivers, and in some cases trial stays so residents can experience our community before a long-term commitment." },
  { q: "What is the difference between a Level of Care and a care plan?", a: "The Level of Care (LOC) determines the intensity of nursing services required and directly impacts billing. The care plan is the day-to-day roadmap of how your loved one will be cared for, including goals, activities, and therapies." },
];

export default function AdmissionsPage() {
  return (
    <div>
      {/* Hero */}
      <section className="relative bg-gradient-to-br from-[#0f1a3e] via-[#1a2f6b] to-[#3B5BDB] py-28 overflow-hidden">
        <div className="absolute inset-0 opacity-10 pointer-events-none">
          <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-white rounded-full blur-3xl translate-x-1/3 -translate-y-1/3" />
          <div className="absolute bottom-0 left-0 w-[300px] h-[300px] bg-white rounded-full blur-3xl -translate-x-1/3 translate-y-1/3" />
        </div>
        <div className="max-w-7xl mx-auto px-6 relative z-10">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div>
              <span className="inline-block bg-white/10 text-blue-200 text-sm font-semibold px-4 py-2 rounded-full border border-white/20 mb-6">
                Begin the Journey
              </span>
              <h1 className="text-5xl lg:text-6xl font-bold text-white leading-tight">
                Admissions &
                <br />
                <span className="text-[#748ffc]">How to Get Started</span>
              </h1>
              <p className="mt-6 text-blue-200 text-lg leading-relaxed">
                We make the transition to senior care as smooth and stress-free as possible. Our 4-step process ensures your loved one receives exactly the right care from day one.
              </p>
              <div className="mt-8 flex flex-wrap gap-4">
                <Link
                  to="/contact"
                  className="px-7 py-4 bg-white text-[#3B5BDB] font-bold rounded-2xl hover:bg-blue-50 transition-all shadow-xl"
                >
                  Start the Process
                </Link>
                <a
                  href="tel:9165559000"
                  className="px-7 py-4 bg-white/10 text-white font-semibold rounded-2xl border border-white/20 hover:bg-white/20 transition-all"
                >
                  Call Us Now
                </a>
              </div>
            </div>
            {/* Stats */}
            <div className="grid grid-cols-2 gap-5">
              {[
                { value: "24-48h", label: "Emergency Admission", icon: "⚡" },
                { value: "2-5 days", label: "Standard Admission", icon: "📅" },
                { value: "Free", label: "Initial Consultation", icon: "💬" },
                { value: "100%", label: "Personalized Plans", icon: "❤️" },
              ].map((s) => (
                <div key={s.label} className="bg-white/10 backdrop-blur border border-white/20 rounded-2xl p-5 text-center">
                  <div className="text-3xl mb-2">{s.icon}</div>
                  <p className="text-2xl font-bold text-white">{s.value}</p>
                  <p className="text-xs text-blue-200 mt-1">{s.label}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* 4-Step Process */}
      <section className="py-24 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Our Process</span>
            <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e]">The 4-Step Admission Journey</h2>
            <p className="mt-4 text-gray-500 max-w-xl mx-auto">
              From first contact to move-in day — every step is guided by our experienced team.
            </p>
          </div>

          <div className="space-y-8">
            {steps.map((step, i) => (
              <div
                key={step.number}
                className={`grid grid-cols-1 lg:grid-cols-2 gap-10 items-center ${i % 2 !== 0 ? "lg:flex-row-reverse" : ""}`}
              >
                {/* Content */}
                <div className={`${i % 2 !== 0 ? "lg:order-2" : ""}`}>
                  <div className={`rounded-3xl p-8 border-2 ${step.bg} ${step.border}`}>
                    <div className="flex items-center gap-4 mb-5">
                      <div className={`w-14 h-14 rounded-2xl bg-gradient-to-br ${step.color} flex items-center justify-center text-2xl shadow-lg`}>
                        {step.icon}
                      </div>
                      <div>
                        <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">{step.subtitle}</p>
                        <h3 className="text-2xl font-bold text-[#0f1a3e]">
                          <span className="text-gray-300 mr-2">{step.number}</span>
                          {step.title}
                        </h3>
                      </div>
                    </div>
                    <p className="text-gray-600 leading-relaxed mb-5">{step.desc}</p>
                    <ul className="space-y-2">
                      {step.items.map((item) => (
                        <li key={item} className="flex items-center gap-2.5 text-sm text-gray-700">
                          <svg className="w-4 h-4 text-[#3B5BDB] shrink-0" fill="currentColor" viewBox="0 0 20 20">
                            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                          </svg>
                          {item}
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>
                {/* Image */}
                <div className={`${i % 2 !== 0 ? "lg:order-1" : ""}`}>
                  <div className="rounded-3xl overflow-hidden shadow-xl h-64 lg:h-72">
                    <img
                      src={[
                        "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=600&q=80",
                        "https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=600&q=80",
                        "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&q=80",
                        "https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=600&q=80",
                      ][i]}
                      alt={step.title}
                      className="w-full h-full object-cover"
                    />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Required Documents */}
      <section className="py-20 bg-[#f8f9ff]">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-14">
            <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Be Prepared</span>
            <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e]">Documents You'll Need</h2>
            <p className="mt-4 text-gray-500 max-w-xl mx-auto">
              Having these ready helps us expedite the admission process and ensures uninterrupted care.
            </p>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {requirements.map((r) => (
              <div key={r.title} className="bg-white rounded-2xl p-6 border border-gray-100 hover:border-[#3B5BDB]/30 hover:shadow-lg transition-all duration-300 group">
                <div className="text-3xl mb-4">{r.icon}</div>
                <h3 className="font-bold text-[#0f1a3e] mb-2 group-hover:text-[#3B5BDB] transition-colors">{r.title}</h3>
                <p className="text-sm text-gray-500 leading-relaxed">{r.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* LOC Rates Info */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div>
              <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Transparent Pricing</span>
              <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e] leading-tight">
                Understanding Levels of Care & Costs
              </h2>
              <p className="mt-5 text-gray-500 leading-relaxed">
                Our care is organized into Levels of Care (LOC), which determines the intensity of nursing services and daily support your loved one receives. Each level is priced transparently — no hidden fees.
              </p>
              <div className="mt-8 space-y-4">
                {[
                  { loc: "LOC 1", label: "Basic Assistance", desc: "Daily living support with minimal medical needs", color: "bg-blue-100 text-blue-800" },
                  { loc: "LOC 2", label: "Moderate Care", desc: "Regular nursing visits and therapy support", color: "bg-purple-100 text-purple-800" },
                  { loc: "LOC 3", label: "Enhanced Care", desc: "Frequent skilled nursing and complex medical management", color: "bg-orange-100 text-orange-800" },
                  { loc: "LOC 4", label: "Intensive Care", desc: "Around-the-clock nursing for high acuity needs", color: "bg-red-100 text-red-800" },
                ].map((l) => (
                  <div key={l.loc} className="flex items-start gap-4 p-4 rounded-2xl border border-gray-100 hover:border-[#3B5BDB]/20 transition-all">
                    <span className={`text-xs font-bold px-3 py-1.5 rounded-lg shrink-0 ${l.color}`}>{l.loc}</span>
                    <div>
                      <p className="font-semibold text-[#0f1a3e] text-sm">{l.label}</p>
                      <p className="text-xs text-gray-500 mt-0.5">{l.desc}</p>
                    </div>
                  </div>
                ))}
              </div>
              <p className="mt-5 text-sm text-gray-400 italic">
                * Exact rates are determined after assessment and vary by care level. Contact us for a personalized quote.
              </p>
            </div>
            <div className="relative">
              <div className="rounded-3xl overflow-hidden shadow-2xl shadow-blue-100">
                <img
                  src="https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=700&q=80"
                  alt="Care coordinator explaining options"
                  className="w-full h-[480px] object-cover"
                />
              </div>
              <div className="absolute -bottom-5 -left-5 bg-[#3B5BDB] text-white rounded-2xl p-5 shadow-xl">
                <p className="font-bold text-lg">Medicare & Medicaid</p>
                <p className="text-blue-200 text-sm mt-1">Accepted · We'll help you understand coverage</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ */}
      <section className="py-20 bg-[#f8f9ff]">
        <div className="max-w-3xl mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold text-[#0f1a3e]">Admissions FAQs</h2>
            <p className="mt-4 text-gray-500">Everything you need to know before starting the process.</p>
          </div>
          <AdmissionFaq faqs={faqs} />
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 bg-gradient-to-br from-[#3B5BDB] to-[#1a2f6b]">
        <div className="max-w-3xl mx-auto px-6 text-center">
          <h2 className="text-4xl font-bold text-white">Ready to Begin?</h2>
          <p className="mt-4 text-blue-200 text-lg">
            Contact our Admissions team today — we're available Monday through Friday, 8am–6pm, and by appointment on weekends.
          </p>
          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Link
              to="/contact"
              className="px-8 py-4 bg-white text-[#3B5BDB] font-bold rounded-2xl hover:bg-blue-50 transition-all shadow-lg"
            >
              Schedule a Consultation
            </Link>
            <a
              href="tel:9165559000"
              className="px-8 py-4 bg-white/10 text-white font-semibold rounded-2xl border border-white/20 hover:bg-white/20 transition-all"
            >
              (916) 555-9000
            </a>
          </div>
        </div>
      </section>
    </div>
  );
}

function AdmissionFaq({ faqs }: { faqs: { q: string; a: string }[] }) {
  const [open, setOpen] = React.useState<number | null>(null);
  return (
    <div className="space-y-4">
      {faqs.map((faq, i) => (
        <div
          key={i}
          className={`border rounded-2xl overflow-hidden bg-white transition-all duration-300 ${open === i ? "border-[#3B5BDB]/30 shadow-md" : "border-gray-200"}`}
        >
          <button
            onClick={() => setOpen(open === i ? null : i)}
            className="w-full flex items-center justify-between p-6 text-left"
          >
            <span className="font-semibold text-[#0f1a3e] text-sm pr-4">{faq.q}</span>
            <svg className={`w-5 h-5 text-[#3B5BDB] shrink-0 transition-transform duration-300 ${open === i ? "rotate-180" : ""}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
  );
}

import React from "react";
