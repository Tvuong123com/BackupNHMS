import {
  ServiceHero,
  WhatWeProvide,
  OurProcess,
  ServiceFaq,
  ServiceCta,
} from "../../components/services/service-detail-shared";

const ACCENT = "#D97706";
const ACCENT_LIGHT = "#FFFBEB";

const provides = [
  { icon: "🎨", title: "Arts & Crafts", desc: "Painting, drawing, collage, pottery, and crafting sessions that spark creativity, improve fine motor skills, and provide a meaningful outlet for self-expression." },
  { icon: "🎵", title: "Music Therapy & Performance", desc: "Live music sessions, singalongs, rhythm activities, and music reminiscence programs that reduce anxiety, boost mood, and stimulate long-term memory." },
  { icon: "🌿", title: "Horticulture Therapy", desc: "Therapeutic gardening in our outdoor gardens — planting, watering, and harvesting — connects residents with nature and provides gentle physical activity." },
  { icon: "🎭", title: "Drama & Storytelling", desc: "Group storytelling, reminiscence circles, and light dramatic performances that build community, encourage communication, and preserve life histories." },
  { icon: "🧩", title: "Cognitive Games & Brain Training", desc: "Board games, trivia, puzzles, word games, and memory exercises that keep minds active, engaged, and socially connected." },
  { icon: "🚌", title: "Community Outings", desc: "Supervised excursions to local parks, museums, restaurants, and cultural events that keep residents connected to the wider community." },
  { icon: "⛪", title: "Spiritual & Religious Programs", desc: "Non-denominational worship services, chaplain visits, and spiritual care support for residents of all faith traditions." },
  { icon: "🎬", title: "Movie & Entertainment Nights", desc: "Weekly movie screenings, game shows, and live entertainment in our community room — bringing laughter and social connection to every resident." },
  { icon: "📚", title: "Educational & Discussion Groups", desc: "Book clubs, current events discussions, and learning workshops that stimulate curiosity and intellectual engagement." },
];

const process = [
  { number: "01", title: "Recreational Therapy Assessment", desc: "Our certified recreational therapist conducts an individualized assessment of each resident's interests, hobbies, functional abilities, and social preferences to build a personalized activity profile." },
  { number: "02", title: "Personalized Activity Planning", desc: "Based on the assessment, the RT creates an individualized activity plan that balances preferred activities, therapeutic goals (cognitive, physical, social, emotional), and group programming." },
  { number: "03", title: "Weekly Activity Calendar", desc: "A structured, varied weekly calendar is published and posted throughout the facility. Residents can choose from group programs, 1-on-1 activities, and special events tailored to their preferences and abilities." },
  { number: "04", title: "Therapeutic Goal Integration", desc: "All recreational programs are designed with specific therapeutic goals in mind — reducing depression, maintaining cognitive function, improving social engagement, and supporting physical health." },
  { number: "05", title: "Progress Documentation & Family Updates", desc: "Our recreational therapist documents participation, mood, and response to activities, sharing updates with the care team and families at quarterly care conferences." },
];

const faqs = [
  { q: "What is recreational therapy — is it just fun activities?", a: "Recreational therapy is a clinical practice using leisure activities as therapeutic interventions. Our certified recreational therapists (CTRSs) design programs with specific goals — like reducing depression, improving cognition, or maintaining physical function." },
  { q: "How often will my loved one have recreational activities?", a: "We offer 5–7 activity sessions per day, 7 days per week — including morning, afternoon, and evening programs. Residents can participate at their level of comfort and ability." },
  { q: "Can my loved one request a specific activity?", a: "Absolutely. Our activities team actively solicits resident preferences and can arrange 1-on-1 activity sessions for residents who prefer individual programming or have specific hobbies." },
  { q: "What if my loved one has limited mobility?", a: "All of our programs are adapted for varying levels of mobility. Residents who use wheelchairs or have limited dexterity can participate fully in adapted activities." },
  { q: "Are family members welcome to join activities?", a: "Yes! We love when family members participate. Family involvement in activities is one of the most meaningful ways to stay connected with your loved one." },
];

const highlights = [
  { metric: "20+", label: "Activities per week" },
  { metric: "7 days", label: "Weekly programming" },
  { metric: "CTRS", label: "Certified Therapist" },
  { metric: "100%", label: "Resident-centered" },
];

export default function RecreationalTherapyPage() {
  return (
    <div>
      <ServiceHero
        badge="Recreational Therapy"
        title="Recreational Therapy"
        subtitle="Enriching life through meaningful activity."
        description="Life at ElderCare is vibrant, purposeful, and full of joy. Our certified recreational therapists design programs that nurture the mind, body, and spirit — because living well is about more than medical care alone."
        heroImg="https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=800&q=80"
        accentColor={ACCENT}
        accentLight={ACCENT_LIGHT}
        stats={[
          { value: "20+", label: "Programs / Week", icon: "🎨" },
          { value: "CTRS", label: "Certified Therapist", icon: "🏆" },
          { value: "7 Days", label: "Weekly Calendar", icon: "📅" },
        ]}
      />

      {/* Benefits */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div>
              <h2 className="text-4xl font-bold text-[#0f1a3e]">The Benefits of Recreational Therapy</h2>
              <p className="mt-4 text-gray-500 leading-relaxed">
                Research consistently shows that meaningful activity and social engagement are fundamental to quality of life, cognitive health, and emotional well-being for seniors. Our program delivers these benefits:
              </p>
              <div className="mt-6 grid grid-cols-2 gap-4">
                {[
                  { icon: "😊", label: "Reduced Depression & Anxiety" },
                  { icon: "🧠", label: "Cognitive Maintenance" },
                  { icon: "🤝", label: "Social Connection" },
                  { icon: "💪", label: "Physical Activity & Mobility" },
                  { icon: "🎯", label: "Sense of Purpose" },
                  { icon: "😴", label: "Improved Sleep Quality" },
                  { icon: "🍎", label: "Better Appetite" },
                  { icon: "❤️", label: "Overall Quality of Life" },
                ].map((b) => (
                  <div key={b.label} className="flex items-center gap-3 p-3 rounded-xl" style={{ background: ACCENT_LIGHT }}>
                    <span className="text-2xl">{b.icon}</span>
                    <span className="text-sm font-medium text-[#0f1a3e]">{b.label}</span>
                  </div>
                ))}
              </div>
            </div>
            <div className="relative rounded-3xl overflow-hidden shadow-2xl h-[450px]">
              <img src="https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=700&q=80" alt="Activities" className="w-full h-full object-cover" />
            </div>
          </div>
        </div>
      </section>

      <WhatWeProvide items={provides} accentColor={ACCENT} accentLight={ACCENT_LIGHT} title="Our Recreational Programs" />
      <OurProcess steps={process} accentColor={ACCENT} />
      <ServiceFaq faqs={faqs} accentColor={ACCENT} />
      <ServiceCta
        title="Life Here Is Full of Joy"
        desc="See how our recreational therapy program transforms daily life for residents. Schedule a visit to experience it yourself."
        accentColor={ACCENT}
      />
    </div>
  );
}
