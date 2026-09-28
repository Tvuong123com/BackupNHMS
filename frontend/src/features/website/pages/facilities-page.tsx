import { useState } from "react";
import { Link } from "react-router";

const facilities = [
  {
    id: 1,
    name: "ElderCare Main Campus",
    type: "Skilled Nursing & Rehabilitation",
    address: "1234 Elder Lane, Sacramento, CA 95814",
    phone: "(916) 555-9000",
    beds: 120,
    rating: 5,
    established: 1999,
    img: "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800&q=80",
    features: ["Skilled Nursing", "Physical Therapy", "Memory Care Wing", "Private & Semi-Private Rooms", "Garden Courtyard", "Chapel"],
    description: "Our flagship campus offers a full continuum of skilled nursing and rehabilitation services in a warm, homelike environment. With 120 beds and a dedicated memory care wing, we serve a diverse range of care needs.",
    highlights: [
      { icon: "🏥", label: "120 Beds" },
      { icon: "🧠", label: "Memory Care Wing" },
      { icon: "💪", label: "Full Rehab Gym" },
      { icon: "🌿", label: "Garden Courtyard" },
    ],
  },
  {
    id: 2,
    name: "ElderCare Riverside",
    type: "Assisted Living & Long-Term Care",
    address: "5678 Riverside Blvd, Sacramento, CA 95816",
    phone: "(916) 555-9100",
    beds: 80,
    rating: 5,
    established: 2006,
    img: "https://images.unsplash.com/photo-1497366216548-37526070297c?w=800&q=80",
    features: ["Assisted Living", "Long-Term Care", "Occupational Therapy", "Shared Dining Hall", "Activity Center", "WiFi Throughout"],
    description: "Our Riverside location specializes in assisted living and long-term care for residents who need daily support but enjoy a more independent lifestyle. The vibrant activity center keeps residents engaged every day.",
    highlights: [
      { icon: "🏠", label: "80 Beds" },
      { icon: "🍽️", label: "Restaurant-Style Dining" },
      { icon: "🎨", label: "Activity Center" },
      { icon: "📶", label: "Full WiFi Coverage" },
    ],
  },
  {
    id: 3,
    name: "ElderCare Wellness Center",
    type: "Outpatient Therapy & Day Programs",
    address: "910 Wellness Way, Sacramento, CA 95819",
    phone: "(916) 555-9200",
    beds: 0,
    rating: 4,
    established: 2015,
    img: "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=800&q=80",
    features: ["Outpatient Therapy", "Adult Day Programs", "Nutrition Counseling", "Group Fitness Classes", "Telehealth Services", "Family Education"],
    description: "Our Wellness Center provides outpatient therapy, adult day programs, and preventive health services for seniors living in the community. Perfect for those who want quality care without full-time residency.",
    highlights: [
      { icon: "🏋️", label: "Therapy Gym" },
      { icon: "📺", label: "Telehealth Ready" },
      { icon: "👥", label: "Day Programs" },
      { icon: "🥗", label: "Nutrition Counseling" },
    ],
  },
];

const amenities = [
  { icon: "🛁", label: "Private Bathrooms", desc: "Ensuite or shared bathrooms with safety grab bars and walk-in showers." },
  { icon: "📺", label: "In-Room Entertainment", desc: "Cable TV, telephone service, and high-speed WiFi in all rooms." },
  { icon: "🌿", label: "Outdoor Spaces", desc: "Landscaped gardens, patios, and walking paths for fresh air and relaxation." },
  { icon: "🍽️", label: "Restaurant-Style Dining", desc: "Three chef-prepared meals daily plus healthy snacks, served in our dining rooms." },
  { icon: "💈", label: "Beauty & Barber Shop", desc: "On-site salon services including haircuts, styling, and grooming." },
  { icon: "📚", label: "Library & Lounge", desc: "Quiet reading rooms, newspaper delivery, and comfortable common lounges." },
  { icon: "⛪", label: "Chapel & Spiritual Care", desc: "Non-denominational chapel with regular services and chaplain visits." },
  { icon: "🚌", label: "Transportation", desc: "Scheduled transportation for medical appointments and community outings." },
];

export default function FacilitiesPage() {
  const [activeFacility, setActiveFacility] = useState(0);

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-[#EDF2FF] to-white py-24">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div>
              <span className="inline-block bg-[#3B5BDB]/10 text-[#3B5BDB] text-sm font-semibold px-4 py-2 rounded-full mb-6">
                Our Locations
              </span>
              <h1 className="text-5xl lg:text-6xl font-bold text-[#0f1a3e] leading-tight">
                Our
                <span className="text-[#3B5BDB]"> Facilities</span>
              </h1>
              <p className="mt-6 text-lg text-gray-500 leading-relaxed">
                Three carefully designed campuses — each offering a warm, safe, and enriching environment. Every facility is staffed 24/7 by our dedicated care team.
              </p>
              <div className="mt-8 flex flex-wrap gap-6">
                <div className="text-center">
                  <p className="text-3xl font-bold text-[#3B5BDB]">3</p>
                  <p className="text-sm text-gray-500">Locations</p>
                </div>
                <div className="w-px bg-gray-200" />
                <div className="text-center">
                  <p className="text-3xl font-bold text-[#3B5BDB]">200+</p>
                  <p className="text-sm text-gray-500">Total Beds</p>
                </div>
                <div className="w-px bg-gray-200" />
                <div className="text-center">
                  <p className="text-3xl font-bold text-[#3B5BDB]">24/7</p>
                  <p className="text-sm text-gray-500">Staffed</p>
                </div>
                <div className="w-px bg-gray-200" />
                <div className="text-center">
                  <p className="text-3xl font-bold text-[#3B5BDB]">CMS ⭐⭐⭐⭐⭐</p>
                  <p className="text-sm text-gray-500">5-Star Rating</p>
                </div>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <img src="https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=400&q=80" alt="Facility exterior" className="rounded-2xl h-48 w-full object-cover shadow-lg" />
              <img src="https://images.unsplash.com/photo-1581579438747-1dc8d17bbce4?w=400&q=80" alt="Care room" className="rounded-2xl h-48 w-full object-cover shadow-lg mt-6" />
              <img src="https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=400&q=80" alt="Activity room" className="rounded-2xl h-48 w-full object-cover shadow-lg -mt-6" />
              <img src="https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&q=80" alt="Garden" className="rounded-2xl h-48 w-full object-cover shadow-lg" />
            </div>
          </div>
        </div>
      </section>

      {/* Facility Tabs */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold text-[#0f1a3e]">Explore Our Campuses</h2>
            <p className="mt-4 text-gray-500">Each location is designed for comfort, safety, and community.</p>
          </div>

          {/* Tab Selector */}
          <div className="flex flex-wrap justify-center gap-3 mb-12">
            {facilities.map((f, i) => (
              <button
                key={f.id}
                onClick={() => setActiveFacility(i)}
                className={`px-6 py-3 rounded-2xl text-sm font-semibold transition-all duration-200 ${
                  activeFacility === i
                    ? "bg-[#3B5BDB] text-white shadow-lg shadow-blue-200"
                    : "bg-gray-100 text-gray-600 hover:bg-[#EDF2FF] hover:text-[#3B5BDB]"
                }`}
              >
                {f.name.replace("ElderCare ", "")}
              </button>
            ))}
          </div>

          {/* Active Facility Detail */}
          {facilities.map((f, i) => (
            <div
              key={f.id}
              className={`transition-all duration-300 ${activeFacility === i ? "block" : "hidden"}`}
            >
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
                {/* Image */}
                <div className="relative">
                  <div className="rounded-3xl overflow-hidden shadow-2xl shadow-blue-100">
                    <img src={f.img} alt={f.name} className="w-full h-[420px] object-cover" />
                  </div>
                  {/* Type badge */}
                  <div className="absolute top-5 left-5 bg-white/95 backdrop-blur rounded-xl px-4 py-2 shadow-md">
                    <p className="text-xs text-gray-500">Facility Type</p>
                    <p className="text-sm font-bold text-[#3B5BDB]">{f.type}</p>
                  </div>
                  {/* Beds badge */}
                  {f.beds > 0 && (
                    <div className="absolute bottom-5 right-5 bg-[#3B5BDB] text-white rounded-xl px-4 py-2 shadow-md">
                      <p className="text-xl font-bold">{f.beds}</p>
                      <p className="text-xs text-blue-200">Beds</p>
                    </div>
                  )}
                </div>

                {/* Info */}
                <div>
                  <div className="flex items-center gap-2 mb-2">
                    {[...Array(f.rating)].map((_, ri) => (
                      <svg key={ri} className="w-5 h-5 text-amber-400" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                      </svg>
                    ))}
                    <span className="text-sm text-gray-500 ml-1">CMS {f.rating}-Star</span>
                  </div>

                  <h3 className="text-3xl font-bold text-[#0f1a3e]">{f.name}</h3>
                  <p className="mt-2 text-sm text-gray-400">Est. {f.established}</p>

                  <p className="mt-4 text-gray-600 leading-relaxed">{f.description}</p>

                  {/* Highlights */}
                  <div className="grid grid-cols-2 gap-3 mt-6">
                    {f.highlights.map((h) => (
                      <div key={h.label} className="flex items-center gap-2.5 bg-[#f8f9ff] rounded-xl px-4 py-3">
                        <span className="text-xl">{h.icon}</span>
                        <span className="text-sm font-medium text-[#0f1a3e]">{h.label}</span>
                      </div>
                    ))}
                  </div>

                  {/* Features */}
                  <div className="mt-6">
                    <p className="text-sm font-semibold text-gray-500 mb-3 uppercase tracking-wide">Services Available</p>
                    <div className="flex flex-wrap gap-2">
                      {f.features.map((feat) => (
                        <span key={feat} className="text-xs bg-[#EDF2FF] text-[#3B5BDB] font-medium px-3 py-1.5 rounded-full">
                          {feat}
                        </span>
                      ))}
                    </div>
                  </div>

                  {/* Contact */}
                  <div className="mt-6 p-5 bg-[#f8f9ff] rounded-2xl border border-gray-100 space-y-2.5">
                    <div className="flex items-center gap-3 text-sm text-gray-600">
                      <svg className="w-4 h-4 text-[#3B5BDB] shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                      </svg>
                      {f.address}
                    </div>
                    <div className="flex items-center gap-3 text-sm text-gray-600">
                      <svg className="w-4 h-4 text-[#3B5BDB] shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                      </svg>
                      {f.phone}
                    </div>
                  </div>

                  <Link
                    to="/contact"
                    className="mt-5 inline-flex items-center gap-2 px-6 py-3.5 bg-[#3B5BDB] text-white font-semibold rounded-2xl hover:bg-[#2f4bc4] transition-all shadow-lg shadow-blue-200 text-sm"
                  >
                    Schedule a Tour at This Location
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
                    </svg>
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Amenities */}
      <section className="py-20 bg-[#f8f9ff]">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-14">
            <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Comfort & Convenience</span>
            <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e]">Amenities Across All Facilities</h2>
            <p className="mt-4 text-gray-500 max-w-xl mx-auto">
              Every ElderCare location is designed to feel like home — with the services and comfort your loved one deserves.
            </p>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {amenities.map((a) => (
              <div key={a.label} className="bg-white rounded-2xl p-6 border border-gray-100 hover:border-[#3B5BDB]/30 hover:shadow-md transition-all group">
                <div className="text-3xl mb-3">{a.icon}</div>
                <h3 className="font-bold text-[#0f1a3e] mb-1.5 group-hover:text-[#3B5BDB] transition-colors">{a.label}</h3>
                <p className="text-xs text-gray-500 leading-relaxed">{a.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 bg-gradient-to-br from-[#3B5BDB] to-[#1a2f6b]">
        <div className="max-w-3xl mx-auto px-6 text-center">
          <h2 className="text-4xl font-bold text-white">Visit Us In Person</h2>
          <p className="mt-4 text-blue-200 text-lg">
            Nothing compares to seeing our facilities firsthand. Schedule a free tour at any of our locations today.
          </p>
          <Link
            to="/contact"
            className="mt-8 inline-block px-10 py-4 bg-white text-[#3B5BDB] font-bold rounded-2xl hover:bg-blue-50 transition-all shadow-xl text-sm"
          >
            Book a Free Tour
          </Link>
        </div>
      </section>
    </div>
  );
}
