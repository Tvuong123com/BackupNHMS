import { useState } from "react";

const careTypes = [
  "Skilled Nursing Care",
  "Memory Care",
  "Physical Therapy",
  "Occupational Therapy",
  "Hospice & Palliative Care",
  "Recreational Therapy",
  "Other",
];

const contactInfo = [
  {
    icon: (
      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
      </svg>
    ),
    title: "Address",
    value: "1234 Elder Lane, Sacramento, CA 95814",
  },
  {
    icon: (
      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
      </svg>
    ),
    title: "Phone",
    value: "(916) 555-9000",
  },
  {
    icon: (
      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
      </svg>
    ),
    title: "Email",
    value: "care@eldercare.com",
  },
  {
    icon: (
      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    ),
    title: "Hours",
    value: "Mon–Fri: 8am–6pm · 24/7 Emergency",
  },
];

const faqs = [
  { q: "How quickly can we arrange a tour?", a: "We can typically arrange a facility tour within 24–48 hours of your request. Tours are free and include a meet-and-greet with our care coordinator." },
  { q: "What should I bring to the first meeting?", a: "Please bring any relevant medical records, a list of current medications, insurance information, and any legal documents such as power of attorney." },
  { q: "Is there a waitlist for admission?", a: "Availability varies by care type. We recommend reaching out early so we can assess needs and discuss options, including our waitlist process if needed." },
  { q: "Do you offer short-term or respite care?", a: "Yes. We offer short-term rehabilitation stays, respite care for family caregivers, and trial stays so residents can experience our community before committing." },
];

export default function ContactPage() {
  const [formData, setFormData] = useState({
    firstName: "", lastName: "", phone: "", email: "", careType: "", message: "",
  });
  const [submitted, setSubmitted] = useState(false);
  const [openFaq, setOpenFaq] = useState<number | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
  };

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-[#0f1a3e] to-[#1e3a8a] py-28 relative overflow-hidden">
        <div className="absolute inset-0 opacity-10">
          <div className="absolute top-0 right-0 w-96 h-96 bg-white rounded-full blur-3xl translate-x-1/2 -translate-y-1/2" />
          <div className="absolute bottom-0 left-0 w-64 h-64 bg-white rounded-full blur-3xl -translate-x-1/2 translate-y-1/2" />
        </div>
        <div className="max-w-7xl mx-auto px-6 relative z-10 text-center">
          <span className="inline-block bg-white/10 text-blue-200 text-sm font-semibold px-4 py-2 rounded-full border border-white/20 mb-6">
            We're Here For You
          </span>
          <h1 className="text-5xl lg:text-6xl font-bold text-white leading-tight">
            Let's Find The Right
            <br />
            <span className="text-[#748ffc]">Care Together</span>
          </h1>
          <p className="mt-6 text-blue-200 text-xl max-w-2xl mx-auto leading-relaxed">
            Our care coordinators are ready to answer your questions, discuss options, and help you take the next step — with no pressure and no obligation.
          </p>
        </div>
      </section>

      {/* Contact Form + Info */}
      <section className="py-24 bg-white">
        <div className="max-w-7xl mx-auto px-6 grid grid-cols-1 lg:grid-cols-3 gap-16">
          {/* Contact Info */}
          <div className="space-y-6">
            <div>
              <h2 className="text-2xl font-bold text-[#0f1a3e]">Get in Touch</h2>
              <p className="mt-2 text-gray-500 text-sm leading-relaxed">
                Whether you're just beginning to explore options or ready to make a decision — we're here to help.
              </p>
            </div>

            <div className="space-y-4">
              {contactInfo.map((c) => (
                <div key={c.title} className="flex items-start gap-4">
                  <div className="w-11 h-11 bg-[#EDF2FF] text-[#3B5BDB] rounded-xl flex items-center justify-center shrink-0">
                    {c.icon}
                  </div>
                  <div>
                    <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">{c.title}</p>
                    <p className="text-[#0f1a3e] font-semibold text-sm mt-0.5">{c.value}</p>
                  </div>
                </div>
              ))}
            </div>

            {/* Map placeholder */}
            <div className="rounded-2xl overflow-hidden border border-gray-100 shadow-md">
              <img
                src="https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=500&q=80"
                alt="Our location"
                className="w-full h-44 object-cover"
              />
              <div className="p-4 bg-[#EDF2FF]">
                <p className="text-[#3B5BDB] text-sm font-semibold">📍 1234 Elder Lane, Sacramento CA</p>
                <p className="text-xs text-gray-500 mt-1">Free parking available on-site</p>
              </div>
            </div>
          </div>

          {/* Form */}
          <div className="lg:col-span-2">
            {submitted ? (
              <div className="h-full flex items-center justify-center">
                <div className="text-center p-12">
                  <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
                    <svg className="w-10 h-10 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                  <h3 className="text-2xl font-bold text-[#0f1a3e]">Thank You!</h3>
                  <p className="mt-3 text-gray-500">
                    We've received your message and a care coordinator will reach out within 24 hours.
                  </p>
                  <button
                    onClick={() => setSubmitted(false)}
                    className="mt-6 text-[#3B5BDB] font-semibold hover:underline text-sm"
                  >
                    Send another message
                  </button>
                </div>
              </div>
            ) : (
              <div className="bg-[#f8f9ff] rounded-3xl p-8 border border-gray-100">
                <h2 className="text-2xl font-bold text-[#0f1a3e] mb-8">Tell Us About Your Needs</h2>
                <form onSubmit={handleSubmit} className="space-y-6">
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">First Name *</label>
                      <input
                        type="text"
                        required
                        value={formData.firstName}
                        onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                        className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-[#3B5BDB]/30 focus:border-[#3B5BDB] transition-all"
                        placeholder="John"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">Last Name *</label>
                      <input
                        type="text"
                        required
                        value={formData.lastName}
                        onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                        className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-[#3B5BDB]/30 focus:border-[#3B5BDB] transition-all"
                        placeholder="Smith"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">Phone Number</label>
                      <input
                        type="tel"
                        value={formData.phone}
                        onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                        className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-[#3B5BDB]/30 focus:border-[#3B5BDB] transition-all"
                        placeholder="(916) 555-0000"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">Email Address *</label>
                      <input
                        type="email"
                        required
                        value={formData.email}
                        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-[#3B5BDB]/30 focus:border-[#3B5BDB] transition-all"
                        placeholder="john@email.com"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Care Type Needed</label>
                    <select
                      value={formData.careType}
                      onChange={(e) => setFormData({ ...formData, careType: e.target.value })}
                      className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-[#3B5BDB]/30 focus:border-[#3B5BDB] transition-all text-gray-700"
                    >
                      <option value="">Select a care type...</option>
                      {careTypes.map((t) => <option key={t} value={t}>{t}</option>)}
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Message</label>
                    <textarea
                      rows={5}
                      value={formData.message}
                      onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                      className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-[#3B5BDB]/30 focus:border-[#3B5BDB] transition-all resize-none"
                      placeholder="Tell us about your loved one's needs, any questions you have, or preferred times to connect..."
                    />
                  </div>

                  <button
                    type="submit"
                    className="w-full py-4 bg-[#3B5BDB] text-white font-bold rounded-2xl hover:bg-[#2f4bc4] transition-all duration-200 shadow-xl shadow-blue-200 hover:shadow-blue-300 hover:-translate-y-0.5 text-sm"
                  >
                    Send Message — We'll Respond Within 24 Hours
                  </button>
                </form>
              </div>
            )}
          </div>
        </div>
      </section>

      {/* FAQ */}
      <section className="py-20 bg-[#f8f9ff]">
        <div className="max-w-3xl mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold text-[#0f1a3e]">What Families Ask Most</h2>
            <p className="mt-4 text-gray-500">Quick answers to the questions we hear from families every day.</p>
          </div>
          <div className="space-y-4">
            {faqs.map((faq, i) => (
              <div
                key={i}
                className={`border rounded-2xl overflow-hidden transition-all duration-300 ${openFaq === i ? "border-[#3B5BDB]/30 shadow-md bg-white" : "border-gray-200 bg-white"}`}
              >
                <button
                  onClick={() => setOpenFaq(openFaq === i ? null : i)}
                  className="w-full flex items-center justify-between p-6 text-left"
                >
                  <span className="font-semibold text-[#0f1a3e] text-sm">{faq.q}</span>
                  <svg className={`w-5 h-5 text-[#3B5BDB] shrink-0 ml-4 transition-transform duration-300 ${openFaq === i ? "rotate-180" : ""}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
                {openFaq === i && (
                  <div className="px-6 pb-6 text-gray-500 text-sm leading-relaxed border-t border-gray-50 pt-4">
                    {faq.a}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
