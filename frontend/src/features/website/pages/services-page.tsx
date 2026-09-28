import { useState } from "react";
import { Link } from "react-router";

const services = [
  {
    icon: "🏥",
    title: "Skilled Nursing Care",
    desc: "Round-the-clock nursing by licensed professionals for complex medical needs including wound care, IV therapy, and post-surgical recovery.",
    color: "bg-blue-50 border-blue-100",
    accent: "text-blue-600",
    to: "/services/skilled-nursing",
  },
  {
    icon: "🧠",
    title: "Memory Care",
    desc: "A structured, secure program for residents with Alzheimer's and dementia, featuring specialized therapies and sensory activities.",
    color: "bg-purple-50 border-purple-100",
    accent: "text-purple-600",
    to: "/services/memory-care",
  },
  {
    icon: "💪",
    title: "Physical Therapy",
    desc: "Rehabilitation programs to restore mobility, strength, and function after surgery, stroke, or injury.",
    color: "bg-green-50 border-green-100",
    accent: "text-green-600",
    to: "/services/physical-therapy",
  },
  {
    icon: "🤝",
    title: "Occupational Therapy",
    desc: "Helping residents regain independence in daily activities — from dressing and cooking to fine motor skills.",
    color: "bg-orange-50 border-orange-100",
    accent: "text-orange-600",
    to: null,
  },
  {
    icon: "🩺",
    title: "Non-Resident Nursing",
    desc: "Outpatient nursing services for community seniors who need skilled care without full-time residency.",
    color: "bg-red-50 border-red-100",
    accent: "text-red-600",
    to: null,
  },
  {
    icon: "♻️",
    title: "Hospice & Palliative Care",
    desc: "Compassionate end-of-life care focused on comfort, dignity, and emotional support for residents and families.",
    color: "bg-teal-50 border-teal-100",
    accent: "text-teal-600",
    to: null,
  },
  {
    icon: "👨‍👩‍👧",
    title: "Family Support Groups",
    desc: "Regular support meetings, counseling sessions, and educational workshops for families navigating caregiving challenges.",
    color: "bg-indigo-50 border-indigo-100",
    accent: "text-indigo-600",
    to: "/services/family-support",
  },
  {
    icon: "🍽️",
    title: "Nutrition & Dietary Care",
    desc: "Personalized meal plans crafted by registered dietitians, accommodating medical diets, allergies, and cultural preferences.",
    color: "bg-yellow-50 border-yellow-100",
    accent: "text-yellow-600",
    to: null,
  },
  {
    icon: "🎨",
    title: "Recreational Therapy",
    desc: "Art, music, gardening, and social programs that boost mental health, cognitive function, and quality of life.",
    color: "bg-pink-50 border-pink-100",
    accent: "text-pink-600",
    to: "/services/recreational-therapy",
  },
];


const faqs = [
  {
    q: "What is the admission process like?",
    a: "Our admission team begins with a comprehensive assessment of your loved one's medical, social, and personal needs. We then develop a customized care plan and guide your family through every step of the transition.",
  },
  {
    q: "Do you accept Medicare and Medicaid?",
    a: "Yes. We are Medicare and Medicaid certified. Our financial counselors are available to help you understand your coverage options and any out-of-pocket costs.",
  },
  {
    q: "Can family members visit anytime?",
    a: "We welcome family visits during our open visiting hours (8am–8pm daily). Family involvement is a key part of our care philosophy.",
  },
  {
    q: "How are care plans updated?",
    a: "Care plans are reviewed at a minimum every 90 days or whenever there is a significant change in a resident's condition. Families are always included in these reviews.",
  },
  {
    q: "What happens in a medical emergency?",
    a: "We have licensed nurses on-site 24/7. In a medical emergency, we coordinate immediately with local emergency services while keeping families informed in real time.",
  },
];

export default function ServicesPage() {
  const [openFaq, setOpenFaq] = useState<number | null>(null);

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-[#EDF2FF] to-white py-24">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <span className="inline-block bg-[#3B5BDB]/10 text-[#3B5BDB] text-sm font-semibold px-4 py-2 rounded-full mb-6">
            What We Offer
          </span>
          <h1 className="text-5xl lg:text-6xl font-bold text-[#0f1a3e] leading-tight">
            Our Care Services
          </h1>
          <p className="mt-6 text-xl text-gray-500 max-w-2xl mx-auto leading-relaxed">
            Compassionate Care Tailored To Every Senior — from skilled nursing to recreational therapy, we offer a full continuum of care.
          </p>
        </div>
      </section>

      {/* Services Grid */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {services.map((s) => {
              const inner = (
                <>
                  <div className="text-4xl mb-4">{s.icon}</div>
                  <h3 className={`text-xl font-bold text-[#0f1a3e] mb-2`}>{s.title}</h3>
                  <p className="text-gray-500 text-sm leading-relaxed">{s.desc}</p>
                  <div className={`mt-4 flex items-center gap-1.5 text-sm font-semibold ${s.accent} group-hover:gap-3 transition-all`}>
                    {s.to ? "Learn more" : "Coming soon"}
                    {s.to && (
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
                      </svg>
                    )}
                  </div>
                </>
              );
              const classes = `group rounded-2xl p-7 border-2 ${s.color} hover:shadow-xl transition-all duration-300 hover:-translate-y-1 block`;
              return s.to ? (
                <Link key={s.title} to={s.to} className={classes}>{inner}</Link>
              ) : (
                <div key={s.title} className={classes}>{inner}</div>
              );
            })}
          </div>
        </div>
      </section>

      {/* Right Care CTA */}
      <section className="py-20 bg-[#f8f9ff]">
        <div className="max-w-7xl mx-auto px-6">
          <div className="bg-gradient-to-br from-[#3B5BDB] to-[#1a2f6b] rounded-3xl overflow-hidden">
            <div className="grid grid-cols-1 lg:grid-cols-2">
              <div className="p-12 lg:p-16 flex flex-col justify-center">
                <h2 className="text-4xl font-bold text-white leading-tight">
                  Let's Find The Right Care Together
                </h2>
                <p className="mt-4 text-blue-200 text-lg leading-relaxed">
                  Not sure which service is right for your loved one? Our care coordinators are here to guide you — with no obligation.
                </p>
                <Link
                  to="/contact"
                  className="mt-8 inline-flex items-center gap-2 bg-white text-[#3B5BDB] font-bold px-8 py-4 rounded-2xl hover:bg-blue-50 transition-colors w-fit shadow-lg"
                >
                  Start the Conversation
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
                  </svg>
                </Link>
              </div>
              <div className="relative h-64 lg:h-auto">
                <img
                  src="https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=600&q=80"
                  alt="Care coordinator"
                  className="w-full h-full object-cover"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ */}
      <section className="py-20 bg-white">
        <div className="max-w-3xl mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold text-[#0f1a3e]">What Families Ask Most</h2>
            <p className="mt-4 text-gray-500">Answers to the questions we hear most often from families like yours.</p>
          </div>

          <div className="space-y-4">
            {faqs.map((faq, i) => (
              <div
                key={i}
                className={`border rounded-2xl overflow-hidden transition-all duration-300 ${
                  openFaq === i ? "border-[#3B5BDB]/30 shadow-md" : "border-gray-100"
                }`}
              >
                <button
                  onClick={() => setOpenFaq(openFaq === i ? null : i)}
                  className="w-full flex items-center justify-between p-6 text-left group"
                >
                  <span className="font-semibold text-[#0f1a3e] group-hover:text-[#3B5BDB] transition-colors">
                    {faq.q}
                  </span>
                  <svg
                    className={`w-5 h-5 text-[#3B5BDB] shrink-0 ml-4 transition-transform duration-300 ${openFaq === i ? "rotate-180" : ""}`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
                {openFaq === i && (
                  <div className="px-6 pb-6 text-gray-500 text-sm leading-relaxed border-t border-gray-50 pt-4">
                    {faq.a}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
