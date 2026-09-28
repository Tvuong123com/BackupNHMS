import {
  ServiceHero,
  WhatWeProvide,
  OurProcess,
  ServiceFaq,
  ServiceCta,
} from "../../components/services/service-detail-shared";


const ACCENT = "#DC2626";
const ACCENT_LIGHT = "#FEF2F2";

const provides = [
  { icon: "👥", title: "Family Support Groups", desc: "Regularly scheduled peer support groups led by a licensed social worker where family caregivers share experiences, coping strategies, and mutual encouragement." },
  { icon: "📚", title: "Caregiver Education Workshops", desc: "Monthly educational sessions covering dementia care, grief, self-care, communication techniques, legal planning, and navigating the healthcare system." },
  { icon: "💬", title: "Individual Counseling", desc: "One-on-one counseling sessions with our social worker or chaplain for family members dealing with caregiver burnout, anticipatory grief, or difficult care decisions." },
  { icon: "📋", title: "Care Conference Facilitation", desc: "Quarterly family care conferences mediated by our social worker to review the resident's care plan, address concerns, and align the care team with family goals." },
  { icon: "🏛️", title: "Legal & Financial Guidance", desc: "Connections to legal resources for advance directives, power of attorney, Medicaid planning, and estate preparation — with referrals to trusted elder law attorneys." },
  { icon: "🫀", title: "Grief & Bereavement Support", desc: "Ongoing bereavement support for families before and after a resident's passing, including a dedicated grief support group and chaplain services." },
  { icon: "📞", title: "24/7 Family Communication Line", desc: "A direct line to our nursing team for urgent questions or updates — because peace of mind shouldn't wait for business hours." },
  { icon: "🌐", title: "Community Resource Connections", desc: "Referrals to community organizations, respite care programs, caregiver relief services, and government assistance programs." },
  { icon: "📖", title: "Family Resource Library", desc: "Curated educational materials — books, guides, videos, and articles — covering all aspects of senior care, dementia, end-of-life planning, and family caregiving." },
];

const process = [
  { number: "01", title: "Family Welcome Meeting", desc: "Within 72 hours of admission, our social worker meets with the family to introduce support services, understand family dynamics and concerns, identify communication preferences, and establish a family care liaison contact." },
  { number: "02", title: "Family Needs Assessment", desc: "We assess the emotional, informational, and practical needs of key family members — especially the primary caregiver — to tailor the support services we provide." },
  { number: "03", title: "Ongoing Support Program Enrollment", desc: "Family members are connected to the appropriate mix of support programs: group sessions, 1-on-1 counseling, educational workshops, and communication channels based on their needs and availability." },
  { number: "04", title: "Regular Care Conferences", desc: "Our social worker facilitates quarterly family care conferences where the entire care team reviews the resident's progress and families can ask questions, raise concerns, and participate in care planning decisions." },
  { number: "05", title: "Transition & Bereavement Support", desc: "Whether a resident transitions home, transfers to another level of care, or passes away — our social worker provides guidance, emotional support, and connection to aftercare resources every step of the way." },
];

const faqs = [
  { q: "What kind of support is available for family caregivers?", a: "We offer a comprehensive range of support: peer support groups, individual counseling sessions with our social worker, educational workshops, care conference participation, a 24/7 nurse communication line, and bereavement support. All services are included in your loved one's care." },
  { q: "How often are family support groups held?", a: "Our family caregiver support group meets twice per month, with sessions typically held on weekday evenings and Saturday mornings to accommodate different schedules. We also offer virtual participation for family members who cannot attend in person." },
  { q: "Can family members be involved in care decisions?", a: "Absolutely. Family involvement in care decisions is a cornerstone of our philosophy. Families participate in quarterly care conferences and can request a meeting with our social worker or Director of Nursing at any time." },
  { q: "What happens if our family is struggling with a difficult care decision?", a: "Our social worker and ethics team are available to support families through complex decisions — including end-of-life choices, care transitions, and goal-of-care conversations. We can also facilitate family meetings with the full care team." },
  { q: "Is bereavement support available even after a resident passes?", a: "Yes. Our bereavement program continues for family members for up to 13 months following a resident's passing. This includes personal condolence outreach, a bereavement support group, chaplain availability, and referrals to grief counselors." },
  { q: "What if I live far away and cannot visit frequently?", a: "We support long-distance families through scheduled phone or video update calls with our nursing team, video visit technology for remote family time with residents, and detailed written care summaries shared electronically." },
];

const testimonials = [
  {
    quote: "The family support group became my lifeline. I met other families going through the exact same thing, and our social worker helped me understand what my father was experiencing — I couldn't have navigated this without them.",
    name: "Jennifer L.",
    relation: "Daughter of Resident",
    img: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&q=80",
  },
  {
    quote: "The care conferences gave me real peace of mind. I could ask every question, raise every concern, and feel like a true partner in my mother's care — not just a visitor.",
    name: "Marcus T.",
    relation: "Son of Resident",
    img: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&q=80",
  },
];

export default function FamilySupportPage() {
  return (
    <div>
      <ServiceHero
        badge="Family Support"
        title="Family Support Services"
        subtitle="Caring for families, not just residents."
        description="We believe that when a loved one enters our care, the entire family becomes part of our community. Our family support program provides the education, emotional support, and communication tools that family caregivers need to thrive."
        heroImg="https://images.unsplash.com/photo-1491013516836-7db643ee125a?w=800&q=80"
        accentColor={ACCENT}
        accentLight={ACCENT_LIGHT}
        stats={[
          { value: "24/7", label: "Family Hotline", icon: "📞" },
          { value: "Monthly", label: "Workshops", icon: "📚" },
          { value: "Free", label: "All Support Services", icon: "❤️" },
        ]}
      />

      {/* Why Family Support Matters */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div className="relative rounded-3xl overflow-hidden shadow-2xl h-[420px]">
              <img src="https://images.unsplash.com/photo-1609220136736-443140cffec6?w=700&q=80" alt="Family visit" className="w-full h-full object-cover" />
            </div>
            <div>
              <h2 className="text-4xl font-bold text-[#0f1a3e]">Why Family Support Matters</h2>
              <p className="mt-4 text-gray-500 leading-relaxed">
                Caring for a loved one in a nursing facility is an emotional journey — often one marked by guilt, grief, uncertainty, and exhaustion. Our Family Support program acknowledges that families are not bystanders; they are essential partners in care.
              </p>
              <div className="mt-6 space-y-4">
                {[
                  { icon: "💔", title: "Caregiver Burnout Is Real", desc: "Before admission and after, family caregivers experience high rates of depression and physical health decline. We provide tools and support to protect caregiver well-being." },
                  { icon: "🤝", title: "Informed Families = Better Outcomes", desc: "Residents whose families are actively engaged in their care plans consistently show better health outcomes, higher satisfaction, and more stable transitions." },
                  { icon: "🧭", title: "Navigating Care Is Complex", desc: "Medical decisions, financial planning, legal documents, and care transitions are overwhelming. Our social workers guide families through every step." },
                ].map((item) => (
                  <div key={item.title} className="flex items-start gap-4 p-4 rounded-2xl border border-gray-100">
                    <span className="text-2xl shrink-0">{item.icon}</span>
                    <div>
                      <p className="font-bold text-[#0f1a3e] text-sm">{item.title}</p>
                      <p className="text-xs text-gray-500 mt-1">{item.desc}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </section>

      <WhatWeProvide items={provides} accentColor={ACCENT} accentLight={ACCENT_LIGHT} title="Family Support Services" />
      <OurProcess steps={process} accentColor={ACCENT} />

      {/* Testimonials */}
      <section className="py-20" style={{ background: ACCENT_LIGHT }}>
        <div className="max-w-5xl mx-auto px-6">
          <h2 className="text-4xl font-bold text-[#0f1a3e] text-center mb-12">Families Trust Us</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {testimonials.map((t) => (
              <div key={t.name} className="bg-white rounded-3xl p-8 shadow-md border border-gray-100">
                <div className="flex gap-1 mb-4">
                  {[...Array(5)].map((_, i) => (
                    <svg key={i} className="w-5 h-5 text-amber-400" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  ))}
                </div>
                <p className="text-gray-600 italic text-sm leading-relaxed">"{t.quote}"</p>
                <div className="flex items-center gap-3 mt-6 pt-4 border-t border-gray-100">
                  <img src={t.img} alt={t.name} className="w-10 h-10 rounded-full object-cover" />
                  <div>
                    <p className="font-bold text-[#0f1a3e] text-sm">{t.name}</p>
                    <p className="text-xs text-gray-500">{t.relation}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <ServiceFaq faqs={faqs} accentColor={ACCENT} />
      <ServiceCta
        title="You Don't Have to Navigate This Alone"
        desc="Our family support team is ready to walk alongside you every step of the way — with compassion, expertise, and genuine care."
        accentColor={ACCENT}
      />
    </div>
  );
}
