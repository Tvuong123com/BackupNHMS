import { useState } from "react";
import { Link } from "react-router";

const departments = [
  { id: "all", label: "All Team" },
  { id: "don", label: "Nursing Leadership" },
  { id: "nurse", label: "Nursing Staff" },
  { id: "cna", label: "Care Assistants" },
  { id: "therapy", label: "Therapy Team" },
  { id: "admin", label: "Administration" },
];

const team = [
  {
    name: "Dr. Margaret Collins",
    role: "Medical Director",
    dept: "don",
    img: "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400&q=80",
    bio: "Board-certified geriatrician with 20+ years of experience in senior care medicine.",
    credentials: ["MD, Geriatric Medicine", "Board Certified", "20+ Years Experience"],
    highlight: true,
  },
  {
    name: "Rebecca Torres, RN",
    role: "Director of Nursing (DON)",
    dept: "don",
    img: "https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=400&q=80",
    bio: "Oversees all nursing operations and care quality across our three campuses.",
    credentials: ["BSN, Registered Nurse", "Certified DON", "15 Years in SNF Care"],
    highlight: true,
  },
  {
    name: "James Kim, RN",
    role: "Charge Nurse – Main Campus",
    dept: "nurse",
    img: "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400&q=80",
    bio: "Specializes in post-surgical recovery and complex medical care.",
    credentials: ["BSN, Registered Nurse", "CCRN Certified"],
    highlight: false,
  },
  {
    name: "Aisha Johnson, RN",
    role: "Memory Care Nurse",
    dept: "nurse",
    img: "https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=400&q=80",
    bio: "Dementia care specialist with expertise in person-centered approaches.",
    credentials: ["BSN, Registered Nurse", "Dementia Care Certified"],
    highlight: false,
  },
  {
    name: "Carlos Mendez, RN",
    role: "Night Shift Charge Nurse",
    dept: "nurse",
    img: "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400&q=80",
    bio: "Dedicated to ensuring residents receive the same quality care around the clock.",
    credentials: ["ADN, Registered Nurse", "10 Years Experience"],
    highlight: false,
  },
  {
    name: "Lisa Park, CNA",
    role: "Senior Care Assistant",
    dept: "cna",
    img: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&q=80",
    bio: "Beloved by residents for her warm personality and attentive daily care.",
    credentials: ["Certified Nursing Assistant", "Resident Satisfaction Award 2024"],
    highlight: false,
  },
  {
    name: "David Okafor, CNA",
    role: "Care Assistant – Riverside",
    dept: "cna",
    img: "https://images.unsplash.com/photo-1607990281513-2c110a25bd8c?w=400&q=80",
    bio: "Passionate about making each resident's day meaningful and comfortable.",
    credentials: ["Certified Nursing Assistant", "CPR & First Aid Certified"],
    highlight: false,
  },
  {
    name: "Dr. Anna Reeves, PT",
    role: "Physical Therapy Director",
    dept: "therapy",
    img: "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400&q=80",
    bio: "Leads our rehabilitation program helping residents regain strength and independence.",
    credentials: ["DPT, Physical Therapy", "Geriatric Specialist", "Board Certified"],
    highlight: false,
  },
  {
    name: "Michael Santos, OT",
    role: "Occupational Therapist",
    dept: "therapy",
    img: "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=400&q=80",
    bio: "Helps residents re-learn daily living skills and adapt their environment for safety.",
    credentials: ["MOT, Occupational Therapy", "AOTA Member"],
    highlight: false,
  },
  {
    name: "Sarah Mitchell",
    role: "Admission Coordinator",
    dept: "admin",
    img: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400&q=80",
    bio: "First point of contact for families — guides them through the admission process with compassion.",
    credentials: ["BSW, Social Work", "5 Years in Senior Care"],
    highlight: false,
  },
  {
    name: "Tom Bradley",
    role: "Activities Director",
    dept: "admin",
    img: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&q=80",
    bio: "Designs and leads the vibrant activity programs that enrich residents' daily lives.",
    credentials: ["Recreation Therapy Certified", "Music Therapy Training"],
    highlight: false,
  },
  {
    name: "Nina Patel, RD",
    role: "Registered Dietitian",
    dept: "admin",
    img: "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=400&q=80",
    bio: "Creates individualized nutrition plans to support health and recovery for each resident.",
    credentials: ["RD, Registered Dietitian", "Geriatric Nutrition Specialist"],
    highlight: false,
  },
];

const values = [
  { icon: "❤️", title: "Compassion First", desc: "Every team member is trained to lead with empathy and respect in every interaction." },
  { icon: "🎓", title: "Continuous Education", desc: "Ongoing training and certification programs keep our staff at the leading edge of senior care." },
  { icon: "🤝", title: "Team Collaboration", desc: "Interdisciplinary rounds and communication ensure coordinated, holistic care." },
  { icon: "🏆", title: "Excellence Standards", desc: "We hold ourselves to the highest clinical and service standards — every shift, every day." },
];

export default function TeamPage() {
  const [activeTab, setActiveTab] = useState("all");

  const filtered = activeTab === "all" ? team : team.filter((m) => m.dept === activeTab);
  const leaders = filtered.filter((m) => m.highlight);
  const members = filtered.filter((m) => !m.highlight);

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-[#0f1a3e] to-[#1e3a8a] py-28 relative overflow-hidden">
        <div className="absolute inset-0 opacity-10 pointer-events-none">
          <div className="absolute top-0 right-0 w-96 h-96 bg-white rounded-full blur-3xl translate-x-1/2 -translate-y-1/2" />
        </div>
        <div className="max-w-7xl mx-auto px-6 relative z-10 grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <div>
            <span className="inline-block bg-white/10 text-blue-200 text-sm font-semibold px-4 py-2 rounded-full border border-white/20 mb-6">
              The People Behind the Care
            </span>
            <h1 className="text-5xl lg:text-6xl font-bold text-white leading-tight">
              Meet Our
              <br />
              <span className="text-[#748ffc]">Care Team</span>
            </h1>
            <p className="mt-6 text-blue-200 text-lg leading-relaxed">
              Behind every resident's smile is a team of dedicated nurses, therapists, and care assistants who go above and beyond every single day.
            </p>
            <div className="mt-8 grid grid-cols-3 gap-6">
              {[
                { value: "120+", label: "Team Members" },
                { value: "DON", label: "Nursing Leadership" },
                { value: "24/7", label: "Coverage" },
              ].map((s) => (
                <div key={s.label} className="text-center bg-white/10 rounded-2xl p-4 border border-white/10">
                  <p className="text-2xl font-bold text-white">{s.value}</p>
                  <p className="text-xs text-blue-200 mt-1">{s.label}</p>
                </div>
              ))}
            </div>
          </div>
          {/* Stacked staff photos */}
          <div className="relative h-80 lg:h-96 hidden lg:block">
            <img src="https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=300&q=80" alt="Team" className="absolute top-0 left-0 w-40 h-52 rounded-2xl object-cover shadow-xl border-4 border-white/20" />
            <img src="https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=300&q=80" alt="Team" className="absolute top-4 left-36 w-44 h-60 rounded-2xl object-cover shadow-xl border-4 border-white/20" />
            <img src="https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=300&q=80" alt="Team" className="absolute top-20 left-72 w-40 h-52 rounded-2xl object-cover shadow-xl border-4 border-white/20" />
            <img src="https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=300&q=80" alt="Team" className="absolute bottom-0 left-16 w-36 h-48 rounded-2xl object-cover shadow-xl border-4 border-white/20" />
          </div>
        </div>
      </section>

      {/* Team Values */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {values.map((v) => (
              <div key={v.title} className="text-center p-6 rounded-2xl border border-gray-100 hover:border-[#3B5BDB]/30 hover:shadow-md transition-all">
                <div className="text-4xl mb-4">{v.icon}</div>
                <h3 className="font-bold text-[#0f1a3e] mb-2">{v.title}</h3>
                <p className="text-sm text-gray-500 leading-relaxed">{v.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Team Directory */}
      <section className="py-20 bg-[#f8f9ff]">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold text-[#0f1a3e]">Our Team Directory</h2>
            <p className="mt-4 text-gray-500 max-w-xl mx-auto">
              Get to know the professionals who will care for your loved one every day.
            </p>
          </div>

          {/* Department Tabs */}
          <div className="flex flex-wrap justify-center gap-2 mb-12">
            {departments.map((d) => (
              <button
                key={d.id}
                onClick={() => setActiveTab(d.id)}
                className={`px-5 py-2.5 rounded-xl text-sm font-semibold transition-all duration-200 ${
                  activeTab === d.id
                    ? "bg-[#3B5BDB] text-white shadow-lg shadow-blue-200"
                    : "bg-white text-gray-600 border border-gray-200 hover:border-[#3B5BDB]/40 hover:text-[#3B5BDB]"
                }`}
              >
                {d.label}
              </button>
            ))}
          </div>

          {/* Leadership (highlight) */}
          {leaders.length > 0 && (
            <div className="mb-12">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-6">Leadership</p>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {leaders.map((m) => (
                  <div key={m.name} className="bg-gradient-to-br from-[#3B5BDB] to-[#1a2f6b] rounded-3xl p-1 shadow-xl shadow-blue-100">
                    <div className="bg-white rounded-[calc(1.5rem-4px)] p-6 flex gap-5">
                      <img src={m.img} alt={m.name} className="w-24 h-28 rounded-2xl object-cover shrink-0 shadow-md" />
                      <div>
                        <span className="text-xs bg-[#EDF2FF] text-[#3B5BDB] font-bold px-2.5 py-1 rounded-full">{m.role}</span>
                        <h3 className="mt-2 text-xl font-bold text-[#0f1a3e]">{m.name}</h3>
                        <p className="mt-1.5 text-sm text-gray-500">{m.bio}</p>
                        <div className="mt-3 flex flex-wrap gap-1.5">
                          {m.credentials.map((c) => (
                            <span key={c} className="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded-lg">{c}</span>
                          ))}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Other Members */}
          {members.length > 0 && (
            <div>
              {leaders.length > 0 && <p className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-6">Team Members</p>}
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {members.map((m) => (
                  <div key={m.name} className="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-lg hover:-translate-y-1 transition-all duration-300 group">
                    <div className="relative h-48 overflow-hidden">
                      <img src={m.img} alt={m.name} className="w-full h-full object-cover object-top group-hover:scale-105 transition-transform duration-500" />
                      <div className="absolute inset-0 bg-gradient-to-t from-[#0f1a3e]/60 to-transparent" />
                      <div className="absolute bottom-4 left-4">
                        <p className="text-white font-bold text-lg leading-tight">{m.name}</p>
                        <p className="text-blue-200 text-xs">{m.role}</p>
                      </div>
                    </div>
                    <div className="p-5">
                      <p className="text-sm text-gray-500 leading-relaxed mb-3">{m.bio}</p>
                      <div className="flex flex-wrap gap-1.5">
                        {m.credentials.map((c) => (
                          <span key={c} className="text-xs text-[#3B5BDB] bg-[#EDF2FF] px-2.5 py-1 rounded-full font-medium">{c}</span>
                        ))}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {filtered.length === 0 && (
            <p className="text-center text-gray-400 py-12">No team members in this category.</p>
          )}
        </div>
      </section>

      {/* Join Our Team CTA */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="bg-gradient-to-br from-[#f8f9ff] to-[#EDF2FF] rounded-3xl p-10 lg:p-16 grid grid-cols-1 lg:grid-cols-2 gap-10 items-center border border-[#3B5BDB]/10">
            <div>
              <span className="text-[#3B5BDB] text-sm font-semibold uppercase tracking-widest">Join the Family</span>
              <h2 className="mt-3 text-4xl font-bold text-[#0f1a3e] leading-tight">
                Passionate About Senior Care?
              </h2>
              <p className="mt-4 text-gray-500 leading-relaxed">
                We're always looking for compassionate nurses, CNAs, therapists, and support staff to join our growing team. Competitive pay, flexible schedules, and a mission-driven workplace await.
              </p>
              <div className="mt-6 flex flex-wrap gap-3">
                <Link
                  to="/contact"
                  className="px-7 py-3.5 bg-[#3B5BDB] text-white font-semibold rounded-2xl hover:bg-[#2f4bc4] transition-all shadow-lg shadow-blue-200 text-sm"
                >
                  View Open Positions
                </Link>
                <a
                  href="mailto:careers@eldercare.com"
                  className="px-7 py-3.5 bg-white text-[#3B5BDB] font-semibold rounded-2xl border-2 border-[#3B5BDB]/20 hover:border-[#3B5BDB] transition-all text-sm"
                >
                  Send Your Resume
                </a>
              </div>
            </div>
            <div className="rounded-2xl overflow-hidden shadow-xl h-64">
              <img
                src="https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=600&q=80"
                alt="Happy care team"
                className="w-full h-full object-cover"
              />
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
