import React, { useState, useRef, useEffect } from "react";
import { useLocation } from "react-router";
import { websiteAiService, type WebsiteChatResponse } from "../../services/website-ai-service";

interface Message {
  id: string;
  sender: "user" | "ai";
  text: string;
  timestamp: Date;
  fallback?: boolean;
}

const QUICK_PROMPTS = [
  { label: "🏥 Chi phí & BHYT / Insurance", prompt: "Chi phí điều dưỡng và bảo hiểm y tế (Medicare/Medicaid) chi trả như thế nào?" },
  { label: "🧠 Chăm sóc sa sút trí tuệ / Memory Care", prompt: "Viện có tiếp nhận bệnh nhân Alzheimer, sa sút trí tuệ có hành vi đi lạc không?" },
  { label: "🕒 Giờ thăm bệnh / Visiting Hours", prompt: "Giờ thăm người thân và quy định thăm viếng tại trung tâm như thế nào?" },
  { label: "📋 Quy trình nhập viện / Admissions", prompt: "Quy trình làm hồ sơ nhập viện cho người cao tuổi gồm những bước gì?" },
  { label: "📅 Đặt lịch tham quan / Book a Tour", prompt: "Tôi muốn đặt lịch đến tham quan trực tiếp cơ sở vật chất thì làm thế nào?" },
];

export const WebsiteChatWidget: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "welcome",
      sender: "ai",
      text: "Hello! 👋 I'm the ElderCare virtual assistant. I'm here to help you learn about our services, admission process, visiting hours, and more.\n\nHow can I assist you today?",
      timestamp: new Date(),
    },
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [showQuickPrompts, setShowQuickPrompts] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const location = useLocation();

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
      setTimeout(() => inputRef.current?.focus(), 200);
    }
  }, [messages, isOpen]);

  const handleSend = async (textToSend?: string) => {
    const text = (textToSend || input).trim();
    if (!text || loading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      sender: "user",
      text,
      timestamp: new Date(),
    };
    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setLoading(true);
    setShowQuickPrompts(false);

    try {
      const response: WebsiteChatResponse = await websiteAiService.chat({
        message: text,
        currentPage: location.pathname,
      });
      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: response.reply,
        timestamp: new Date(),
        fallback: response.fallback,
      };
      setMessages((prev) => [...prev, aiMessage]);
    } catch {
      setMessages((prev) => [
        ...prev,
        {
          id: (Date.now() + 1).toString(),
          sender: "ai",
          text: "The assistant is temporarily unavailable. Please call (916) 555-9000 or email care@eldercare.com for current information.",
          timestamp: new Date(),
          fallback: true,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <>
      {/* Floating Toggle Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="fixed bottom-6 right-6 z-50 w-14 h-14 rounded-full shadow-xl flex items-center justify-center transition-all duration-300 hover:scale-110"
        style={{
          background: "linear-gradient(135deg, #1e3a8a, #3b82f6)",
          color: "white",
        }}
        aria-label="Chat with ElderCare AI"
      >
        {isOpen ? (
          <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        ) : (
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
        )}
      </button>

      {/* Notification dot */}
      {!isOpen && messages.length <= 1 && (
        <span className="fixed bottom-[68px] right-6 z-50 flex h-3.5 w-3.5 pointer-events-none">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-3.5 w-3.5 bg-blue-500"></span>
        </span>
      )}

      {/* Chat Panel */}
      {isOpen && (
        <div
          className="fixed bottom-24 right-6 z-50 flex flex-col overflow-hidden shadow-2xl"
          style={{
            width: "380px",
            maxWidth: "calc(100vw - 48px)",
            height: "520px",
            maxHeight: "calc(100vh - 140px)",
            borderRadius: "20px",
            border: "1px solid #e2e8f0",
            background: "#ffffff",
          }}
        >
          {/* Header */}
          <div
            className="flex items-center gap-3 px-5 py-4 shrink-0"
            style={{
              background: "linear-gradient(135deg, #0f1a3e, #1e3a8a)",
              color: "white",
            }}
          >
            <div className="w-9 h-9 rounded-full bg-white/20 flex items-center justify-center shrink-0">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            </div>
            <div className="flex-1 min-w-0">
              <h3 className="font-semibold text-sm">ElderCare Assistant</h3>
              <p className="text-xs text-blue-200">Ask about services, admissions & more</p>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="w-7 h-7 rounded-full hover:bg-white/20 flex items-center justify-center transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="6 9 12 15 18 9"></polyline></svg>
            </button>
          </div>

          {/* Messages */}
          <div
            className="flex-1 overflow-y-auto px-4 py-3 space-y-3"
            style={{ background: "#f8fafc" }}
          >
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex ${msg.sender === "user" ? "justify-end" : "justify-start"}`}
              >
                <div
                  className={`max-w-[85%] px-3.5 py-2.5 text-sm leading-relaxed whitespace-pre-wrap ${
                    msg.sender === "user"
                      ? "bg-blue-600 text-white rounded-2xl rounded-br-md"
                      : "bg-white text-gray-800 border border-gray-200 rounded-2xl rounded-bl-md shadow-sm"
                  }`}
                >
                  {msg.text}
                  {msg.fallback && (
                    <p className="mt-2 border-t border-amber-200 pt-2 text-xs text-amber-800" role="status">
                      General guidance · AI unavailable
                    </p>
                  )}
                </div>
              </div>
            ))}

            {loading && (
              <div className="flex justify-start">
                <div className="bg-white border border-gray-200 rounded-2xl rounded-bl-md shadow-sm px-4 py-3">
                  <div className="flex items-center gap-1.5">
                    <span className="w-2 h-2 bg-blue-400 rounded-full animate-bounce" style={{ animationDelay: "0ms" }}></span>
                    <span className="w-2 h-2 bg-blue-400 rounded-full animate-bounce" style={{ animationDelay: "150ms" }}></span>
                    <span className="w-2 h-2 bg-blue-400 rounded-full animate-bounce" style={{ animationDelay: "300ms" }}></span>
                  </div>
                </div>
              </div>
            )}

            {/* Quick Prompts */}
            {showQuickPrompts && !loading && (
              <div className="flex flex-wrap gap-1.5 pt-1">
                {QUICK_PROMPTS.map((item, idx) => (
                  <button
                    key={idx}
                    onClick={() => handleSend(item.prompt)}
                    className="text-xs px-3 py-1.5 rounded-full border border-blue-200 bg-blue-50 text-blue-700 hover:bg-blue-100 transition-colors cursor-pointer text-left"
                  >
                    {item.label}
                  </button>
                ))}
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          {/* Input */}
          <div className="px-3 py-3 bg-white border-t border-gray-100 shrink-0">
            <div className="flex items-center gap-2">
              <input
                ref={inputRef}
                type="text"
                value={input}
                maxLength={2000}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="Type your question..."
                disabled={loading}
                className="flex-1 px-4 py-2.5 text-sm bg-gray-50 border border-gray-200 rounded-full outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 disabled:opacity-50 transition-all"
              />
              <button
                onClick={() => handleSend()}
                disabled={!input.trim() || loading}
                className="w-10 h-10 rounded-full flex items-center justify-center shrink-0 transition-all disabled:opacity-40"
                style={{
                  background: input.trim() && !loading ? "linear-gradient(135deg, #1e3a8a, #3b82f6)" : "#e2e8f0",
                  color: input.trim() && !loading ? "white" : "#94a3b8",
                }}
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
              </button>
            </div>
            <p className="text-[10px] text-gray-400 text-center mt-1.5">
              AI-powered · For informational purposes only
            </p>
          </div>
        </div>
      )}
    </>
  );
};
