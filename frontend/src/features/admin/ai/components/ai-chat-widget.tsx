import React, { useState, useRef, useEffect } from "react";
import {
  Bot,
  X,
  Send,
  Sparkles,
  RefreshCw,
  ChevronDown,
  User,
  ShieldCheck,
} from "lucide-react";
import { aiService } from "../services/ai-service";

interface Message {
  id: string;
  sender: "user" | "ai";
  text: string;
  timestamp: Date;
}

export const AiChatWidget: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "welcome",
      sender: "ai",
      text: "Xin chào! Tôi là Trợ lý AI của Viện dưỡng lão ElderCare. Tôi có thể hỗ trợ bạn tra cứu quy trình, phân loại sự cố, gợi ý kế hoạch chăm sóc và tóm tắt thông tin ca trực.",
      timestamp: new Date(),
    },
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
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
    if (!textToSend) setInput("");
    setLoading(true);

    try {
      const response = await aiService.chat({ message: text });
      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: response.reply,
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, aiMessage]);
    } catch {
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: "Hệ thống AI đang khởi động hoặc chưa sẵn sàng kết nối. Vui lòng kiểm tra lại dịch vụ Ollama local hoặc thử lại sau.",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const samplePrompts = [
    "Quy trình xử lý khi phát hiện cư dân bị ngã?",
    "Tiêu chuẩn phân loại sự cố mức High và Emergency?",
    "Gợi ý các mục tiêu chăm sóc cho cụ suy giảm trí nhớ?",
  ];

  return (
    <div className="fixed bottom-6 right-6 z-50 flex flex-col items-end">
      {/* Chat Window */}
      {isOpen && (
        <div className="mb-4 w-96 max-w-[calc(100vw-2rem)] h-[520px] bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl shadow-2xl flex flex-col overflow-hidden animate-in fade-in slide-in-from-bottom-5 duration-200">
          {/* Header */}
          <div className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white px-4 py-3 flex items-center justify-between shadow-sm">
            <div className="flex items-center space-x-2.5">
              <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center backdrop-blur-sm">
                <Bot className="w-5 h-5 text-white" />
              </div>
              <div>
                <div className="flex items-center space-x-1.5">
                  <h3 className="text-sm font-semibold tracking-wide">
                    ElderCare AI Assistant
                  </h3>
                  <span className="flex h-2 w-2 relative">
                    <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                    <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-400"></span>
                  </span>
                </div>
                <p className="text-[11px] text-blue-100 flex items-center space-x-1">
                  <ShieldCheck className="w-3 h-3 inline" />
                  <span>Qwen 3.5 Local • HIPAA Guard</span>
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-1">
              <button
                onClick={() =>
                  setMessages([
                    {
                      id: "welcome",
                      sender: "ai",
                      text: "Xin chào! Tôi có thể hỗ trợ gì cho bạn trong ca trực hôm nay?",
                      timestamp: new Date(),
                    },
                  ])
                }
                title="Làm mới hội thoại"
                className="p-1.5 rounded-lg hover:bg-white/10 text-blue-100 hover:text-white transition-colors"
              >
                <RefreshCw className="w-4 h-4" />
              </button>
              <button
                onClick={() => setIsOpen(false)}
                title="Đóng chat"
                className="p-1.5 rounded-lg hover:bg-white/10 text-blue-100 hover:text-white transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Messages Body */}
          <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-zinc-50/50 dark:bg-zinc-950/40 text-xs">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex items-start gap-2.5 ${
                  msg.sender === "user" ? "flex-row-reverse" : "flex-row"
                }`}
              >
                <div
                  className={`w-7 h-7 rounded-full flex items-center justify-center shrink-0 ${
                    msg.sender === "user"
                      ? "bg-blue-600 text-white"
                      : "bg-indigo-100 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 border border-indigo-200 dark:border-indigo-800"
                  }`}
                >
                  {msg.sender === "user" ? (
                    <User className="w-4 h-4" />
                  ) : (
                    <Bot className="w-4 h-4" />
                  )}
                </div>
                <div
                  className={`max-w-[78%] px-3.5 py-2.5 rounded-2xl leading-relaxed whitespace-pre-wrap ${
                    msg.sender === "user"
                      ? "bg-blue-600 text-white rounded-tr-xs"
                      : "bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 text-zinc-800 dark:text-zinc-200 rounded-tl-xs shadow-xs"
                  }`}
                >
                  {msg.text}
                </div>
              </div>
            ))}

            {loading && (
              <div className="flex items-center space-x-2 text-zinc-500 py-1 pl-9">
                <Sparkles className="w-4 h-4 text-indigo-500 animate-spin" />
                <span className="text-[11px] animate-pulse">
                  AI đang phân tích và soạn câu trả lời...
                </span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Prompt Suggestions */}
          {messages.length <= 2 && (
            <div className="px-3 py-2 bg-zinc-100/60 dark:bg-zinc-900/60 border-t border-zinc-200 dark:border-zinc-800 flex flex-wrap gap-1.5">
              {samplePrompts.map((prompt, idx) => (
                <button
                  key={idx}
                  onClick={() => handleSend(prompt)}
                  className="text-[11px] bg-white dark:bg-zinc-800 hover:bg-indigo-50 dark:hover:bg-zinc-700/60 border border-zinc-200 dark:border-zinc-700 text-zinc-600 dark:text-zinc-300 rounded-full px-2.5 py-1 text-left transition-colors"
                >
                  💡 {prompt}
                </button>
              ))}
            </div>
          )}

          {/* Input Footer */}
          <div className="p-3 bg-white dark:bg-zinc-900 border-t border-zinc-200 dark:border-zinc-800 flex items-center gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSend()}
              placeholder="Nhập câu hỏi hoặc yêu cầu AI hỗ trợ..."
              className="flex-1 text-xs px-3.5 py-2 rounded-xl bg-zinc-100 dark:bg-zinc-800 text-zinc-900 dark:text-zinc-100 border border-transparent focus:border-indigo-500 focus:bg-white dark:focus:bg-zinc-900 focus:outline-none transition-colors"
            />
            <button
              onClick={() => handleSend()}
              disabled={!input.trim() || loading}
              className="p-2 rounded-xl bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 disabled:cursor-not-allowed text-white transition-colors shrink-0"
            >
              <Send className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* Floating Toggle Button */}
      <button
        onClick={() => setIsOpen((prev) => !prev)}
        className="group relative flex items-center justify-center w-14 h-14 rounded-full bg-gradient-to-r from-blue-600 via-indigo-600 to-indigo-700 text-white shadow-xl hover:shadow-indigo-500/25 hover:scale-105 active:scale-95 transition-all duration-200 focus:outline-none"
        aria-label="Mở Trợ lý AI"
      >
        <span className="absolute -top-1 -right-1 flex h-4 w-4">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-4 w-4 bg-emerald-500 text-[9px] font-bold items-center justify-center text-white">
            AI
          </span>
        </span>
        {isOpen ? (
          <ChevronDown className="w-6 h-6 transition-transform group-hover:translate-y-0.5" />
        ) : (
          <Bot className="w-6 h-6 transition-transform group-hover:scale-110" />
        )}
      </button>
    </div>
  );
};
