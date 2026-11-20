# ElderCare local Ollama setup

The two Modelfiles set system instructions and a few example exchanges. `ollama create` packages those prompts with the base model; it **does not fine-tune model weights**. The JSONL files under `data/` are curated examples for review and future supervised fine-tuning or evaluation. They are not loaded by Ollama at runtime. See the [Ollama Modelfile reference](https://docs.ollama.com/modelfile) and [model import guide](https://docs.ollama.com/import).

## What the backend uses today

- Public chatbot and FAQ: `src/main/resources/ai/website-knowledge.json` is loaded at startup. A small keyword matcher supplies at most three relevant, source-traceable public facts to the chosen provider. This is local retrieval, not embedding-based RAG. Fallback answers also come from this file.
- Other website tools (admission screener, care quiz, memory explainer): their runtime prompts and rule fallbacks remain separate from clinical staff context. The Care Navigator accepts a personal goal and mobility information so the provider can explain the suggestion and generate questions for staff. The admission pre-screener uses the selected needs and optional notes, validates the proposed program, and never decides eligibility. The JSONL examples describe desired behavior but are not automatically loaded.
- Internal staff AI: receives its existing role-specific prompt and runtime clinical context. The curated examples avoid specific drug doses and emergency schedules; local clinical policy still needs a qualified owner to review it.

## Create the local models

On this machine, Ollama 0.33.2 is in `D:\CreateAssetGamer\tools\ai\ollama\ollama.exe` and its model store is `D:\CreateAssetGamer\tools\ai\models\ollama`. The backend starts the local service in CPU mode automatically during a Windows `dev` run with `mvn spring-boot:run`. The GTX 1650 GPU stalled during model warmup in a local check. To start Ollama without the backend or set up models, run from this directory:

```powershell
.\start-local-ollama-cpu.ps1
.\setup-eldercare-model.ps1
```

The base tag is `qwen3.5:2b-q4_K_M`. `-PullBase` downloads it; omit the switch if already present. The script accepts `-OllamaExe` and `-ModelsPath` to use another installation. It creates `eldercare-ai` for staff and `eldercare-website` for visitors. To inspect them on this machine:

```powershell
& 'D:\CreateAssetGamer\tools\ai\ollama\ollama.exe' list
& 'D:\CreateAssetGamer\tools\ai\ollama\ollama.exe' run eldercare-website "What is the exact monthly price?"
```

In Admin → Settings → AI, choose Local Ollama and set the model to `eldercare-ai`. Public requests default to `eldercare-website`; `ELDERCARE_AI_OLLAMA_WEBSITE_MODEL` can override that name. The backend sends `think: false` to Ollama so the response includes usable final content within its token budget. If Ollama is unavailable, the website returns a labeled rule-based fallback.

## Review and update data

Each public fact records the source website file. The contact details, admission timing, insurance wording, visiting hours, and service claims in this demo project need business verification before a real facility publishes them. Edit the public JSON and the corresponding website page together. Do not put resident records, protected health information, API keys, or internal clinical rules in public facts or JSONL examples.

`data/website-examples.jsonl` contains 25 bilingual and English examples covering the public chatbot, FAQ, screener, quiz, and memory explainer. They can be used for review and evaluation; the live chatbot uses `website-knowledge.json` and the current LOC table instead. `Modelfile.website` includes a smaller set of examples that are packaged into the `eldercare-website` model with `ollama create`. This is prompt configuration, **not weight fine-tuning**. `data/staff-examples.jsonl` covers documentation and escalation examples for staff. These are small seed/evaluation sets, **not** enough to train a reliable clinical model. Real fine-tuning would require a separate training pipeline, consented data, qualified clinical review, evaluation, and import of trained weights into Ollama. Admin F6 now uses [Ollama embeddings](https://docs.ollama.com/capabilities/embeddings) with SQL Server; see [AI_ADMIN_SETUP.md](../AI_ADMIN_SETUP.md).
