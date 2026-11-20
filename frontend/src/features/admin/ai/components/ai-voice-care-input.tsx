import React, { useState, useEffect, useRef } from "react";
import { Mic, MicOff, Sparkles, Loader2, Check } from "lucide-react";
import { aiService } from "../services/ai-service";
import type { VoiceCareParseResponse } from "../types/ai.types";

interface AiVoiceCareInputProps {
  residentId: number;
  tasks: { id: number; taskType: string; status: string }[];
  onConfirmed?: () => void;
  expectedResidentName?: string;
  expectedRoomNumber?: string;
  onParsedResult?: (result: VoiceCareParseResponse) => void;
  className?: string;
}

export const AiVoiceCareInput: React.FC<AiVoiceCareInputProps> = ({
  residentId,
  tasks,
  onConfirmed,
  expectedResidentName,
  expectedRoomNumber,
  onParsedResult,
  className = "",
}) => {
  const [isListening, setIsListening] = useState(false);
  const [transcript, setTranscript] = useState("");
  const [isProcessing, setIsProcessing] = useState(false);
  const [lastParsed, setLastParsed] = useState<VoiceCareParseResponse | null>(null);
  const [selectedTaskId, setSelectedTaskId] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [savedMessage, setSavedMessage] = useState("");
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
      setError("");
      setSavedMessage("");
      if (!recognitionRef.current) {
        const manualInput = prompt("Enter the observed care note for this resident:", "");
        if (manualInput) { setTranscript(manualInput); void handleProcessVoice(manualInput); }
        return;
      }
      try {
        recognitionRef.current.start();
        setIsListening(true);
      } catch {
        // Fallback prompt nếu SpeechRecognition không khả dụng
        const manualInput = prompt(
          "Enter dictated care observation (or type quickly):",
          ""
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
    } catch {
      setError("Could not extract this note. You can try again or enter it manually.");
    } finally {
      setIsProcessing(false);
    }
  };

  const confirm = async () => {
    if (!lastParsed || !transcript.trim()) return;
    setSaving(true);
    setError("");
    try {
      const saved = await aiService.confirmVoiceObservation({
        residentId,
        taskId: selectedTaskId ? Number(selectedTaskId) : undefined,
        transcript,
        parsed: lastParsed,
      });
      setSavedMessage(saved.vitalSignId
        ? `Observation #${saved.observationId} and vital chart entry #${saved.vitalSignId} saved.`
        : `Observation #${saved.observationId} saved.`);
      onParsedResult?.(lastParsed);
      onConfirmed?.();
      setLastParsed(null);
      setSelectedTaskId("");
      setTranscript("");
    } catch {
      setError("Could not save the observation. Check the selected task and try again.");
    } finally {
      setSaving(false);
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
          title="Dictate clinical observations; NLP extracts vitals and care records"
        >
          {isListening ? (
            <>
              <MicOff className="w-3.5 h-3.5 text-white" />
              <span>Listening... (Click to finish)</span>
            </>
          ) : isProcessing ? (
            <>
              <Loader2 className="w-3.5 h-3.5 animate-spin" />
              <span>Extracting clinical entities...</span>
            </>
          ) : (
            <>
              <Mic className="w-3.5 h-3.5 text-indigo-600 dark:text-indigo-400" />
              <span>Dictate Care Note</span>
            </>
          )}
        </button>

        {transcript && (
          <span className="text-xs text-zinc-500 italic truncate max-w-xs">
            "{transcript}"
          </span>
        )}
        {transcript && !isListening && !isProcessing && !lastParsed && (
          <button type="button" className="text-xs text-indigo-700 underline" onClick={() => void handleProcessVoice(transcript)}>Analyze note</button>
        )}
      </div>

      {/* Extracted entities summary */}
      {lastParsed && (
        <div className="p-2.5 rounded-xl bg-emerald-50/60 dark:bg-emerald-950/20 border border-emerald-200 dark:border-emerald-800/60 text-xs text-zinc-700 dark:text-zinc-300 space-y-2 animate-in fade-in duration-150">
          <div className="flex items-center gap-2 flex-wrap">
            <span className="inline-flex items-center gap-1 text-emerald-700 dark:text-emerald-400 font-semibold">
              <Check className="w-3.5 h-3.5" /> Extracted draft — verify before saving:
            </span>
            {lastParsed.dietaryIntakePercentage !== undefined && lastParsed.dietaryIntakePercentage !== null && (
              <span className="px-2 py-0.5 rounded bg-white dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 font-medium">
                Intake: {lastParsed.dietaryIntakePercentage}%
              </span>
            )}
            {lastParsed.bloodPressureSystolic && lastParsed.bloodPressureDiastolic && (
              <span className="px-2 py-0.5 rounded bg-white dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 font-medium">
                BP: {lastParsed.bloodPressureSystolic}/{lastParsed.bloodPressureDiastolic} mmHg
              </span>
            )}
            {lastParsed.medicationTaken && (
              <span className="px-2 py-0.5 rounded bg-emerald-100 dark:bg-emerald-900/50 text-emerald-800 dark:text-emerald-200 font-medium">
                Medication mentioned — verify in eMAR
              </span>
            )}
          </div>
          <span className="text-[10px] text-zinc-400 shrink-0 flex items-center gap-1">
            <Sparkles className="w-3 h-3 text-indigo-400" />
            Confidence: {(lastParsed.confidence * 100).toFixed(0)}%
          </span>
          <div className="flex flex-wrap gap-2 items-center">
            <select className="border rounded p-1 text-xs" aria-label="Task to complete after review" value={selectedTaskId} onChange={e => setSelectedTaskId(e.target.value)}>
              <option value="">Save observation only</option>
              {tasks.filter(task => task.status === "PENDING" && [
                "BATHING", "DRESSING", "GROOMING", "TOILETING", "FEEDING",
                "MEAL_ASSISTANCE", "HYDRATION", "MOBILITY", "TRANSFER",
                "REPOSITIONING", "ACTIVITY",
              ].includes(task.taskType.trim().toUpperCase().replace(/[^A-Z0-9]+/g, "_")))
                .map(task => <option key={task.id} value={task.id}>Complete #{task.id}: {task.taskType}</option>)}
            </select>
            <button type="button" className="rounded bg-emerald-700 text-white px-2 py-1 disabled:opacity-50" disabled={saving} onClick={() => void confirm()}>
              {saving ? "Saving…" : "Confirm & save"}
            </button>
          </div>
        </div>
      )}
      {error && <p className="text-xs text-red-600">{error}</p>}
      {savedMessage && <p className="text-xs text-emerald-700">{savedMessage}</p>}
    </div>
  );
};
