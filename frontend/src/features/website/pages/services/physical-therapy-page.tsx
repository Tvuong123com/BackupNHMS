import {
  ServiceHero,
  WhatWeProvide,
  OurProcess,
  ServiceFaq,
  ServiceCta,
} from "../../components/services/service-detail-shared";

const ACCENT = "#059669";
const ACCENT_LIGHT = "#ECFDF5";

const provides = [
  { icon: "🦵", title: "Orthopedic Rehabilitation", desc: "Specialized recovery protocols for hip, knee, and shoulder replacements — restoring strength, range of motion, and safe walking as quickly as possible." },
  { icon: "🧠", title: "Neurological Rehabilitation", desc: "Targeted therapy for stroke, Parkinson's disease, traumatic brain injury, and multiple sclerosis — improving mobility, coordination, and functional independence." },
  { icon: "❤️", title: "Cardiac Rehabilitation", desc: "Exercise programs and education for residents recovering from heart attack, bypass surgery, or heart failure — safely building cardiovascular endurance." },
  { icon: "🏃", title: "Gait & Balance Training", desc: "Progressive balance programs and gait retraining to reduce fall risk and help residents walk safely and confidently." },
  { icon: "💪", title: "Strength & Conditioning", desc: "Individualized resistance training and functional movement exercises to rebuild muscle strength after illness, surgery, or prolonged bedrest." },
  { icon: "🧘", title: "Pain Management", desc: "Manual therapy, therapeutic modalities (ultrasound, electrical stimulation, heat/cold), and exercise-based pain relief strategies." },
  { icon: "🏠", title: "Home Safety Assessment", desc: "Pre-discharge home evaluations to identify fall hazards and recommend modifications, ensuring a safe return home." },
  { icon: "📋", title: "Functional Goal Setting", desc: "Collaborative goal-setting with residents and families to define meaningful recovery targets — from climbing stairs to returning to gardening." },
  { icon: "🤝", title: "Caregiver Training", desc: "Education and hands-on training for family caregivers on safe transfer techniques, exercises, and fall prevention strategies." },
];

const process = [
  { number: "01", title: "Initial Physical Therapy Evaluation", desc: "On or before day two of admission, our licensed physical therapist conducts a comprehensive evaluation — assessing strength, balance, gait, pain, and functional mobility — to establish measurable baseline goals." },
  { number: "02", title: "Individualized Treatment Plan", desc: "Based on the evaluation and the resident's personal goals, the PT develops a tailored treatment plan specifying frequency, modalities, exercises, and target outcomes for recovery." },
  { number: "03", title: "Daily Therapy Sessions", desc: "Residents receive 1–2 PT sessions per day (typically 30–60 minutes each) in our fully equipped rehabilitation gym, combining active exercise with manual techniques and modalities." },
  { number: "04", title: "Weekly Progress Reviews", desc: "The PT reviews progress against goals weekly, adjusting intensity and exercises as the resident improves. Families and the interdisciplinary team are updated at care conferences." },
  { number: "05", title: "Discharge Planning & Home Program", desc: "As discharge approaches, the PT transitions to a home exercise program and coordinates any outpatient PT needs, adaptive equipment, or home safety modifications for a confident return home." },
];

const faqs = [
  { q: "How many PT sessions will my loved one receive per day?", a: "Most residents receive 1–2 physical therapy sessions per day, typically 30–60 minutes each, 5–7 days per week depending on their condition and tolerance." },
  { q: "Does Medicare cover physical therapy in a skilled nursing facility?", a: "Yes. Physical therapy in a skilled nursing facility is covered under Medicare Part A following a qualifying hospital stay. Our billing team will verify your specific coverage." },
  { q: "How long does rehabilitation typically take?", a: "Recovery timelines vary by diagnosis. Hip and knee replacement patients often stay 10–21 days. Stroke rehabilitation may require 4–12 weeks. We set individualized goals and progress at each resident's pace." },
  { q: "What is the difference between PT, OT, and Speech Therapy?", a: "Physical Therapy focuses on mobility, strength, balance, and pain. Occupational Therapy focuses on daily living skills and fine motor tasks. Speech Therapy addresses swallowing, communication, and cognitive-communication. Many residents receive all three concurrently." },
  { q: "Can family members participate in therapy sessions?", a: "Absolutely. We encourage family involvement — especially toward discharge, when caregivers learn safe assist techniques and home exercise routines." },
  { q: "What equipment is available in your rehabilitation gym?", a: "Our fully equipped gym includes parallel bars, treadmills, stationary bikes, free weights, balance boards, stairs practice, and electrical stimulation modalities." },
];

export default function PhysicalTherapyPage() {
  return (
    <div>
      <ServiceHero
        badge="Physical Therapy"
        title="Physical Therapy & Rehabilitation"
        subtitle="Restoring movement, strength, and independence."
        description="Our licensed physical therapists deliver evidence-based rehabilitation programs designed to help residents recover from surgery, illness, or injury — and regain the strength and confidence to live life fully."
        heroImg="https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=800&q=80"
        accentColor={ACCENT}
        accentLight={ACCENT_LIGHT}
        stats={[
          { value: "Daily", label: "Therapy Sessions", icon: "💪" },
          { value: "Full", label: "Rehab Gym", icon: "🏋️" },
          { value: "7 Days", label: "Per Week", icon: "📅" },
        ]}
      />

      {/* Conditions */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div>
              <h2 className="text-4xl font-bold text-[#0f1a3e]">Conditions We Treat</h2>
              <p className="mt-4 text-gray-500 leading-relaxed">
                Our rehabilitation program serves a wide range of diagnoses. Some of the most common conditions we treat include:
              </p>
              <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 gap-3">
                {[
                  ["🦵", "Hip & Knee Replacement"],
                  ["🧠", "Stroke Recovery"],
                  ["🦴", "Fractures & Bone Injuries"],
                  ["❤️", "Cardiac Surgery Recovery"],
                  ["🫁", "Pulmonary Conditions"],
                  ["🤸", "Parkinson's Disease"],
                  ["🏋️", "Deconditioning / Weakness"],
                  ["⚖️", "Balance Disorders & Falls"],
                ].map(([icon, label]) => (
                  <div key={label as string} className="flex items-center gap-3 bg-[#ECFDF5] rounded-xl px-4 py-3">
                    <span className="text-xl">{icon}</span>
                    <span className="text-sm font-medium text-[#0f1a3e]">{label}</span>
                  </div>
                ))}
              </div>
            </div>
            <div className="relative rounded-3xl overflow-hidden shadow-2xl h-[420px]">
              <img src="https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=700&q=80" alt="Physical therapy" className="w-full h-full object-cover" />
            </div>
          </div>
        </div>
      </section>

      <WhatWeProvide items={provides} accentColor={ACCENT} accentLight={ACCENT_LIGHT} title="Our PT Services" />
      <OurProcess steps={process} accentColor={ACCENT} />
      <ServiceFaq faqs={faqs} accentColor={ACCENT} />
      <ServiceCta
        title="Start Your Recovery Journey"
        desc="Our physical therapists are ready to help your loved one regain strength, mobility, and confidence."
        accentColor={ACCENT}
      />
    </div>
  );
}
