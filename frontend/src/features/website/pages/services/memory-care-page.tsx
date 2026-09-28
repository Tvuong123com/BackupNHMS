import {
  ServiceHero,
  WhatWeProvide,
  OurProcess,
  ServiceFaq,
  ServiceCta,
} from "../../components/services/service-detail-shared";

const ACCENT = "#7C3AED";
const ACCENT_LIGHT = "#F5F3FF";

const provides = [
  { icon: "🧠", title: "Cognitive Stimulation Therapy", desc: "Structured group activities designed to stimulate thinking, concentration, and memory — helping residents stay mentally engaged." },
  { icon: "🎵", title: "Music & Memory Program", desc: "Personalized music playlists and live music sessions proven to reduce agitation and spark long-term memory recall in dementia residents." },
  { icon: "🌿", title: "Sensory Gardens & Therapy", desc: "Specially designed outdoor spaces with fragrant plants, textured pathways, and soothing water features that calm anxiety and spark sensory engagement." },
  { icon: "🖼️", title: "Art & Creative Expression", desc: "Guided art sessions that give residents a meaningful creative outlet, reducing isolation and improving emotional well-being." },
  { icon: "🏡", title: "Secure, Homelike Environment", desc: "A dedicated memory care wing with coded exits, calming décor, familiar layouts, and wandering-safe design to keep residents safe without feeling confined." },
  { icon: "👩‍⚕️", title: "Dementia-Trained Nursing Staff", desc: "All nursing staff in our memory care wing receive specialized dementia care training, including de-escalation, validation therapy, and person-centered approaches." },
  { icon: "💬", title: "Behavior Management Plans", desc: "Individualized behavioral support plans that address challenging behaviors like sundowning, agitation, or refusal of care with non-pharmacological strategies first." },
  { icon: "👨‍👩‍👧", title: "Family Education & Support", desc: "Regular family education sessions, support groups, and 1-on-1 consultations to help families understand dementia progression and communicate more effectively." },
  { icon: "📋", title: "Person-Centered Care Plans", desc: "Each resident's care plan is built around their life story, preferences, and strengths — honoring who they are beyond their diagnosis." },
];

const process = [
  { number: "01", title: "Dementia-Specific Assessment", desc: "Upon admission, our memory care specialists conduct a comprehensive cognitive assessment using standardized tools (Mini-Cog, MMSE) to understand the type and stage of dementia and establish individualized baselines." },
  { number: "02", title: "Life Story & Preferences Gathering", desc: "We work closely with families to learn about each resident's biography, routines, preferences, and meaningful relationships. This 'life story' guides every care interaction." },
  { number: "03", title: "Individualized Care Plan Development", desc: "A person-centered care plan is built incorporating medical management, behavioral strategies, daily routine preferences, social engagement goals, and family communication preferences." },
  { number: "04", title: "Structured Daily Programming", desc: "Residents follow a consistent, predictable daily routine proven to reduce anxiety in dementia. Our activity team delivers morning energizers, afternoon cognitive sessions, and evening calm-down activities." },
  { number: "05", title: "Ongoing Monitoring & Family Updates", desc: "Nursing staff monitor for changes in cognition or behavior and adjust care proactively. Families receive regular updates and are invited to attend quarterly care conferences." },
];

const faqs = [
  { q: "What is the difference between Memory Care and regular nursing care?", a: "Memory Care is a specialized program within our facility designed specifically for residents with Alzheimer's disease, dementia, or other cognitive impairments. It features a secure environment, dementia-trained staff, and structured programming tailored to cognitive needs." },
  { q: "Is our memory care unit locked?", a: "Yes. Our memory care wing has controlled access with coded entry and exit points for resident safety. This prevents wandering while maintaining a comfortable, homelike atmosphere." },
  { q: "How do you handle sundowning and agitation?", a: "We use non-pharmacological approaches first — including redirection, calming music, sensory activities, and environmental adjustments. We believe in preserving quality of life and only use medications when absolutely necessary and physician-approved." },
  { q: "Can family members visit anytime?", a: "Yes. Family involvement is strongly encouraged. We have open visiting hours and offer family care conferences, caregiver support groups, and educational workshops to keep families engaged and informed." },
  { q: "How do you match staffing to memory care residents?", a: "Our memory care wing maintains a higher staff-to-resident ratio than standard nursing units. Staff work consistent shifts to build familiarity and trust with residents, reducing anxiety and improving care quality." },
  { q: "What happens as dementia progresses?", a: "We are equipped to care for residents through all stages of dementia, including end-stage care. We work with families to plan for transitions and can provide hospice care within our facility when the time comes." },
];

export default function MemoryCarePage() {
  return (
    <div>
      <ServiceHero
        badge="Memory Care"
        title="Memory Care Program"
        subtitle="Compassion for every stage of the journey."
        description="Our specialized memory care program creates a safe, structured, and deeply nurturing environment for residents living with Alzheimer's disease, dementia, and other cognitive conditions. We honor who each person is — not just their diagnosis."
        heroImg="https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800&q=80"
        accentColor={ACCENT}
        accentLight={ACCENT_LIGHT}
        stats={[
          { value: "Secured", label: "Memory Wing", icon: "🔒" },
          { value: "Trained", label: "Dementia Staff", icon: "🧠" },
          { value: "Daily", label: "Structured Programs", icon: "📅" },
        ]}
      />

      {/* Signs section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div className="relative rounded-3xl overflow-hidden shadow-2xl h-[420px]">
              <img src="https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=700&q=80" alt="Memory care resident" className="w-full h-full object-cover" />
              <div className="absolute inset-0 bg-gradient-to-t from-[#4c1d95]/30 to-transparent" />
            </div>
            <div>
              <h2 className="text-4xl font-bold text-[#0f1a3e]">Who Is Memory Care Right For?</h2>
              <p className="mt-4 text-gray-500 leading-relaxed">
                Our memory care program is designed for individuals who need more than standard nursing support due to cognitive impairment. Signs that a loved one may benefit include:
              </p>
              <ul className="mt-6 space-y-3">
                {[
                  "Diagnosis of Alzheimer's disease, vascular dementia, or Lewy body dementia",
                  "Significant memory loss affecting daily safety and function",
                  "Wandering, elopement risk, or getting lost in familiar settings",
                  "Sundowning, agitation, or behavioral symptoms requiring specialized support",
                  "Caregiver burnout or inability to safely manage at home",
                  "Need for 24-hour supervision and structured cognitive programming",
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
          </div>
        </div>
      </section>

      <WhatWeProvide items={provides} accentColor={ACCENT} accentLight={ACCENT_LIGHT} title="Our Memory Care Services" />
      <OurProcess steps={process} accentColor={ACCENT} />
      <ServiceFaq faqs={faqs} accentColor={ACCENT} />
      <ServiceCta
        title="Let's Talk About Memory Care"
        desc="Our memory care specialists are available for free consultations to help you understand your options and plan the next steps."
        accentColor={ACCENT}
      />
    </div>
  );
}
