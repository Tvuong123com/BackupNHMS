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
  Cloud,
  Server,
} from "lucide-react";
import { aiService } from "../services/ai-service";

interface Message {
  id: string;
  sender: "user" | "ai";
  text: string;
  timestamp: Date;
  sources?: string[];
}

export const AiChatWidget: React.FC = () => {
  const [conversationId, setConversationId] = useState(() => sessionStorage.getItem("eldercare-admin-ai-conversation") || "");
  const [isOpen, setIsOpen] = useState(false);
  const [aiEngine, setAiEngine] = useState<{ provider: string; model: string }>({
    provider: "LOCAL_OLLAMA",
    model: "eldercare-ai",
  });
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "welcome",
      sender: "ai",
      text: "Hello. I can help find recorded context and draft staff documentation. Confirm clinical decisions against the chart and approved procedures.",
      timestamp: new Date(),
    },
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const fetchActiveEngine = async () => {
    try {
      const s = await aiService.checkHealth();
      if (s) {
        setAiEngine({
          provider: s.provider || "LOCAL_OLLAMA",
          model: s.model || "eldercare-ai",
        });
      }
    } catch {
      // fallback
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    fetchActiveEngine();
    if (conversationId) {
      aiService.getChatHistory(conversationId).then((turns) => {
        if (turns.length > 0) setMessages(turns.map((turn, index) => ({
          id: `history-${index}`,
          sender: turn.role === "user" ? "user" : "ai",
          text: turn.content,
          timestamp: new Date(),
        })));
      }).catch(() => {});
    }
  }, []);

  useEffect(() => {
    if (isOpen) {
      fetchActiveEngine();
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
      const response = await aiService.chat({ message: text, conversationId: conversationId || undefined });
      if (response.conversationId) {
        setConversationId(response.conversationId);
        sessionStorage.setItem("eldercare-admin-ai-conversation", response.conversationId);
      }
      if (response.model) {
        setAiEngine({
          model: response.model,
          provider: response.model.toLowerCase().includes("gemini") ? "GOOGLE_GEMINI" : "LOCAL_OLLAMA",
        });
      }
      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: response.reply,
        timestamp: new Date(),
        sources: response.sources,
      };
      setMessages((prev) => [...prev, aiMessage]);
    } catch {
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: "The clinical AI assistant is currently warming up or unavailable. Please verify AI settings or ensure connection to the selected model.",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const samplePrompts = [
    { label: "🚨 Unwitnessed Fall Protocol", prompt: "What is the immediate nursing protocol upon discovering an unwitnessed resident fall?" },
    { label: "💊 Warfarin & NSAID Interaction", prompt: "What are the clinical bleeding risks and monitoring actions if a resident on Warfarin is prescribed Ibuprofen?" },
    { label: "🧠 Sundowning Non-Pharm Care", prompt: "Propose non-pharmacological interventions for a dementia resident exhibiting sundowning agitation at 17:00?" },
    { label: "📋 SBAR Shift Handover Draft", prompt: "Draft a clinical SBAR shift handover summary for a resident with acute fever and hypotension during shift?" },
    { label: "🛡️ Beers Criteria & FRIDs", prompt: "Which sedative/hypnotic medications require Gradual Dose Reduction (GDR) under CMS F758 and Beers Criteria?" },
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
                    ElderCare Staff AI Assistant
                  </h3>
                  <span className="flex h-2 w-2 relative">
                    <span className="relative inline-flex rounded-full h-2 w-2 bg-blue-200"></span>
                  </span>
                </div>
                <p className="text-[11px] text-blue-100 flex items-center space-x-1">
                  {aiEngine.provider === "GOOGLE_GEMINI" ? (
                    <>
                      <Sparkles className="w-3 h-3 inline text-cyan-300" />
                      <span className="font-mono">{aiEngine.model} • Google AI Studio</span>
                    </>
                  ) : (
                    <>
                      <Server className="w-3 h-3 inline text-amber-300" />
                      <span className="font-mono">{aiEngine.model} • Local Ollama</span>
                    </>
                  )}
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-1">
              <button
                onClick={() => {
                  fetchActiveEngine();
                  setConversationId("");
                  sessionStorage.removeItem("eldercare-admin-ai-conversation");
                  setMessages([
                    {
                      id: "welcome",
                      sender: "ai",
                      text: "Hello. Ask about recorded context or staff documentation. Verify clinical decisions in the chart.",
                      timestamp: new Date(),
                    },
                  ]);
                }}
                title="Refresh active model status & reset conversation"
                className="p-1.5 rounded-lg hover:bg-white/10 text-blue-100 hover:text-white transition-colors cursor-pointer"
              >
                <RefreshCw className="w-4 h-4" />
              </button>
              <button
                onClick={() => setIsOpen(false)}
                title="Close chat"
                className="p-1.5 rounded-lg hover:bg-white/10 text-blue-100 hover:text-white transition-colors cursor-pointer"
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
                  {msg.sources && msg.sources.length > 0 && (
                    <div className="mt-2 pt-2 border-t border-zinc-200 dark:border-zinc-700 text-[10px] text-zinc-500">
                      Retrieved context: {msg.sources.join(", ")}
                    </div>
                  )}
                </div>
              </div>
            ))}

            {loading && (
              <div className="flex items-center space-x-2 text-zinc-500 py-1 pl-9">
                <Sparkles className="w-4 h-4 text-indigo-500 animate-spin" />
                <span className="text-[11px] animate-pulse">
                  Analyzing and drafting clinical response...
                </span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Prompt Suggestions */}
          {messages.length <= 3 && (
            <div className="px-3 py-2 bg-zinc-100/70 dark:bg-zinc-900/70 border-t border-zinc-200 dark:border-zinc-800 flex flex-wrap gap-1.5 max-h-28 overflow-y-auto">
              {samplePrompts.map((item, idx) => (
                <button
                  key={idx}
                  onClick={() => handleSend(item.prompt)}
                  title={item.prompt}
                  className="text-[11px] bg-white dark:bg-zinc-800 hover:bg-indigo-50 dark:hover:bg-zinc-700/60 border border-zinc-200 dark:border-zinc-700 text-zinc-700 dark:text-zinc-200 hover:text-indigo-600 rounded-full px-2.5 py-1 text-left transition-all shadow-2xs hover:shadow-xs active:scale-95 cursor-pointer"
                >
                  {item.label}
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
              placeholder="Type your question or request clinical guidance..."
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
        aria-label="Open Staff AI Assistant"
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
