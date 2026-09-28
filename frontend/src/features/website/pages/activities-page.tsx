const activities = [
  { title: "Arts & Crafts", desc: "Creative expression through painting, pottery, and handcrafts.", img: "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=500&q=80", tag: "Creative" },
  { title: "Garden Therapy", desc: "Therapeutic gardening in our beautiful outdoor spaces.", img: "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=500&q=80", tag: "Wellness" },
  { title: "Music Sessions", desc: "Live music performances and singalong events every week.", img: "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&q=80", tag: "Therapy" },
  { title: "Group Exercises", desc: "Chair yoga, stretching, and low-impact fitness classes.", img: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500&q=80", tag: "Fitness" },
  { title: "Movie Nights", desc: "Classic films and modern favorites in our cozy theater room.", img: "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&q=80", tag: "Social" },
  { title: "Cooking Classes", desc: "Interactive cooking demos and tasting events for residents.", img: "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=500&q=80", tag: "Life Skills" },
  { title: "Book Club", desc: "Monthly reading groups and literary discussions.", img: "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500&q=80", tag: "Educational" },
  { title: "Community Outings", desc: "Supervised trips to local parks, museums, and restaurants.", img: "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=500&q=80", tag: "Community" },
];

const testimonials = [
  {
    quote: "The staff at ElderCare are like family to my mother. She looks forward to every activity and has never been happier.",
    name: "Sarah M.",
    relation: "Daughter of Resident",
    avatar: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&q=80",
    stars: 5,
  },
  {
    quote: "The Music Therapy sessions have made an incredible difference for my father's dementia. He sings along to songs he remembered from years ago.",
    name: "Robert K.",
    relation: "Son of Resident",
    avatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&q=80",
    stars: 5,
  },
  {
    quote: "What sets ElderCare apart is the genuine care from every single staff member. The activities program keeps my mom engaged and joyful.",
    name: "Linda T.",
    relation: "Daughter of Resident",
    avatar: "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=100&q=80",
    stars: 5,
  },
];

const tagColor: Record<string, string> = {
  Creative: "bg-pink-100 text-pink-700",
  Wellness: "bg-green-100 text-green-700",
  Therapy: "bg-blue-100 text-blue-700",
  Fitness: "bg-orange-100 text-orange-700",
  Social: "bg-purple-100 text-purple-700",
  "Life Skills": "bg-yellow-100 text-yellow-700",
  Educational: "bg-teal-100 text-teal-700",
  Community: "bg-indigo-100 text-indigo-700",
};

export default function ActivitiesPage() {
  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-[#f0f4ff] to-white py-24">
        <div className="max-w-7xl mx-auto px-6 grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <div>
            <span className="inline-block bg-[#3B5BDB]/10 text-[#3B5BDB] text-sm font-semibold px-4 py-2 rounded-full mb-6">
              Enriching Lives Daily
            </span>
            <h1 className="text-5xl font-bold text-[#0f1a3e] leading-tight">
              Social & Recreational Activities
            </h1>
            <p className="mt-6 text-lg text-gray-500 leading-relaxed">
              Life at ElderCare is vibrant, engaging, and full of purpose. Our activities program is designed to stimulate the mind, nurture the body, and feed the soul.
            </p>
            <div className="mt-8 flex gap-6">
              <div className="text-center">
                <p className="text-3xl font-bold text-[#3B5BDB]">20+</p>
                <p className="text-sm text-gray-500">Activities per week</p>
              </div>
              <div className="w-px bg-gray-200" />
              <div className="text-center">
                <p className="text-3xl font-bold text-[#3B5BDB]">7</p>
                <p className="text-sm text-gray-500">Days a week</p>
              </div>
              <div className="w-px bg-gray-200" />
              <div className="text-center">
                <p className="text-3xl font-bold text-[#3B5BDB]">100%</p>
                <p className="text-sm text-gray-500">Volunteer based</p>
              </div>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <img src="https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=400&q=80" alt="Activity" className="rounded-2xl h-52 w-full object-cover shadow-lg" />
            <img src="https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=400&q=80" alt="Activity" className="rounded-2xl h-52 w-full object-cover shadow-lg mt-8" />
            <img src="https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=400&q=80" alt="Activity" className="rounded-2xl h-52 w-full object-cover shadow-lg -mt-8" />
            <img src="https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&q=80" alt="Activity" className="rounded-2xl h-52 w-full object-cover shadow-lg" />
          </div>
        </div>
      </section>

      {/* Activities Grid */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-14">
            <h2 className="text-4xl font-bold text-[#0f1a3e]">Our Activity Programs</h2>
            <p className="mt-4 text-gray-500 max-w-xl mx-auto">
              From morning yoga to evening concerts — there is always something to look forward to.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {activities.map((a) => (
              <div
                key={a.title}
                className="group rounded-2xl overflow-hidden border border-gray-100 hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
              >
                <div className="relative h-44 overflow-hidden">
                  <img src={a.img} alt={a.title} className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
                  <div className="absolute top-3 left-3">
                    <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${tagColor[a.tag]}`}>{a.tag}</span>
                  </div>
                </div>
                <div className="p-5">
                  <h3 className="font-bold text-[#0f1a3e]">{a.title}</h3>
                  <p className="mt-1.5 text-xs text-gray-500 leading-relaxed">{a.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Testimonials */}
      <section className="py-20 bg-[#f8f9ff]">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-14">
            <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Family Reviews</span>
            <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e]">What Families Say</h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {testimonials.map((t) => (
              <div key={t.name} className="bg-white rounded-3xl p-8 shadow-md border border-gray-100 hover:shadow-xl transition-shadow duration-300">
                {/* Stars */}
                <div className="flex gap-1 mb-5">
                  {[...Array(t.stars)].map((_, i) => (
                    <svg key={i} className="w-5 h-5 text-amber-400" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  ))}
                </div>

                <p className="text-gray-600 italic leading-relaxed text-sm">"{t.quote}"</p>

                <div className="flex items-center gap-3 mt-6 pt-5 border-t border-gray-100">
                  <img src={t.avatar} alt={t.name} className="w-10 h-10 rounded-full object-cover" />
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

      {/* Newsletter */}
      <section className="py-16 bg-[#3B5BDB]">
        <div className="max-w-2xl mx-auto px-6 text-center">
          <h2 className="text-3xl font-bold text-white">Stay Updated With ElderCare</h2>
          <p className="mt-3 text-blue-200">Get our monthly activity calendar and community updates delivered to your inbox.</p>
          <div className="mt-8 flex gap-3 max-w-md mx-auto">
            <input
              type="email"
              placeholder="Enter your email"
              className="flex-1 px-5 py-3.5 rounded-xl bg-white text-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-white/50 placeholder:text-gray-400"
            />
            <button className="px-6 py-3.5 bg-[#0f1a3e] text-white font-semibold rounded-xl hover:bg-[#1a2f6b] transition-colors whitespace-nowrap text-sm">
              Subscribe
            </button>
          </div>
        </div>
      </section>
    </div>
  );
}
