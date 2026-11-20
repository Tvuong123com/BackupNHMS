# Admin AI F1–F10: local setup

The admin AI features use the existing SQL Server database and local Ollama. The Ollama Modelfile packages prompts and examples; it does not fine-tune model weights. See [ollama/README.md](ollama/README.md) for the example data and model setup.

For clinical scope, responsibilities, validation gates, and the US-source references, see [CLINICAL_AI_VALIDATION_PLAN.md](CLINICAL_AI_VALIDATION_PLAN.md). These features are not clinically validated solely by passing software tests.

## Prerequisites

1. Start the configured SQL Server instance and apply the existing project schema.
2. Apply `src/main/resources/schema-migration-ai-admin.sql` to that database. It creates `ai_chat_turns` (F1), `ai_vector_documents` (F6), `ai_voice_observations` (F7), `ai_summary_drafts` (F5/F10), and `ai_fall_screenings` (F4). It also backfills legacy `incidents.created_at` and `care_plans.created_by` columns if absent. Run the SQL Server script with a client that understands `GO` batch separators.
3. On this Windows development workstation, `mvn spring-boot:run "-Dmaven.test.skip=true"` starts local Ollama automatically if port 11434 is not responding. It uses the executable at `D:\CreateAssetGamer\tools\ai\ollama\ollama.exe` and model store at `D:\CreateAssetGamer\tools\ai\models\ollama`. On this workstation, GPU model warmup stalled; the startup uses CPU mode. `ollama/start-local-ollama-cpu.ps1` remains available when Ollama is needed without the backend. Set `ELDERCARE_AI_OLLAMA_AUTOSTART=false` to disable automatic startup.
4. Ensure the `eldercare-ai`, `eldercare-website`, and `nomic-embed-text` models are installed. The embedding model supplies 768-dimensional vectors for F6. Use `ollama pull nomic-embed-text` if it is absent.
5. In Admin → Settings → AI, select Local Ollama, set the chat model to `eldercare-ai`, and test the connection. The local `backend/ai-settings.json` is private and ignored by Git.

The default SQL Server development profile reads `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` from the environment. On this workstation the existing `nhms-db` Docker container serves the project database on `localhost:1433`. The Windows `MSSQLSERVER` service is a separate instance on port 1434; starting it does not start the project database.

## Initialize and use search

After the migration, sign in as an administrator or DON and click **Refresh index** in the admin dashboard search panel. This embeds the built-in staff workflow reminders, care goals, and incidents from the last 90 days. It stores JSON vectors in SQL Server and ranks matches using cosine similarity in the backend. A scheduled refresh runs daily at 02:00 server time; override it with `eldercare.ai.search.reindex-cron` if needed. Refresh manually after importing many records.

`GET /api/v1/ai/search` is restricted to nurse, DON, and administrator roles. `POST /api/v1/ai/search/reindex` is restricted to DON and administrator roles. In the staff chat, nurse/DON/admin accounts may receive retrieved incident and care-goal context; CNA accounts receive workflow reminders only. Search content is scoped to authenticated staff, and the chat shows source identifiers. The workflow reminders are operational examples, not approved clinical policy.

## Feature behavior

| Feature | Admin workflow |
| --- | --- |
| F1 | Staff chat in the admin layout; per-user conversation history in SQL Server; retrieved source context when the index is ready. |
| F2 | Incident description analysis in the incident form; staff review the suggested classification. |
| F3 | On a resident care plan, generate draft goals from recorded assessment, diagnoses, and incidents; save selected goals and interventions as `PENDING_REVIEW`. |
| F4 | Dashboard shows all active residents. It scores documented incidents from the last 90 days with lower weight for days 31–90; a resident without incidents has no incident score. The latest ADL total is chart context and marked confirmed or unverified. The resident AI Review tab records all six [Morse Fall Scale items](https://www.ahrq.gov/sites/default/files/publications/files/fallpxtoolkit.pdf), calculated server-side and signed with the staff account; the dashboard also shows the latest raw Morse score. The Morse score does not change the incident score or create a risk category because cutoffs require facility calibration. This is not a validated 7–30 day prediction. |
| F5 | Dashboard shift draft uses recorded care tasks and incidents plus optional verified notes; it is saved for the requesting staff account and requires review. |
| F6 | Admin dashboard semantic search over indexed workflow reminders, incident summaries, and care goals. |
| F7 | Care task resident card parses a spoken or typed note; staff confirm the draft, which saves an observation, writes any reviewed vital values to `vital_signs`, and can complete a matching non-medication task. Locked resident charts reject the write. It does not write to eMAR. |
| F8 | Resident review tab drafts an elopement concern review from documented behavior; it never activates door controls. |
| F9 | Resident review tab shows active `medication_orders`, chart allergies and diagnoses, and any additional manually entered MAR names. It explicitly flags missing chart entries for verification. Exact-name duplicate/allergy checks and medication-pair questions are deterministic. The UI links each name to [NLM DailyMed label search](https://dailymed.nlm.nih.gov/dailymed/search.cfm?query=acetaminophen); the reviewer must select the matching product/formulation. There is no medication-order editing UI in this repository, so this is a separate review workflow. No interaction severity or mechanism is inferred without an approved reference. It never clears a drug for administration or supplies a dose. |
| F10 | Dashboard saves a resident-specific family update **draft visible only to its creator in admin**. There is no family publication, email, or SMS. |

All clinical outputs need staff review. F8/F9 are decision support; this project has no validated predictive model or comprehensive drug interaction database. F9 intentionally does not infer interaction severity or mechanism from the language model. An empty or unavailable AI response must not become a clinical order.

The public openFDA label API [explicitly warns against relying on its results for medical-care decisions](https://open.fda.gov/apis/drug/label/understanding-the-api-results/). DailyMed search is offered as a reference navigation aid, not an automated interaction clearance. To turn F9 into a verified interaction checker, the facility must provide an approved interaction dataset or service with product identifiers, update cadence, and pharmacist sign-off. To validate F4 prediction, longitudinal assessments and future fall outcomes plus locally approved thresholds are required; the current sample has no completed Morse assessments.

## Verification on this workstation

On 2026-10-05, the project database in the existing `nhms-db` Docker container was healthy, the migration above was applied, and the backend connected to SQL Server. The full backend Maven test suite and frontend TypeScript/Vite build passed. Authenticated API smoke checks passed for F1 chat/history, F2 classification, F3 suggestions, F4 review priorities (10 real resident records), F5 and F10 persisted drafts, F6 reindex/search (40 documents), F7 confirmed vital-sign write, F8 review, and F9 reconciliation; W1–W4 and W6 public endpoints also returned responses. Temporary F1/F5/F7/F10 smoke records were removed after verification. These API checks verify wiring and persistence, not clinical accuracy.

The F4 Morse endpoint was additionally checked with a temporary six-item screening: POST calculated 80, GET returned 80, then the row was removed. The seeded nurse role could read the endpoint and the CNA role received 403. In the browser, the website chatbot answered a services question, the admin login loaded the dashboard and resident AI Review tab, and F9 displayed the chart order with DailyMed search links. The local Vite origin `127.0.0.1:5173` is now allowed by backend CORS alongside `localhost:5173`.

After the F4 coverage and F9 chart-context changes, the complete backend suite passed again: 276 tests, 0 failures/errors/skips. The frontend TypeScript/Vite build also passed. Docker, Ollama, and the local application servers were stopped during this final rerun, so these changes have unit/build verification but not a new live SQL Server/browser smoke test. The earlier live checks above predate these two refinements.

The first clinical-readiness increment adds `generatedAt`, logic version, source record IDs, and explicit data gaps to the F4/F9 API responses. F9 also exposes `interactionReferenceStatus=NOT_CONNECTED` and separates active chart orders from manually entered MAR names. The staff UI shows these fields under **Review basis and missing data**. `GET /auth/me` now supplies the actual logged-in identity and role to the frontend instead of using a fixed DON demo role; the backend restricts F4 review priorities to Nurse/DON/admin and remains the authority for access. The frontend role-to-menu mapping covers known local roles and fails closed for unknown roles; it is not a dynamic permission service. The full 277-test backend suite and frontend build passed after this increment. Live SQL Server/browser verification remains pending because Docker Desktop and Ollama are stopped; starting the Docker service on this workstation was denied by the operating system.
