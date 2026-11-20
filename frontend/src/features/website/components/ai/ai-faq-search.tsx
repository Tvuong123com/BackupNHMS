import React, { useState } from "react";
import { websiteAiService, type FaqItem } from "../../services/website-ai-service";

interface AiFaqSearchProps {
  availableFaqs?: FaqItem[];
  context?: string;
  placeholder?: string;
  accentColor?: string;
}

export const AiFaqSearch: React.FC<AiFaqSearchProps> = ({
  availableFaqs = [],
  context = "ElderCare Services",
  placeholder = "Ask any question about our care, pricing, or admissions...",
  accentColor = "#3B5BDB",
}) => {
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [answer, setAnswer] = useState<string | null>(null);
  const [fallback, setFallback] = useState(false);
  const [searchedQuery, setSearchedQuery] = useState<string | null>(null);

  const handleSearch = async (textToSearch?: string) => {
    const text = (textToSearch || query).trim();
    if (!text || loading) return;

    setLoading(true);
    setSearchedQuery(text);

    try {
      const response = await websiteAiService.searchFaq({
        question: text,
        context,
        availableFaqs,
      });
      setAnswer(response.answer);
      setFallback(response.fallback);
    } catch {
      setFallback(true);
      setAnswer(
        "We're having trouble retrieving an answer right now. Please call us directly at (916) 555-9000 and our care team will be happy to assist you!"
      );
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleSearch();
    }
  };

  const handleClear = () => {
    setQuery("");
    setAnswer(null);
    setFallback(false);
    setSearchedQuery(null);
  };

  return (
    <div className="mb-8">
      {/* Search Input Box */}
      <div className="relative bg-white rounded-2xl shadow-sm border border-gray-200 focus-within:border-[#3B5BDB] focus-within:ring-2 focus-within:ring-blue-100 transition-all p-2 flex items-center gap-2">
        <div className="pl-3 text-gray-400">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
        </div>

        <input
          type="text"
          value={query}
          maxLength={1000}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder={placeholder}
          disabled={loading}
          className="flex-1 px-2 py-2 text-sm bg-transparent outline-none text-gray-800 placeholder-gray-400"
        />

        {query && (
          <button
            onClick={handleClear}
            className="text-gray-400 hover:text-gray-600 p-1 text-xs"
            title="Clear search"
          >
            ✕
          </button>
        )}

        <button
          onClick={() => handleSearch()}
          disabled={!query.trim() || loading}
          className="px-4 py-2 rounded-xl font-semibold text-xs text-white transition-all disabled:opacity-40 flex items-center gap-1.5 shadow-sm"
          style={{ background: accentColor }}
        >
          {loading ? (
            <>
              <div className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
              <span>Asking AI...</span>
            </>
          ) : (
            <>
              <span>Ask AI</span>
              <span>✨</span>
            </>
          )}
        </button>
      </div>

      {/* Suggested Quick Inquiries */}
      {!answer && (
        <div className="mt-2.5 flex flex-wrap items-center gap-2">
          <span className="text-[11px] text-gray-400 font-medium">Quick questions:</span>
          {["Visiting hours?", "Medicare coverage?", "How to schedule a tour?", "Dietary options?"].map((q) => (
            <button
              key={q}
              onClick={() => {
                setQuery(q);
                handleSearch(q);
              }}
              className="text-[11px] px-2.5 py-1 bg-gray-50 hover:bg-blue-50 text-gray-600 hover:text-[#3B5BDB] rounded-full border border-gray-100 transition-colors"
            >
              {q}
            </button>
          ))}
        </div>
      )}

      {/* AI Answer Card */}
      {answer && (
        <div className="mt-4 p-5 rounded-2xl bg-gradient-to-br from-blue-50/80 via-white to-indigo-50/50 border border-blue-200/70 shadow-md">
          <div className="flex items-center justify-between gap-2 mb-2 pb-2 border-b border-blue-100">
            <div className="flex items-center gap-2">
              <span className="flex h-2 w-2 relative">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-blue-600"></span>
              </span>
              <span className="text-xs font-bold uppercase tracking-wider text-blue-800">
                {fallback ? "General guidance" : "AI Answer"}
              </span>
              {searchedQuery && (
                <span className="text-xs text-gray-400 font-normal truncate max-w-xs">
                  · "{searchedQuery}"
                </span>
              )}
            </div>

            <button
              onClick={handleClear}
              className="text-xs text-gray-400 hover:text-gray-600 font-medium"
            >
              Clear
            </button>
          </div>

          <p className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
            {answer}
          </p>
          {fallback && (
            <p role="status" className="mt-3 text-xs text-amber-800">
              AI is temporarily unavailable. Please confirm details with our care team.
            </p>
          )}

          <div className="mt-3 pt-3 border-t border-blue-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2 text-xs text-gray-500">
            <span>Need more specifics? We are available 24/7.</span>
            <a
              href="tel:9165559000"
              className="font-semibold text-[#3B5BDB] hover:underline flex items-center gap-1"
            >
              <span>📞</span> Call (916) 555-9000
            </a>
          </div>
        </div>
      )}
    </div>
  );
};
