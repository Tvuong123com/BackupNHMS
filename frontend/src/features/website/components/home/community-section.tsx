const events = [
  {
    date: "Aug 10",
    day: "Sun",
    title: "Garden Therapy Morning",
    desc: "Join residents for a peaceful morning of gardening and nature therapy.",
    category: "Wellness",
    img: "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&q=80",
  },
  {
    date: "Aug 14",
    day: "Thu",
    title: "Family Visiting Day",
    desc: "A special afternoon for families to spend quality time with their loved ones.",
    category: "Community",
    img: "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=400&q=80",
  },
  {
    date: "Aug 18",
    day: "Mon",
    title: "Music & Memory Session",
    desc: "Therapeutic music program designed for memory care residents.",
    category: "Therapy",
    img: "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&q=80",
  },
];

const categoryColor: Record<string, string> = {
  Wellness: "bg-green-100 text-green-700",
  Community: "bg-purple-100 text-purple-700",
  Therapy: "bg-blue-100 text-blue-700",
};

export function CommunitySection() {
  return (
    <section className="py-24 bg-white">
      <div className="max-w-7xl mx-auto px-6">
        {/* Header */}
        <div className="flex flex-col md:flex-row md:items-end md:justify-between gap-6 mb-14">
          <div>
            <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Upcoming Events</span>
            <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e]">
              Stay Engaged With Our Community
            </h2>
            <p className="mt-3 text-gray-500 max-w-xl">
              We believe an active, social lifestyle is essential to well-being. Join us for regular events, activities, and celebrations.
            </p>
          </div>
          <a
            href="/activities"
            className="shrink-0 inline-flex items-center gap-2 text-[#3B5BDB] font-semibold hover:underline"
          >
            View all activities
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          </a>
        </div>

        {/* Event Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {events.map((event) => (
            <div
              key={event.title}
              className="group rounded-3xl overflow-hidden border border-gray-100 hover:shadow-xl hover:shadow-blue-50 hover:-translate-y-1 transition-all duration-300"
            >
              {/* Image */}
              <div className="relative h-48 overflow-hidden">
                <img
                  src={event.img}
                  alt={event.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />
                {/* Date badge */}
                <div className="absolute top-4 left-4 bg-white rounded-xl px-3 py-2 text-center shadow-md">
                  <p className="text-xs text-gray-400 uppercase tracking-wide">{event.day}</p>
                  <p className="text-lg font-bold text-[#0f1a3e] leading-none">{event.date.split(" ")[1]}</p>
                  <p className="text-xs text-[#3B5BDB] font-semibold">Aug</p>
                </div>
              </div>

              {/* Content */}
              <div className="p-6">
                <span className={`text-xs font-semibold px-3 py-1 rounded-full ${categoryColor[event.category]}`}>
                  {event.category}
                </span>
                <h3 className="mt-3 text-lg font-bold text-[#0f1a3e]">{event.title}</h3>
                <p className="mt-2 text-sm text-gray-500 leading-relaxed">{event.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
