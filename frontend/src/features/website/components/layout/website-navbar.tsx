import { useState, useEffect, useRef } from "react";
import { NavLink, Link, useLocation } from "react-router";

type NavItem = {
  label: string;
  to?: string;
  children?: { label: string; to: string; desc: string }[];
};

const navItems: NavItem[] = [
  { label: "Home", to: "/" },
  {
    label: "About",
    children: [
      { label: "About Us", to: "/about", desc: "Our story, mission & journey" },
      { label: "Our Facilities", to: "/facilities", desc: "Tour our 3 campuses" },
      { label: "Our Team", to: "/team", desc: "Meet the care professionals" },
    ],
  },
  { label: "Services", to: "/services" },
  { label: "Activities", to: "/activities" },
  { label: "Admissions", to: "/admissions" },
  { label: "Contact", to: "/contact" },
];

export function WebsiteNavbar() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [openDropdown, setOpenDropdown] = useState<string | null>(null);
  const location = useLocation();
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  useEffect(() => {
    setOpenDropdown(null);
    setMobileOpen(false);
  }, [location]);

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setOpenDropdown(null);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        scrolled ? "bg-white shadow-md py-3" : "bg-white/95 py-4"
      }`}
    >
      <div className="max-w-7xl mx-auto px-6 flex items-center justify-between" ref={dropdownRef}>
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2.5 group shrink-0">
          <div className="w-9 h-9 rounded-xl bg-[#3B5BDB] flex items-center justify-center shadow-lg shadow-blue-200">
            <svg viewBox="0 0 24 24" fill="none" className="w-5 h-5 text-white">
              <path d="M9 11l3-3 3 3M9 15l3-3 3 3" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </div>
          <div>
            <span className="text-xl font-bold text-[#1a2340] tracking-tight">Elder</span>
            <span className="text-xl font-bold text-[#3B5BDB]">Care</span>
          </div>
        </Link>

        {/* Desktop Nav */}
        <nav className="hidden lg:flex items-center gap-0.5">
          {navItems.map((item) => {
            if (item.to) {
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.to === "/"}
                  className={({ isActive }) =>
                    `px-3.5 py-2 rounded-lg text-sm font-medium transition-all duration-200 whitespace-nowrap ${
                      isActive
                        ? "text-[#3B5BDB] bg-[#EDF2FF]"
                        : "text-gray-600 hover:text-[#3B5BDB] hover:bg-[#f0f4ff]"
                    }`
                  }
                >
                  {item.label}
                </NavLink>
              );
            }

            const isOpen = openDropdown === item.label;
            const isChildActive = item.children?.some((c) => location.pathname === c.to);

            return (
              <div key={item.label} className="relative">
                <button
                  onClick={() => setOpenDropdown(isOpen ? null : item.label)}
                  className={`flex items-center gap-1 px-3.5 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
                    isChildActive || isOpen
                      ? "text-[#3B5BDB] bg-[#EDF2FF]"
                      : "text-gray-600 hover:text-[#3B5BDB] hover:bg-[#f0f4ff]"
                  }`}
                >
                  {item.label}
                  <svg
                    className={`w-3.5 h-3.5 transition-transform duration-200 ${isOpen ? "rotate-180" : ""}`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>

                {isOpen && (
                  <div className="absolute top-full left-0 mt-2 w-56 bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden z-50">
                    {item.children!.map((child) => (
                      <Link
                        key={child.to}
                        to={child.to}
                        className="flex flex-col px-4 py-3.5 hover:bg-[#f0f4ff] transition-colors group border-b border-gray-50 last:border-0"
                      >
                        <span className="text-sm font-semibold text-[#0f1a3e] group-hover:text-[#3B5BDB] transition-colors">
                          {child.label}
                        </span>
                        <span className="text-xs text-gray-400 mt-0.5">{child.desc}</span>
                      </Link>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </nav>

        {/* Desktop CTA */}
        <div className="hidden lg:flex items-center gap-3 shrink-0">
          <Link
            to="/login"
            className="text-sm text-gray-500 hover:text-[#3B5BDB] transition-colors font-medium"
          >
            Staff Login
          </Link>
          <Link
            to="/contact"
            className="px-5 py-2.5 bg-[#3B5BDB] text-white text-sm font-semibold rounded-xl hover:bg-[#2f4bc4] transition-all duration-200 shadow-md shadow-blue-200 hover:shadow-blue-300"
          >
            Schedule a Visit
          </Link>
        </div>

        {/* Mobile hamburger */}
        <button
          className="lg:hidden flex flex-col gap-1.5 p-2"
          onClick={() => setMobileOpen(!mobileOpen)}
          aria-label="Toggle menu"
        >
          <span className={`block w-6 h-0.5 bg-gray-700 transition-all ${mobileOpen ? "rotate-45 translate-y-2" : ""}`} />
          <span className={`block w-6 h-0.5 bg-gray-700 transition-all ${mobileOpen ? "opacity-0" : ""}`} />
          <span className={`block w-6 h-0.5 bg-gray-700 transition-all ${mobileOpen ? "-rotate-45 -translate-y-2" : ""}`} />
        </button>
      </div>

      {/* Mobile Menu */}
      {mobileOpen && (
        <div className="lg:hidden bg-white border-t border-gray-100 px-6 py-4 flex flex-col gap-1 max-h-[80vh] overflow-y-auto">
          {navItems.map((item) => {
            if (item.to) {
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.to === "/"}
                  onClick={() => setMobileOpen(false)}
                  className={({ isActive }) =>
                    `px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
                      isActive ? "text-[#3B5BDB] bg-[#EDF2FF]" : "text-gray-600"
                    }`
                  }
                >
                  {item.label}
                </NavLink>
              );
            }

            return (
              <div key={item.label}>
                <p className="px-4 py-2 text-xs font-bold text-gray-400 uppercase tracking-wider">{item.label}</p>
                {item.children!.map((child) => (
                  <NavLink
                    key={child.to}
                    to={child.to}
                    onClick={() => setMobileOpen(false)}
                    className={({ isActive }) =>
                      `block pl-8 pr-4 py-2.5 rounded-lg text-sm transition-colors ${
                        isActive ? "text-[#3B5BDB] font-semibold" : "text-gray-600"
                      }`
                    }
                  >
                    {child.label}
                  </NavLink>
                ))}
              </div>
            );
          })}

          <div className="border-t border-gray-100 pt-3 mt-2 space-y-2">
            <Link
              to="/login"
              onClick={() => setMobileOpen(false)}
              className="block px-4 py-3 text-sm text-gray-500 font-medium"
            >
              Staff Login
            </Link>
            <Link
              to="/contact"
              onClick={() => setMobileOpen(false)}
              className="block px-5 py-3 bg-[#3B5BDB] text-white text-sm font-semibold rounded-xl text-center"
            >
              Schedule a Visit
            </Link>
          </div>
        </div>
      )}
    </header>
  );
}
