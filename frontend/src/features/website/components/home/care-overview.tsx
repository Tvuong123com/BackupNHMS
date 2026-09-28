import { Link } from "react-router";

const features = [
  {
    icon: (
      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
      </svg>
    ),
    title: "Personalized Nursing Care",
    desc: "Each resident receives a custom care plan developed by our certified nursing staff.",
    img: "https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=500&q=80",
  },
  {
    icon: (
      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    ),
    title: "Rehabilitation & Therapy",
    desc: "Physical, occupational, and speech therapy programs designed to restore independence.",
    img: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500&q=80",
  },
  {
    icon: (
      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
      </svg>
    ),
    title: "Recreational Programs",
    desc: "Engaging social activities, cultural events, and community outings to enrich daily life.",
    img: "https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=500&q=80",
  },
];

export function CareOverviewSection() {
  return (
    <section className="py-24 bg-white">
      <div className="max-w-7xl mx-auto px-6">
        {/* Header */}
        <div className="text-center mb-16 max-w-2xl mx-auto">
          <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Why Choose Us</span>
          <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e] leading-tight">
            Because Your Family Matters To Us
          </h2>
          <p className="mt-4 text-gray-500 text-lg">
            Every decision we make is guided by one principle: the well-being of your loved one.
          </p>
        </div>

        {/* Feature Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {features.map((f) => (
            <div
              key={f.title}
              className="group rounded-3xl overflow-hidden border border-gray-100 hover:border-[#3B5BDB]/20 hover:shadow-xl hover:shadow-blue-50 transition-all duration-300"
            >
              {/* Image */}
              <div className="relative h-52 overflow-hidden">
                <img
                  src={f.img}
                  alt={f.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-[#0f1a3e]/40 to-transparent" />
              </div>

              {/* Content */}
              <div className="p-7">
                <div className="w-12 h-12 bg-[#EDF2FF] text-[#3B5BDB] rounded-2xl flex items-center justify-center mb-4 group-hover:bg-[#3B5BDB] group-hover:text-white transition-all duration-300">
                  {f.icon}
                </div>
                <h3 className="text-xl font-bold text-[#0f1a3e] mb-2">{f.title}</h3>
                <p className="text-gray-500 text-sm leading-relaxed">{f.desc}</p>
                <Link
                  to="/services"
                  className="inline-flex items-center gap-1.5 mt-4 text-[#3B5BDB] text-sm font-semibold hover:gap-3 transition-all duration-200"
                >
                  Learn more
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
                  </svg>
                </Link>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
