const services = [
  { icon: "🏥", label: "24/7 Nursing Support" },
  { icon: "🤲", label: "Assisted Daily Living" },
  { icon: "🧠", label: "Memory Wellness Program" },
  { icon: "💊", label: "Medication Management" },
  { icon: "🦽", label: "Rehabilitation & Hospice" },
  { icon: "🍽️", label: "Nutrition & Dietary Care" },
  { icon: "🎵", label: "Music & Art Therapy" },
  { icon: "👨‍👩‍👧", label: "Family Support Groups" },
];

export function CareJustForYouSection() {
  return (
    <section className="py-24 bg-[#f8f9ff]">
      <div className="max-w-7xl mx-auto px-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          {/* Left */}
          <div>
            <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Tailored For Every Resident</span>
            <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e] leading-tight">
              Care Made Just For You
            </h2>
            <p className="mt-4 text-gray-500 leading-relaxed">
              We recognize that every person is unique. Our interdisciplinary team creates individualized care plans that respect each resident's preferences, medical needs, and personal goals.
            </p>

            {/* Service grid */}
            <div className="mt-10 grid grid-cols-2 gap-4">
              {services.map((s) => (
                <div
                  key={s.label}
                  className="flex items-center gap-3 bg-white rounded-2xl px-4 py-3.5 border border-gray-100 hover:border-[#3B5BDB]/30 hover:shadow-md transition-all duration-200"
                >
                  <span className="text-2xl">{s.icon}</span>
                  <span className="text-sm font-medium text-[#0f1a3e]">{s.label}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Right – Image stack */}
          <div className="relative">
            <div className="rounded-3xl overflow-hidden shadow-2xl shadow-blue-100">
              <img
                src="https://images.unsplash.com/photo-1581579438747-1dc8d17bbce4?w=700&q=80"
                alt="Nurse helping senior resident"
                className="w-full h-[500px] object-cover"
              />
            </div>
            {/* Floating badge */}
            <div className="absolute -bottom-5 left-6 bg-white rounded-2xl shadow-xl p-4 border border-gray-100">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center">
                  <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-bold text-[#0f1a3e]">Personalized Plans</p>
                  <p className="text-xs text-gray-500">For every resident</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
