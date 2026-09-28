import { Link } from "react-router";

export function CtaBannerSection() {
  return (
    <section className="py-24 bg-[#f8f9ff]">
      <div className="max-w-7xl mx-auto px-6">
        <div className="relative bg-gradient-to-br from-[#3B5BDB] to-[#1a2f6b] rounded-3xl overflow-hidden">
          {/* Background decoration */}
          <div className="absolute inset-0 opacity-10">
            <div className="absolute -top-20 -right-20 w-80 h-80 bg-white rounded-full blur-3xl" />
            <div className="absolute -bottom-20 -left-20 w-80 h-80 bg-white rounded-full blur-3xl" />
          </div>

          <div className="relative z-10 grid grid-cols-1 lg:grid-cols-2 gap-10 items-center p-12 lg:p-16">
            {/* Text */}
            <div>
              <h2 className="text-4xl lg:text-5xl font-bold text-white leading-tight">
                Ready to Find the Right Care?
              </h2>
              <p className="mt-5 text-blue-200 text-lg leading-relaxed">
                Our care coordinators are ready to help you navigate your options and find the perfect fit for your loved one. Schedule a free consultation today.
              </p>
              <div className="mt-8 flex flex-wrap gap-4">
                <Link
                  to="/contact"
                  className="px-8 py-4 bg-white text-[#3B5BDB] font-bold rounded-2xl hover:bg-blue-50 transition-all duration-200 shadow-lg"
                >
                  Schedule a Visit
                </Link>
                <a
                  href="tel:9165559000"
                  className="px-8 py-4 bg-white/10 text-white font-semibold rounded-2xl border border-white/20 hover:bg-white/20 transition-all duration-200"
                >
                  Call (916) 555-9000
                </a>
              </div>
            </div>

            {/* Image */}
            <div className="relative rounded-2xl overflow-hidden h-64 lg:h-80 shadow-2xl">
              <img
                src="https://images.unsplash.com/photo-1607990281513-2c110a25bd8c?w=600&q=80"
                alt="Care team ready to help"
                className="w-full h-full object-cover"
              />
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
