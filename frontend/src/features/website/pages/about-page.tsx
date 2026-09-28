const milestones = [
  {
    year: "1999",
    title: "Our Founding",
    desc: "ElderCare opened its doors with a mission to redefine senior nursing care — treating every resident like family.",
    img: "https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=400&q=80",
  },
  {
    year: "2006",
    title: "Expanding Our Reach",
    desc: "We expanded to a second facility and introduced our Memory Care program, one of the first in the region.",
    img: "https://images.unsplash.com/photo-1497366216548-37526070297c?w=400&q=80",
  },
  {
    year: "2015",
    title: "Excellence in Care",
    desc: "Received national accreditation for our nursing standards and introduced our comprehensive rehabilitation program.",
    img: "https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&q=80",
  },
  {
    year: "2024",
    title: "Digital Transformation",
    desc: "Launched integrated digital care management, improving real-time communication between staff and families.",
    img: "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400&q=80",
  },
  {
    year: "2025",
    title: "Today & Beyond",
    desc: "Continuing our mission with 500+ residents, 120+ staff, and an unwavering commitment to compassionate care.",
    img: "https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=400&q=80",
  },
];

const facilities = [
  {
    title: "People-Shared Dining Area",
    desc: "A warm, restaurant-style dining space where residents enjoy nutritious meals together — fostering community and belonging.",
    icon: "🍽️",
  },
  {
    title: "Wellness & Nursing Facility",
    desc: "State-of-the-art nursing stations and private care rooms equipped with the latest medical technology for optimal comfort and safety.",
    icon: "🏥",
  },
  {
    title: "Memory Care Wing",
    desc: "A specialized, secure environment designed specifically for residents with Alzheimer's and other forms of dementia.",
    icon: "🧠",
  },
  {
    title: "Rehabilitation Center",
    desc: "Fully equipped physical and occupational therapy gym to help residents regain strength and independence.",
    icon: "💪",
  },
  {
    title: "Gardens & Outdoor Spaces",
    desc: "Beautiful landscaped gardens providing a serene environment for relaxation, therapy, and family visits.",
    icon: "🌿",
  },
];

export default function AboutPage() {
  return (
    <div>
      {/* Hero */}
      <section className="relative bg-gradient-to-br from-[#0f1a3e] to-[#1e3a8a] py-28 overflow-hidden">
        <div className="absolute inset-0 opacity-10">
          <div className="absolute top-0 right-0 w-96 h-96 bg-white rounded-full blur-3xl translate-x-1/2 -translate-y-1/2" />
        </div>
        <div className="max-w-7xl mx-auto px-6 relative z-10 text-center">
          <span className="inline-block bg-white/10 text-blue-200 text-sm font-semibold px-4 py-2 rounded-full border border-white/20 mb-6">
            Our Story
          </span>
          <h1 className="text-5xl lg:text-7xl font-bold text-white leading-tight">
            The Heart Of
            <br />
            <span className="text-[#748ffc]">ElderCare</span>
          </h1>
          <p className="mt-6 text-blue-200 text-xl max-w-2xl mx-auto leading-relaxed">
            For over 25 years, we have dedicated ourselves to providing exceptional nursing and senior care — driven by compassion, guided by excellence.
          </p>
        </div>
      </section>

      {/* Purpose & Commitment */}
      <section className="py-24 bg-white">
        <div className="max-w-7xl mx-auto px-6 grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <div>
            <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Who We Are</span>
            <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e] leading-tight">
              Our Purpose & Commitment
            </h2>
            <p className="mt-5 text-gray-500 leading-relaxed">
              At ElderCare, we believe that every senior deserves to live with dignity, purpose, and joy. Our team of dedicated nursing professionals, therapists, and care coordinators works tirelessly to create a home — not just a facility — for every resident.
            </p>
            <p className="mt-4 text-gray-500 leading-relaxed">
              We take a holistic approach to care: addressing not just medical needs, but emotional, social, and spiritual well-being too. From personalized care plans to daily enrichment activities, everything we do is rooted in respect for the individuals we serve.
            </p>

            {/* Certifications */}
            <div className="mt-8 flex flex-wrap gap-3">
              {["Joint Commission Accredited", "CMS 5-Star Rating", "AHCA Quality Award"].map((cert) => (
                <div
                  key={cert}
                  className="flex items-center gap-2 bg-[#EDF2FF] text-[#3B5BDB] text-sm font-semibold px-4 py-2 rounded-full"
                >
                  <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                  {cert}
                </div>
              ))}
            </div>
          </div>

          <div className="relative">
            <div className="rounded-3xl overflow-hidden shadow-2xl shadow-blue-100">
              <img
                src="https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=700&q=80"
                alt="Medical team commitment"
                className="w-full h-[480px] object-cover"
              />
            </div>
            <div className="absolute -bottom-6 -right-6 bg-[#3B5BDB] text-white rounded-2xl p-5 shadow-xl">
              <p className="text-4xl font-bold">25+</p>
              <p className="text-blue-200 text-sm">Years of Excellence</p>
            </div>
          </div>
        </div>
      </section>

      {/* Journey Timeline */}
      <section className="py-24 bg-[#f8f9ff]">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Our History</span>
            <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e]">Our Journey Of Care</h2>
            <p className="mt-4 text-gray-500 max-w-xl mx-auto">
              From humble beginnings to a recognized leader in senior care — our journey has always been guided by love.
            </p>
          </div>

          <div className="relative">
            {/* Timeline line */}
            <div className="hidden lg:block absolute left-1/2 -translate-x-1/2 top-0 bottom-0 w-0.5 bg-gradient-to-b from-[#3B5BDB] to-[#748ffc]" />

            <div className="space-y-16">
              {milestones.map((m, i) => (
                <div
                  key={m.year}
                  className={`grid grid-cols-1 lg:grid-cols-2 gap-10 items-center ${i % 2 !== 0 ? "lg:direction-rtl" : ""}`}
                >
                  <div className={`${i % 2 !== 0 ? "lg:order-2" : ""}`}>
                    <div className="bg-white rounded-3xl p-8 shadow-md border border-gray-100 hover:shadow-xl transition-shadow duration-300">
                      <div className="inline-block bg-[#EDF2FF] text-[#3B5BDB] font-bold text-lg px-4 py-1.5 rounded-xl mb-4">
                        {m.year}
                      </div>
                      <h3 className="text-2xl font-bold text-[#0f1a3e]">{m.title}</h3>
                      <p className="mt-3 text-gray-500 leading-relaxed">{m.desc}</p>
                    </div>
                  </div>
                  <div className={`${i % 2 !== 0 ? "lg:order-1" : ""}`}>
                    <div className="rounded-2xl overflow-hidden shadow-lg h-56">
                      <img src={m.img} alt={m.title} className="w-full h-full object-cover" />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Facilities */}
      <section className="py-24 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-start">
            <div className="sticky top-28">
              <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Where We Care</span>
              <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e] leading-tight">Our Facilities</h2>
              <p className="mt-4 text-gray-500 leading-relaxed">
                Our campuses are designed to feel like home — warm, welcoming, and equipped with everything residents need for comfort, safety, and well-being.
              </p>
              <div className="mt-8 rounded-2xl overflow-hidden shadow-xl">
                <img
                  src="https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=600&q=80"
                  alt="ElderCare Facility"
                  className="w-full h-72 object-cover"
                />
              </div>
            </div>

            <div className="space-y-4">
              {facilities.map((f) => (
                <div
                  key={f.title}
                  className="group border border-gray-100 rounded-2xl p-6 hover:border-[#3B5BDB]/30 hover:shadow-md transition-all duration-200"
                >
                  <div className="flex items-start gap-4">
                    <span className="text-3xl">{f.icon}</span>
                    <div>
                      <h3 className="text-lg font-bold text-[#0f1a3e] group-hover:text-[#3B5BDB] transition-colors">{f.title}</h3>
                      <p className="mt-2 text-gray-500 text-sm leading-relaxed">{f.desc}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
