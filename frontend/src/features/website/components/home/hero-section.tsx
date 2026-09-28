import { Link } from "react-router";

export function HeroSection() {
  return (
    <section className="relative overflow-hidden bg-gradient-to-br from-[#EDF2FF] via-white to-[#f8f9ff] min-h-[92vh] flex items-center">
      {/* Background decorative blobs */}
      <div className="absolute top-0 right-0 w-[600px] h-[600px] bg-[#3B5BDB]/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/4 pointer-events-none" />
      <div className="absolute bottom-0 left-0 w-[400px] h-[400px] bg-[#748ffc]/8 rounded-full blur-3xl translate-y-1/2 -translate-x-1/4 pointer-events-none" />

      <div className="max-w-7xl mx-auto px-6 py-20 grid grid-cols-1 lg:grid-cols-2 gap-16 items-center relative z-10">
        {/* Left Content */}
        <div className="space-y-8">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 bg-[#EDF2FF] border border-[#3B5BDB]/20 rounded-full px-4 py-2">
            <span className="w-2 h-2 bg-[#3B5BDB] rounded-full animate-pulse" />
            <span className="text-[#3B5BDB] text-sm font-semibold">Trusted Senior Care Since 1999</span>
          </div>

          {/* Heading */}
          <div>
            <h1 className="text-5xl lg:text-6xl font-bold text-[#0f1a3e] leading-[1.1] tracking-tight">
              With You, For
              <br />
              <span className="text-[#3B5BDB] relative">
                Your Loved Ones
                <svg className="absolute -bottom-2 left-0 w-full" viewBox="0 0 300 12" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M2 10C50 4 100 2 150 5C200 8 250 6 298 4" stroke="#3B5BDB" strokeWidth="3" strokeLinecap="round" opacity="0.4"/>
                </svg>
              </span>
            </h1>
            <p className="mt-6 text-lg text-gray-500 leading-relaxed max-w-md">
              Compassionate, personalized nursing and senior care tailored to every resident's needs — because your family matters to us.
            </p>
          </div>

          {/* CTA Buttons */}
          <div className="flex flex-wrap gap-4">
            <Link
              to="/contact"
              className="inline-flex items-center gap-2 px-7 py-4 bg-[#3B5BDB] text-white font-semibold rounded-2xl hover:bg-[#2f4bc4] transition-all duration-200 shadow-xl shadow-blue-200 hover:shadow-blue-300 hover:-translate-y-0.5"
            >
              Schedule a Tour
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </Link>
            <Link
              to="/about"
              className="inline-flex items-center gap-2 px-7 py-4 bg-white text-[#3B5BDB] font-semibold rounded-2xl border-2 border-[#3B5BDB]/20 hover:border-[#3B5BDB] transition-all duration-200 hover:-translate-y-0.5"
            >
              Learn More
            </Link>
          </div>

          {/* Rating */}
          <div className="flex items-center gap-4 pt-2">
            <div className="flex">
              {[...Array(5)].map((_, i) => (
                <svg key={i} className="w-5 h-5 text-amber-400" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
              ))}
            </div>
            <div>
              <span className="font-bold text-[#0f1a3e]">4.9/5</span>
              <span className="text-gray-400 text-sm ml-1">· 499 families trust us</span>
            </div>
          </div>
        </div>

        {/* Right – Image Collage */}
        <div className="relative">
          {/* Main image */}
          <div className="relative rounded-3xl overflow-hidden shadow-2xl shadow-blue-100">
            <img
              src="https://images.unsplash.com/photo-1576765608535-5f04d1e3f289?w=700&q=80"
              alt="Caring nurse with senior patient"
              className="w-full h-[520px] object-cover"
            />
            {/* Overlay gradient */}
            <div className="absolute inset-0 bg-gradient-to-t from-[#0f1a3e]/20 to-transparent" />
          </div>

          {/* Floating card – Stats */}
          <div className="absolute -bottom-6 -left-6 bg-white rounded-2xl shadow-xl p-5 border border-gray-100">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-[#EDF2FF] rounded-xl flex items-center justify-center">
                <svg className="w-6 h-6 text-[#3B5BDB]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </div>
              <div>
                <p className="text-2xl font-bold text-[#0f1a3e]">500+</p>
                <p className="text-xs text-gray-500">Happy Residents</p>
              </div>
            </div>
          </div>

          {/* Floating card – Available */}
          <div className="absolute -top-4 -right-4 bg-[#3B5BDB] text-white rounded-2xl shadow-xl px-5 py-4">
            <p className="text-sm font-bold">24/7 Care</p>
            <p className="text-xs text-blue-200">Always Available</p>
          </div>

          {/* Small image */}
          <div className="absolute top-8 -left-10 w-28 h-28 rounded-2xl overflow-hidden shadow-xl border-4 border-white hidden lg:block">
            <img
              src="https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=200&q=80"
              alt="Senior activity"
              className="w-full h-full object-cover"
            />
          </div>
        </div>
      </div>
    </section>
  );
}
