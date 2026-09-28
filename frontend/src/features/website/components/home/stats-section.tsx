import { useEffect, useRef, useState } from "react";

const stats = [
  { value: 30, suffix: "+", label: "Years of Excellence" },
  { value: 120, suffix: "+", label: "Dedicated Staff" },
  { value: 500, suffix: "+", label: "Residents Served" },
  { value: 24, suffix: "/7", label: "Emergency Support" },
];

function useCountUp(target: number, duration = 2000, triggered = false) {
  const [count, setCount] = useState(0);
  useEffect(() => {
    if (!triggered) return;
    let start = 0;
    const step = target / (duration / 16);
    const timer = setInterval(() => {
      start += step;
      if (start >= target) {
        setCount(target);
        clearInterval(timer);
      } else {
        setCount(Math.floor(start));
      }
    }, 16);
    return () => clearInterval(timer);
  }, [target, duration, triggered]);
  return count;
}

function StatItem({ value, suffix, label }: { value: number; suffix: string; label: string }) {
  const [triggered, setTriggered] = useState(false);
  const ref = useRef<HTMLDivElement>(null);
  const count = useCountUp(value, 1800, triggered);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => { if (entry.isIntersecting) { setTriggered(true); observer.disconnect(); } },
      { threshold: 0.3 }
    );
    if (ref.current) observer.observe(ref.current);
    return () => observer.disconnect();
  }, []);

  return (
    <div ref={ref} className="text-center group">
      <p className="text-5xl lg:text-6xl font-bold text-white">
        {count}
        <span className="text-[#748ffc]">{suffix}</span>
      </p>
      <p className="mt-2 text-blue-200 text-sm font-medium">{label}</p>
    </div>
  );
}

export function StatsSection() {
  return (
    <section className="py-20 bg-gradient-to-r from-[#1a2f6b] to-[#3B5BDB] relative overflow-hidden">
      {/* Decorative */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-0 left-1/4 w-64 h-64 bg-white rounded-full blur-3xl" />
        <div className="absolute bottom-0 right-1/4 w-64 h-64 bg-white rounded-full blur-3xl" />
      </div>

      <div className="max-w-7xl mx-auto px-6 relative z-10">
        <div className="text-center mb-14">
          <h2 className="text-3xl font-bold text-white">A Glimpse Into Our Impact</h2>
          <p className="mt-3 text-blue-200">Numbers that reflect our commitment to excellence.</p>
        </div>
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-12">
          {stats.map((s) => (
            <StatItem key={s.label} {...s} />
          ))}
        </div>
      </div>
    </section>
  );
}
