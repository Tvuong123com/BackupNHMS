import { Link } from "react-router";

const footerLinks = {
  Services: [
    { label: "Skilled Nursing", to: "/services" },
    { label: "Memory Care", to: "/services" },
    { label: "Physical Therapy", to: "/services" },
    { label: "Recreational Therapy", to: "/services" },
    { label: "Family Support", to: "/services" },
  ],
  Company: [
    { label: "About Us", to: "/about" },
    { label: "Our Facilities", to: "/facilities" },
    { label: "Our Team", to: "/team" },
    { label: "Activities", to: "/activities" },
  ],
  Admissions: [
    { label: "Admission Process", to: "/admissions" },
    { label: "Schedule a Visit", to: "/contact" },
    { label: "FAQ", to: "/contact" },
    { label: "Staff Login", to: "/login" },
  ],
};


export function WebsiteFooter() {
  return (
    <footer className="bg-[#0f1a3e] text-white">
      {/* Main Footer */}
      <div className="max-w-7xl mx-auto px-6 py-16">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-12">
          {/* Brand */}
          <div className="lg:col-span-2">
            <Link to="/" className="flex items-center gap-2.5 mb-5">
              <div className="w-9 h-9 rounded-xl bg-[#3B5BDB] flex items-center justify-center">
                <svg viewBox="0 0 24 24" fill="none" className="w-5 h-5 text-white" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 11l3-3 3 3M9 15l3-3 3 3" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </div>
              <span className="text-xl font-bold">
                Elder<span className="text-[#748ffc]">Care</span>
              </span>
            </Link>
            <p className="text-gray-400 text-sm leading-relaxed mb-6 max-w-xs">
              Providing compassionate, personalized nursing and senior care since 1999. Your loved ones deserve the very best.
            </p>
            {/* Contact Info */}
            <div className="space-y-3 text-sm text-gray-400">
              <div className="flex items-start gap-3">
                <svg className="w-4 h-4 text-[#748ffc] mt-0.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                <span>1234 Elder Lane, Sacramento, CA 95814</span>
              </div>
              <div className="flex items-center gap-3">
                <svg className="w-4 h-4 text-[#748ffc] shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                </svg>
                <span>(916) 555-9000</span>
              </div>
              <div className="flex items-center gap-3">
                <svg className="w-4 h-4 text-[#748ffc] shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>Mon–Fri: 8am–6pm · 24/7 Emergency Care</span>
              </div>
            </div>
          </div>

          {/* Links */}
          {Object.entries(footerLinks).map(([title, links]) => (
            <div key={title}>
              <h4 className="text-white font-semibold text-sm uppercase tracking-wider mb-5">{title}</h4>
              <ul className="space-y-3">
                {links.map((link) => (
                  <li key={link.label}>
                    <Link
                      to={link.to}
                      className="text-gray-400 text-sm hover:text-[#748ffc] transition-colors duration-200"
                    >
                      {link.label}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </div>

      {/* Bottom Bar */}
      <div className="border-t border-white/10">
        <div className="max-w-7xl mx-auto px-6 py-5 flex flex-col sm:flex-row items-center justify-between gap-3">
          <p className="text-gray-500 text-xs">
            © {new Date().getFullYear()} ElderCare Nursing & Senior Care. All rights reserved.
          </p>
          <div className="flex items-center gap-4 text-xs text-gray-500">
            <a href="#" className="hover:text-gray-300 transition-colors">Privacy Policy</a>
            <a href="#" className="hover:text-gray-300 transition-colors">Terms of Service</a>
            <a href="#" className="hover:text-gray-300 transition-colors">Accessibility</a>
          </div>
        </div>
      </div>
    </footer>
  );
}
