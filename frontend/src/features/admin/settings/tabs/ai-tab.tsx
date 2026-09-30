import { useState, useEffect } from "react";
import { toast } from "sonner";
import { 
  Bot, 
  Cpu, 
  Cloud, 
  Server, 
  Zap, 
  Sliders, 
  ShieldCheck, 
  CheckCircle2, 
  AlertCircle, 
  Eye, 
  EyeOff, 
  ExternalLink, 
  RotateCcw,
  Save,
  Check,
  Activity,
  FileText,
  Mic,
  Pill,
  Radio
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { Badge } from "@/components/ui/badge";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { aiService } from "../../ai/services/ai-service";
import type { AiSettings, AiTestResult } from "../../ai/types/ai.types";

const DEFAULT_SYSTEM_RULES = `[ELDERCARE NHMS CLINICAL & OPERATIONAL RULES]
1. STRICT SCOPE: You are the internal Clinical Decision Assistant for ElderCare NHMS (Nursing Home Management System). You must exclusively assist with resident care, clinical evaluations (ADL/Fall/Cognitive), incident classification, care plan goals, medication safety, and nursing operations within this system. Strictly decline any queries outside elder care or nursing home workflows.
2. SPEED & CONCISENESS: Return direct, structured, actionable bullet points without introductory pleasantries, conversational filler, or unnecessary text to maximize system response speed.
3. CLINICAL ACCURACY & HIPAA: Follow evidence-based geriatric care protocols and HIPAA resident confidentiality. Never alter medication regimens without explicit physician order.`;

export const AiTab = () => {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);
  const [showApiKey, setShowApiKey] = useState(false);
  const [testResult, setTestResult] = useState<AiTestResult | null>(null);

  const [settings, setSettings] = useState<AiSettings>({
    provider: "GOOGLE_GEMINI",
    geminiApiKey: "",
    geminiModel: "gemini-3.8-flash",
    ollamaBaseUrl: "http://localhost:11434",
    ollamaModel: "qwen3.5:2b-q4_K_M",
    temperature: 0.2,
    maxTokens: 512,
    systemRules: DEFAULT_SYSTEM_RULES,
    features: {
      chatbot: true,
      incidentAnalysis: true,
      carePlan: true,
      voiceCare: true,
      medicationSafety: true,
      elopementRisk: true,
    },
  });

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      setLoading(true);
      const data = await aiService.getSettings();
      const localKey = localStorage.getItem("eldercare_gemini_api_key") || "";
      const effectiveKey = (data.geminiApiKey && !data.geminiApiKey.includes("...") && data.geminiApiKey !== "******")
        ? data.geminiApiKey
        : (localKey || data.geminiApiKey || "");

      setSettings((prev) => ({
        ...prev,
        ...data,
        geminiApiKey: effectiveKey,
        geminiModel: data.geminiModel && data.geminiModel !== "gemini-2.0-flash" ? data.geminiModel : "gemini-3.8-flash",
        features: {
          ...prev.features,
          ...(data.features || {}),
        },
      }));
    } catch (err: any) {
      console.warn("Failed to load AI settings from backend, using defaults:", err);
      const localKey = localStorage.getItem("eldercare_gemini_api_key") || "";
      if (localKey) {
        setSettings(prev => ({ ...prev, geminiApiKey: localKey }));
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      const keyToSave = settings.geminiApiKey || localStorage.getItem("eldercare_gemini_api_key") || "";
      if (keyToSave && !keyToSave.includes("...")) {
        localStorage.setItem("eldercare_gemini_api_key", keyToSave);
      }
      const updated = await aiService.updateSettings({
        ...settings,
        geminiApiKey: keyToSave,
      });
      setSettings((prev) => ({
        ...prev,
        ...updated,
        geminiApiKey: keyToSave, // Keep real key in local state so user doesn't see dots
      }));
      toast.success("AI Configuration saved successfully!", {
        description: `Active engine: ${settings.provider === "GOOGLE_GEMINI" ? "Google Gemini API (Cloud)" : "Local Ollama (Offline)"}`,
        icon: <CheckCircle2 className="h-5 w-5 text-emerald-600" />,
      });
    } catch (err: any) {
      toast.error("Failed to save AI configuration", {
        description: err?.response?.data?.message || err.message || "Unknown error",
        icon: <AlertCircle className="h-5 w-5 text-red-600" />,
      });
    } finally {
      setSaving(false);
    }
  };

  const handleTestConnection = async () => {
    try {
      setTesting(true);
      setTestResult(null);
      const res = await aiService.testConnection(settings);
      setTestResult(res);
      if (res.success) {
        toast.success(`Connected to ${res.provider}!`, {
          description: `Response received in ${res.latencyMs}ms using model ${res.model}`,
          icon: <CheckCircle2 className="h-5 w-5 text-emerald-600" />,
        });
      } else {
        toast.error(`Connection failed: ${res.message}`, {
          icon: <AlertCircle className="h-5 w-5 text-red-600" />,
        });
      }
    } catch (err: any) {
      const msg = err?.response?.data?.message || err.message || "Connection refused";
      setTestResult({
        success: false,
        latencyMs: 0,
        provider: settings.provider,
        model: settings.provider === "GOOGLE_GEMINI" ? (settings.geminiModel || "") : (settings.ollamaModel || ""),
        message: msg,
      });
      toast.error(`Failed to reach ${settings.provider}: ${msg}`, {
        icon: <AlertCircle className="h-5 w-5 text-red-600" />,
      });
    } finally {
      setTesting(false);
    }
  };

  const handleFeatureToggle = (key: string, value: boolean) => {
    setSettings((prev) => ({
      ...prev,
      features: {
        ...prev.features,
        [key]: value,
      },
    }));
  };

  const handleResetRules = () => {
    setSettings((prev) => ({
      ...prev,
      systemRules: DEFAULT_SYSTEM_RULES,
    }));
    toast.info("Clinical system rules reset to default ElderCare boundaries.");
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center p-12 text-slate-500">
        <Activity className="h-6 w-6 animate-spin mr-2 text-blue-600" />
        <span>Loading AI Engine configuration...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Overview Banner */}
      <div className="bg-gradient-to-r from-blue-900/90 via-indigo-900/90 to-slate-900 p-6 rounded-xl text-white shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold mb-2">
            <Cpu className="h-3.5 w-3.5" /> High-Performance AI Pipeline
          </div>
          <h2 className="text-2xl font-bold tracking-tight">AI Engine & Model Architecture</h2>
          <p className="text-slate-300 text-xs md:text-sm mt-1 max-w-2xl">
            Switch between Google AI Studio (Gemini 2.0 Flash) for ultra-fast cloud responses (~1s) or Local Ollama for offline private execution. Predefined clinical guardrails keep the AI strictly bounded to ElderCare NHMS.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            size="sm"
            onClick={handleTestConnection}
            disabled={testing}
            className="border-blue-400/40 text-blue-100 hover:bg-blue-600/30 bg-blue-950/40 cursor-pointer"
          >
            {testing ? (
              <>
                <Activity className="h-4 w-4 animate-spin mr-1.5" /> Testing...
              </>
            ) : (
              <>
                <Zap className="h-4 w-4 mr-1.5 text-amber-300" /> Test Connection
              </>
            )}
          </Button>

          <Button
            size="sm"
            onClick={handleSave}
            disabled={saving}
            className="bg-blue-600 hover:bg-blue-700 text-white font-medium cursor-pointer shadow-sm"
          >
            {saving ? (
              <>
                <Activity className="h-4 w-4 animate-spin mr-1.5" /> Saving...
              </>
            ) : (
              <>
                <Save className="h-4 w-4 mr-1.5" /> Save Configuration
              </>
            )}
          </Button>
        </div>
      </div>

      {/* Real-time Connection Status Indicator */}
      {testResult && (
        <div
          className={`p-4 rounded-xl border flex items-center justify-between ${
            testResult.success
              ? "bg-emerald-50/80 border-emerald-200 text-emerald-900"
              : "bg-red-50/80 border-red-200 text-red-900"
          }`}
        >
          <div className="flex items-center gap-3">
            {testResult.success ? (
              <CheckCircle2 className="h-5 w-5 text-emerald-600 shrink-0" />
            ) : (
              <AlertCircle className="h-5 w-5 text-red-600 shrink-0" />
            )}
            <div>
              <p className="text-sm font-semibold">
                {testResult.success ? "Connection Verified" : "Connection Test Failed"}
              </p>
              <p className="text-xs opacity-85 mt-0.5">{testResult.message}</p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <Badge variant="outline" className="font-mono text-xs">
              {testResult.provider} : {testResult.model}
            </Badge>
            {testResult.success && (
              <Badge className="bg-emerald-600 text-white font-mono text-xs">
                {testResult.latencyMs} ms
              </Badge>
            )}
          </div>
        </div>
      )}

      {/* Provider Selector Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Google Gemini Card */}
        <div
          onClick={() => setSettings((prev) => ({ ...prev, provider: "GOOGLE_GEMINI" }))}
          className={`relative p-5 rounded-xl border-2 transition-all cursor-pointer ${
            settings.provider === "GOOGLE_GEMINI"
              ? "border-blue-600 bg-blue-50/40 shadow-sm"
              : "border-slate-200 bg-white hover:border-slate-300"
          }`}
        >
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-3">
              <div className={`p-2.5 rounded-lg ${settings.provider === "GOOGLE_GEMINI" ? "bg-blue-600 text-white" : "bg-slate-100 text-slate-700"}`}>
                <Cloud className="h-5 w-5" />
              </div>
              <div>
                <h3 className="font-bold text-slate-900 text-base flex items-center gap-2">
                  Google Gemini API
                  <Badge className="bg-emerald-100 text-emerald-700 border-emerald-200 text-[10px] font-semibold">
                    Fast ~1s
                  </Badge>
                </h3>
                <p className="text-xs text-slate-500 mt-0.5">Google AI Studio Cloud Inference</p>
              </div>
            </div>
            <div className={`h-5 w-5 rounded-full border flex items-center justify-center ${settings.provider === "GOOGLE_GEMINI" ? "border-blue-600 bg-blue-600 text-white" : "border-slate-300"}`}>
              {settings.provider === "GOOGLE_GEMINI" && <Check className="h-3 w-3 stroke-[3]" />}
            </div>
          </div>
          <p className="text-xs text-slate-600 mt-3">
            Best for real-time interactive chatbot and instant incident triage. Zero local GPU/CPU load.
          </p>
        </div>

        {/* Local Ollama Card */}
        <div
          onClick={() => setSettings((prev) => ({ ...prev, provider: "LOCAL_OLLAMA" }))}
          className={`relative p-5 rounded-xl border-2 transition-all cursor-pointer ${
            settings.provider === "LOCAL_OLLAMA"
              ? "border-blue-600 bg-blue-50/40 shadow-sm"
              : "border-slate-200 bg-white hover:border-slate-300"
          }`}
        >
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-3">
              <div className={`p-2.5 rounded-lg ${settings.provider === "LOCAL_OLLAMA" ? "bg-blue-600 text-white" : "bg-slate-100 text-slate-700"}`}>
                <Server className="h-5 w-5" />
              </div>
              <div>
                <h3 className="font-bold text-slate-900 text-base flex items-center gap-2">
                  Local Ollama
                  <Badge variant="outline" className="bg-slate-100 text-slate-700 text-[10px] font-semibold">
                    Offline / Private
                  </Badge>
                </h3>
                <p className="text-xs text-slate-500 mt-0.5">Self-hosted local workstation LLM</p>
              </div>
            </div>
            <div className={`h-5 w-5 rounded-full border flex items-center justify-center ${settings.provider === "LOCAL_OLLAMA" ? "border-blue-600 bg-blue-600 text-white" : "border-slate-300"}`}>
              {settings.provider === "LOCAL_OLLAMA" && <Check className="h-3 w-3 stroke-[3]" />}
            </div>
          </div>
          <p className="text-xs text-slate-600 mt-3">
            100% on-premise execution with zero external data egress. Operates without internet connectivity.
          </p>
        </div>
      </div>

      {/* Provider Details Configuration Card */}
      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="border-b border-slate-100 bg-slate-50/60 pb-4">
          <CardTitle className="text-base font-bold text-slate-900 flex items-center gap-2">
            <Sliders className="h-4 w-4 text-blue-600" />
            {settings.provider === "GOOGLE_GEMINI" ? "Google AI Studio Parameters" : "Ollama Local Service Parameters"}
          </CardTitle>
          <CardDescription className="text-xs">
            {settings.provider === "GOOGLE_GEMINI"
              ? "Configure your API key and fast flash model parameters for Google Cloud AI"
              : "Configure your local Ollama HTTP host and model identifier"}
          </CardDescription>
        </CardHeader>
        <CardContent className="p-6 space-y-5">
          {settings.provider === "GOOGLE_GEMINI" ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2 md:col-span-2">
                <div className="flex items-center justify-between">
                  <Label className="text-xs font-semibold text-slate-700 flex items-center gap-1.5">
                    Google AI Studio API Key <span className="text-red-500">*</span>
                  </Label>
                  <a
                    href="https://aistudio.google.com/app/apikey"
                    target="_blank"
                    rel="noreferrer"
                    className="text-xs text-blue-600 hover:text-blue-700 flex items-center gap-1 font-medium hover:underline"
                  >
                    Get free API Key from Google AI Studio <ExternalLink className="h-3 w-3" />
                  </a>
                </div>
                <div className="relative">
                  <Input
                    type={showApiKey ? "text" : "password"}
                    value={settings.geminiApiKey || ""}
                    onChange={(e) => {
                      const val = e.target.value;
                      setSettings((prev) => ({ ...prev, geminiApiKey: val }));
                      if (val && !val.includes("...")) {
                        localStorage.setItem("eldercare_gemini_api_key", val);
                      }
                    }}
                    placeholder="AIzaSy..."
                    className="pr-10 font-mono text-sm"
                  />
                  <button
                    type="button"
                    onClick={() => setShowApiKey(!showApiKey)}
                    className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 cursor-pointer p-1"
                  >
                    {showApiKey ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
                <p className="text-[11px] text-slate-500">
                  Your key is securely stored server-side in <code className="bg-slate-100 px-1 py-0.5 rounded text-slate-700 font-mono">ai-settings.json</code>.
                </p>
              </div>

              <div className="space-y-2">
                <Label className="text-xs font-semibold text-slate-700">Gemini Model Identifier</Label>
                <div className="flex gap-2">
                  <Input
                    value={settings.geminiModel || "gemini-3.8-flash"}
                    onChange={(e) => setSettings((prev) => ({ ...prev, geminiModel: e.target.value }))}
                    placeholder="e.g. gemini-3.8-flash"
                    className="font-mono text-sm flex-1"
                  />
                  <Select
                    value={settings.geminiModel || "gemini-3.8-flash"}
                    onValueChange={(val) => setSettings((prev) => ({ ...prev, geminiModel: val }))}
                  >
                    <SelectTrigger className="w-[170px]">
                      <SelectValue placeholder="Presets" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="gemini-3.8-flash">gemini-3.8-flash (Recommended)</SelectItem>
                      <SelectItem value="gemini-2.5-flash">gemini-2.5-flash</SelectItem>
                      <SelectItem value="gemini-1.5-flash">gemini-1.5-flash</SelectItem>
                      <SelectItem value="gemini-1.5-pro">gemini-1.5-pro</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div className="flex flex-wrap items-center gap-1.5 pt-1">
                  <span className="text-[11px] text-slate-400">Quick Switch:</span>
                  {[
                    { id: "gemini-3.8-flash", label: "gemini-3.8-flash" },
                    { id: "gemini-2.5-flash", label: "gemini-2.5-flash" },
                    { id: "gemini-1.5-flash", label: "gemini-1.5-flash (Most Stable)" },
                    { id: "gemini-1.5-pro", label: "gemini-1.5-pro" },
                  ].map((m) => (
                    <button
                      key={m.id}
                      type="button"
                      onClick={() => setSettings((prev) => ({ ...prev, geminiModel: m.id }))}
                      className={`text-[11px] px-2 py-0.5 rounded-full border transition-colors cursor-pointer ${
                        settings.geminiModel === m.id
                          ? "bg-blue-600 text-white border-blue-600 font-medium shadow-xs"
                          : "bg-slate-100 hover:bg-slate-200 text-slate-700 border-slate-200"
                      }`}
                    >
                      {m.label}
                    </button>
                  ))}
                </div>
                <p className="text-[11px] text-slate-500">If a model encounters high demand (503), the backend automatically falls back to an alternate flash model, or you can switch directly above.</p>
              </div>

              <div className="space-y-2">
                <Label className="text-xs font-semibold text-slate-700">Max Generation Tokens</Label>
                <Input
                  type="number"
                  min={128}
                  max={2048}
                  value={settings.maxTokens || 512}
                  onChange={(e) => setSettings((prev) => ({ ...prev, maxTokens: parseInt(e.target.value) || 512 }))}
                />
                <p className="text-[11px] text-slate-500">Lower tokens (384-512) increase response velocity.</p>
              </div>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label className="text-xs font-semibold text-slate-700">Ollama Base URL</Label>
                <Input
                  value={settings.ollamaBaseUrl || "http://localhost:11434"}
                  onChange={(e) => setSettings((prev) => ({ ...prev, ollamaBaseUrl: e.target.value }))}
                  placeholder="http://localhost:11434"
                  className="font-mono text-sm"
                />
                <p className="text-[11px] text-slate-500">Local or LAN Ollama endpoint URL</p>
              </div>

              <div className="space-y-2">
                <Label className="text-xs font-semibold text-slate-700">Ollama Model Identifier</Label>
                <Input
                  value={settings.ollamaModel || "qwen3.5:2b-q4_K_M"}
                  onChange={(e) => setSettings((prev) => ({ ...prev, ollamaModel: e.target.value }))}
                  placeholder="e.g. qwen3.5:2b-q4_K_M"
                  className="font-mono text-sm"
                />
                <p className="text-[11px] text-slate-500">Installed model via <code className="bg-slate-100 px-1 py-0.5 rounded text-slate-700 font-mono">ollama pull</code></p>
              </div>

              <div className="space-y-2">
                <Label className="text-xs font-semibold text-slate-700">Sampling Temperature</Label>
                <Input
                  type="number"
                  step="0.05"
                  min={0.0}
                  max={1.0}
                  value={settings.temperature ?? 0.2}
                  onChange={(e) => setSettings((prev) => ({ ...prev, temperature: parseFloat(e.target.value) || 0.2 }))}
                />
                <p className="text-[11px] text-slate-500">0.2 provides high predictability and speed for clinical facts.</p>
              </div>

              <div className="space-y-2">
                <Label className="text-xs font-semibold text-slate-700">Max Prediction Tokens</Label>
                <Input
                  type="number"
                  min={128}
                  max={1024}
                  value={settings.maxTokens || 512}
                  onChange={(e) => setSettings((prev) => ({ ...prev, maxTokens: parseInt(e.target.value) || 512 }))}
                />
                <p className="text-[11px] text-slate-500">Prevents long wait times during local generation.</p>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Clinical Guardrails & Application Boundaries */}
      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="border-b border-slate-100 bg-slate-50/60 pb-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <ShieldCheck className="h-4 w-4 text-emerald-600" />
              <CardTitle className="text-base font-bold text-slate-900">
                Application Scope & Clinical System Rules
              </CardTitle>
            </div>
            <Button
              variant="ghost"
              size="sm"
              onClick={handleResetRules}
              className="text-xs text-slate-600 hover:text-slate-900 cursor-pointer h-7"
            >
              <RotateCcw className="h-3.5 w-3.5 mr-1" /> Reset to Default
            </Button>
          </div>
          <CardDescription className="text-xs">
            These system rules are automatically prepended to every prompt across the entire application. They strictly enforce the ElderCare NHMS scope, prevent the AI from answering out-of-scope requests, and instruct the model to produce concise, fast responses.
          </CardDescription>
        </CardHeader>
        <CardContent className="p-6">
          <Textarea
            rows={6}
            value={settings.systemRules || ""}
            onChange={(e) => setSettings((prev) => ({ ...prev, systemRules: e.target.value }))}
            className="font-mono text-xs leading-relaxed bg-slate-50/70 border-slate-300 resize-y"
            placeholder="System rules and scope boundaries for all AI models..."
          />
          <div className="mt-3 flex items-center gap-2 text-xs text-slate-500">
            <CheckCircle2 className="h-4 w-4 text-emerald-600 shrink-0" />
            <span>AI responses are strictly constrained to ElderCare NHMS clinical workflows.</span>
          </div>
        </CardContent>
      </Card>

      {/* Subsystem Activation Toggles */}
      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="border-b border-slate-100 bg-slate-50/60 pb-4">
          <CardTitle className="text-base font-bold text-slate-900 flex items-center gap-2">
            <Zap className="h-4 w-4 text-amber-500" /> Subsystem AI Activation Matrix
          </CardTitle>
          <CardDescription className="text-xs">
            Enable or disable AI capabilities individually across nursing home modules
          </CardDescription>
        </CardHeader>
        <CardContent className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Chatbot */}
            <div className="flex items-center justify-between p-4 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-blue-50 text-blue-600">
                  <Bot className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-sm font-semibold text-slate-900">Clinical Assistant Chatbot</h4>
                  <p className="text-xs text-slate-500">Interactive resident care & guideline advisor</p>
                </div>
              </div>
              <Switch
                checked={settings.features?.chatbot !== false}
                onCheckedChange={(val) => handleFeatureToggle("chatbot", val)}
              />
            </div>

            {/* Incident Analysis */}
            <div className="flex items-center justify-between p-4 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-red-50 text-red-600">
                  <Activity className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-sm font-semibold text-slate-900">Incident Triage & Severity</h4>
                  <p className="text-xs text-slate-500">Automated classification of falls & medical alerts</p>
                </div>
              </div>
              <Switch
                checked={settings.features?.incidentAnalysis !== false}
                onCheckedChange={(val) => handleFeatureToggle("incidentAnalysis", val)}
              />
            </div>

            {/* Care Plan Generator */}
            <div className="flex items-center justify-between p-4 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-purple-50 text-purple-600">
                  <FileText className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-sm font-semibold text-slate-900">Care Plan & Goal Generator</h4>
                  <p className="text-xs text-slate-500">Evidence-based interventions & SMART care goals</p>
                </div>
              </div>
              <Switch
                checked={settings.features?.carePlan !== false}
                onCheckedChange={(val) => handleFeatureToggle("carePlan", val)}
              />
            </div>

            {/* Voice Care Dictation */}
            <div className="flex items-center justify-between p-4 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-emerald-50 text-emerald-600">
                  <Mic className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-sm font-semibold text-slate-900">Voice Care Note Parsing</h4>
                  <p className="text-xs text-slate-500">CNA speech-to-vitals & dietary parsing</p>
                </div>
              </div>
              <Switch
                checked={settings.features?.voiceCare !== false}
                onCheckedChange={(val) => handleFeatureToggle("voiceCare", val)}
              />
            </div>

            {/* Medication Safety */}
            <div className="flex items-center justify-between p-4 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-amber-50 text-amber-600">
                  <Pill className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-sm font-semibold text-slate-900">Medication Safety Checker</h4>
                  <p className="text-xs text-slate-500">Geriatric drug interaction & allergy scanner</p>
                </div>
              </div>
              <Switch
                checked={settings.features?.medicationSafety !== false}
                onCheckedChange={(val) => handleFeatureToggle("medicationSafety", val)}
              />
            </div>

            {/* Elopement Risk */}
            <div className="flex items-center justify-between p-4 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-indigo-50 text-indigo-600">
                  <Radio className="h-4 w-4" />
                </div>
                <div>
                  <h4 className="text-sm font-semibold text-slate-900">Memory Care Wandering Risk</h4>
                  <p className="text-xs text-slate-500">Elopement behavioral scoring & perimeter alerts</p>
                </div>
              </div>
              <Switch
                checked={settings.features?.elopementRisk !== false}
                onCheckedChange={(val) => handleFeatureToggle("elopementRisk", val)}
              />
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};
