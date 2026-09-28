import {
  ServiceHero,
  WhatWeProvide,
  OurProcess,
  ServiceFaq,
  ServiceCta,
} from "../../components/services/service-detail-shared";

const ACCENT = "#2563EB";
const ACCENT_LIGHT = "#EFF6FF";

const provides = [
  { icon: "🩺", title: "24/7 Nursing Coverage", desc: "Licensed RNs and LPNs on-site around the clock to monitor vital signs, administer medications, and respond to changes in condition." },
  { icon: "💊", title: "Medication Management", desc: "Complete medication administration, reconciliation, and education to ensure safe, accurate dosing aligned with physician orders." },
  { icon: "🩹", title: "Wound Care & Dressing", desc: "Advanced wound care including debridement, wound vac therapy, and pressure ulcer prevention and management." },
  { icon: "💉", title: "IV Therapy & Infusions", desc: "Intravenous antibiotic therapy, hydration, and parenteral nutrition under close nursing supervision." },
  { icon: "📋", title: "Care Plan Coordination", desc: "Interdisciplinary team meetings to review and update individualized care plans, ensuring holistic, goal-oriented care." },
  { icon: "👨‍👩‍👧", title: "Family Communication", desc: "Regular updates and open-door communication with families about their loved one's health status and care changes." },
  { icon: "🫀", title: "Cardiac & Respiratory Care", desc: "Monitoring and management of chronic conditions including CHF, COPD, diabetes, and post-MI recovery." },
  { icon: "🔬", title: "Lab & Diagnostic Support", desc: "On-site specimen collection and coordination of lab work, X-rays, and specialist consultations." },
  { icon: "🏥", title: "Post-Surgical Recovery", desc: "Specialized post-operative nursing for orthopedic, cardiac, and general surgical patients transitioning from hospital." },
];

const process = [
  { number: "01", title: "Physician Orders & Admission Assessment", desc: "Upon admission, our charge nurse conducts a head-to-toe assessment and reviews all physician orders, medications, and care protocols to establish a baseline and develop the initial care plan." },
  { number: "02", title: "Individualized Nursing Care Plan", desc: "Our nursing team — led by the Director of Nursing — creates a detailed, individualized care plan addressing every medical need, personal preference, and goal for recovery or long-term management." },
  { number: "03", title: "Daily Nursing Rounds & Monitoring", desc: "Nurses conduct routine rounds multiple times per day, tracking vital signs, wound healing, pain levels, and any changes in condition. Any concerns are escalated immediately to the attending physician." },
  { number: "04", title: "Interdisciplinary Team Collaboration", desc: "Weekly care conferences bring together nurses, therapists, dietitians, and social workers to review progress, adjust goals, and align the entire team around the resident's current needs." },
  { number: "05", title: "Discharge Planning & Transition Support", desc: "For short-term residents, our team begins discharge planning on day one — coordinating home health services, equipment, and follow-up appointments for a safe return home." },
];

const faqs = [
  { q: "What is skilled nursing care vs. basic nursing home care?", a: "Skilled nursing care involves complex medical needs requiring licensed nurses — like IV therapy, wound care, or post-surgical monitoring. Basic nursing home care focuses on daily living assistance without intensive medical intervention." },
  { q: "Does Medicare cover skilled nursing care?", a: "Yes. Medicare Part A covers short-term skilled nursing stays (up to 100 days) following a qualifying 3-day hospital stay. Coverage details vary — our financial counselors can walk you through your specific benefits." },
  { q: "How often will a doctor visit my loved one?", a: "Physicians are required to visit within the first 30 days of admission, then at least every 30–60 days thereafter, depending on the resident's condition. Our nurses communicate with physicians daily as needed." },
  { q: "What happens if my loved one's condition changes suddenly?", a: "Our nurses assess and stabilize immediately, contact the attending physician, notify family, and document changes — all within minutes. We have protocols for every emergency scenario." },
  { q: "Can my loved one bring personal medications?", a: "For safety, all medications are managed through our pharmacy system. Personal medications must be reviewed and approved by our pharmacist and physician before use." },
];

export default function SkilledNursingPage() {
  return (
    <div>
      <ServiceHero
        badge="Skilled Nursing"
        title="Skilled Nursing Care"
        subtitle="Expert clinical care, 24 hours a day."
        description="Our licensed nursing team provides hospital-level care in a compassionate, homelike environment. From complex wound care and IV therapy to post-surgical recovery and chronic disease management — we're with your loved one every step of the way."
        heroImg="https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=800&q=80"
        accentColor={ACCENT}
        accentLight={ACCENT_LIGHT}
        stats={[
          { value: "24/7", label: "RN Coverage", icon: "🏥" },
          { value: "1:6", label: "Nurse-to-Resident", icon: "👩‍⚕️" },
          { value: "100%", label: "Medicare Certified", icon: "✅" },
        ]}
      />

      {/* Who Benefits */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div>
              <h2 className="text-4xl font-bold text-[#0f1a3e]">Who Benefits from Skilled Nursing?</h2>
              <p className="mt-4 text-gray-500 leading-relaxed">
                Skilled nursing care is appropriate for individuals who require more medical support than can be provided at home but don't need to remain in a hospital. Our program is designed for:
              </p>
              <ul className="mt-6 space-y-3">
                {[
                  "Post-surgical recovery (hip/knee replacement, cardiac surgery)",
                  "Stroke rehabilitation and neurological recovery",
                  "Complex wound care and infection management",
                  "IV antibiotic therapy and infusion services",
                  "Cardiac and pulmonary disease management",
                  "Diabetic care and insulin management",
                  "Hospice and palliative support",
                  "Residents with multiple chronic conditions",
                ].map((item) => (
                  <li key={item} className="flex items-start gap-3 text-sm text-gray-700">
                    <svg className="w-4 h-4 mt-0.5 shrink-0" fill={ACCENT} viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                    {item}
                  </li>
                ))}
              </ul>
            </div>
            <div className="relative rounded-3xl overflow-hidden shadow-2xl h-[450px]">
              <img src="https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=700&q=80" alt="Skilled nurse" className="w-full h-full object-cover" />
              <div className="absolute inset-0 bg-gradient-to-t from-[#0f1a3e]/30 to-transparent" />
            </div>
          </div>
        </div>
      </section>

      <WhatWeProvide items={provides} accentColor={ACCENT} accentLight={ACCENT_LIGHT} title="Our Skilled Nursing Services" />
      <OurProcess steps={process} accentColor={ACCENT} />
      <ServiceFaq faqs={faqs} accentColor={ACCENT} />
      <ServiceCta
        title="Ready to Begin Skilled Nursing Care?"
        desc="Contact our team to learn more or begin the admission process. We're here to make this transition as smooth as possible."
        accentColor={ACCENT}
      />
    </div>
  );
}
