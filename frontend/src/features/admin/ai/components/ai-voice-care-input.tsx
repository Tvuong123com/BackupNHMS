import React, { useState, useEffect, useRef } from "react";
import { Mic, MicOff, Sparkles, Loader2, Check } from "lucide-react";
import { aiService } from "../services/ai-service";
import type { VoiceCareParseResponse } from "../types/ai.types";

interface AiVoiceCareInputProps {
  expectedResidentName?: string;
  expectedRoomNumber?: string;
  onParsedResult?: (result: VoiceCareParseResponse) => void;
  className?: string;
}

export const AiVoiceCareInput: React.FC<AiVoiceCareInputProps> = ({
  expectedResidentName,
  expectedRoomNumber,
  onParsedResult,
  className = "",
}) => {
  const [isListening, setIsListening] = useState(false);
  const [transcript, setTranscript] = useState("");
  const [isProcessing, setIsProcessing] = useState(false);
  const [lastParsed, setLastParsed] = useState<VoiceCareParseResponse | null>(null);
  const recognitionRef = useRef<any>(null);

  useEffect(() => {
    // Khởi tạo SpeechRecognition nếu trình duyệt hỗ trợ
    const SpeechRecognition =
      (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

    if (SpeechRecognition) {
      const recognition = new SpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = true;
      recognition.lang = "vi-VN";

      recognition.onresult = (event: any) => {
        let currentTranscript = "";
        for (let i = event.resultIndex; i < event.results.length; i++) {
          currentTranscript += event.results[i][0].transcript;
        }
        setTranscript(currentTranscript);
      };

      recognition.onerror = () => {
        setIsListening(false);
      };

      recognition.onend = () => {
        setIsListening(false);
      };

      recognitionRef.current = recognition;
    }
  }, []);

  const handleToggleListen = () => {
    if (isListening) {
      recognitionRef.current?.stop();
      setIsListening(false);
      if (transcript.trim()) {
        handleProcessVoice(transcript);
      }
    } else {
      setTranscript("");
      setLastParsed(null);
      try {
        recognitionRef.current?.start();
        setIsListening(true);
      } catch {
        // Fallback prompt nếu SpeechRecognition không khả dụng
        const manualInput = prompt(
          "Nhập nội dung chăm sóc bằng giọng nói (hoặc gõ nhanh):",
          "Cụ phòng 204 ăn hết 80% phần cơm, huyết áp 120/80, đã uống thuốc"
        );
        if (manualInput) {
          setTranscript(manualInput);
          handleProcessVoice(manualInput);
        }
      }
    }
  };

  const handleProcessVoice = async (text: string) => {
    if (!text.trim()) return;
    setIsProcessing(true);
    try {
      const result = await aiService.parseVoiceNote({
        spokenText: text,
        expectedResidentName,
        expectedRoomNumber,
      });
      setLastParsed(result);
      if (onParsedResult) {
        onParsedResult(result);
      }
    } catch {
      // Ignored
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className={`flex flex-col gap-2 ${className}`}>
      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={handleToggleListen}
          disabled={isProcessing}
          className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-all shadow-xs ${
            isListening
              ? "bg-red-600 hover:bg-red-700 text-white animate-pulse"
              : "bg-indigo-50 hover:bg-indigo-100 dark:bg-indigo-950/60 dark:hover:bg-indigo-900/60 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800"
          }`}
          title="Bấm để nói nội dung chăm sóc, AI sẽ tự động điền form"
        >
          {isListening ? (
            <>
              <MicOff className="w-3.5 h-3.5 text-white" />
              <span>Đang lắng nghe... (Bấm để hoàn tất)</span>
            </>
          ) : isProcessing ? (
            <>
              <Loader2 className="w-3.5 h-3.5 animate-spin" />
              <span>AI đang bóc tách...</span>
            </>
          ) : (
            <>
              <Mic className="w-3.5 h-3.5 text-indigo-600 dark:text-indigo-400" />
              <span>🎙️ Voice Care Dictate</span>
            </>
          )}
        </button>

        {transcript && (
          <span className="text-xs text-zinc-500 italic truncate max-w-xs">
            "{transcript}"
          </span>
        )}
      </div>

      {/* Hiển thị tóm tắt dữ liệu vừa trích xuất được */}
      {lastParsed && (
        <div className="p-2.5 rounded-xl bg-emerald-50/60 dark:bg-emerald-950/20 border border-emerald-200 dark:border-emerald-800/60 text-xs text-zinc-700 dark:text-zinc-300 flex items-center justify-between gap-3 animate-in fade-in duration-150">
          <div className="flex items-center gap-2 flex-wrap">
            <span className="inline-flex items-center gap-1 text-emerald-700 dark:text-emerald-400 font-semibold">
              <Check className="w-3.5 h-3.5" /> Đã bóc tách tự động:
            </span>
            {lastParsed.dietaryIntakePercentage !== undefined && lastParsed.dietaryIntakePercentage !== null && (
              <span className="px-2 py-0.5 rounded bg-white dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 font-medium">
                Khẩu phần: {lastParsed.dietaryIntakePercentage}%
              </span>
            )}
            {lastParsed.bloodPressureSystolic && lastParsed.bloodPressureDiastolic && (
              <span className="px-2 py-0.5 rounded bg-white dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 font-medium">
                HA: {lastParsed.bloodPressureSystolic}/{lastParsed.bloodPressureDiastolic} mmHg
              </span>
            )}
            {lastParsed.medicationTaken && (
              <span className="px-2 py-0.5 rounded bg-emerald-100 dark:bg-emerald-900/50 text-emerald-800 dark:text-emerald-200 font-medium">
                ✓ Đã uống thuốc
              </span>
            )}
          </div>
          <span className="text-[10px] text-zinc-400 shrink-0 flex items-center gap-1">
            <Sparkles className="w-3 h-3 text-indigo-400" />
            Độ tin cậy {(lastParsed.confidence * 100).toFixed(0)}%
          </span>
        </div>
      )}
    </div>
  );
};
