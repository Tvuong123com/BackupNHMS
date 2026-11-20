# 📱 ĐẶC TẢ KỸ THUẬT & KẾ HOẠCH TRIỂN KHAI: NHMS CARECOMPANION
### (ỨNG DỤNG HỖ TRỢ THỰC THI NHIỆM VỤ ĐIỀU DƯỠNG & HỘ LÝ TẠI BUỒNG BỆNH)

> **Trạng thái tài liệu:** Bản đặc tả kỹ thuật có căn cứ mã nguồn (Source-Grounded Technical Specification)  
> **Phiên bản:** 2.0 (Đã rà soát & đồng bộ toàn diện với Spring Boot Backend & React Frontend)  
> **Nguyên tắc cốt lõi:** Tính trung thực mã nguồn · An toàn lâm sàng tuyệt đối · Kiến trúc Online-First · Thẩm định con người (Human-in-the-Loop)

---

## 📑 MỤC LỤC
1. [BỐI CẢNH & PHẠM VI DỰ ÁN (SCOPE & OBJECTIVES)](#1-bối-cảnh--phạm-vi-dự-án-scope--objectives)
2. [KIẾN TRÚC HỆ THỐNG THỰC TẾ (SYSTEM ARCHITECTURE)](#2-kiến-trúc-hệ-thống-thực-tế-system-architecture)
   - 2.1. Phân tầng kiến trúc tổng thể
   - 2.2. Cơ chế xác thực & Quản lý phiên thực tế (Authentication Reality)
   - 2.3. Bảng đối chiếu API chuẩn xác với mã nguồn hiện tại
3. [RANH GIỚI AN TOÀN LÂM SÀNG & VAI TRÒ AI (CLINICAL SAFETY & AI BOUNDARIES)](#3-ranh-giới-an-toàn-lâm-sàng--vai-trò-ai-clinical-safety--ai-boundaries)
   - 3.1. Dữ liệu y tế khách quan vs Suy diễn lâm sàng
   - 3.2. Định nghĩa đúng đắn về "Humanizer" trong giao diện
   - 3.3. Quy trình ghi chú giọng nói có người duyệt (Human-in-the-Loop Voice Observation)
4. [ĐẶC TẢ MVP CỐT LÕI CÓ THỂ KIỂM CHỨNG (VERIFIABLE MVP CORE LOOP)](#4-đặc-tả-mvp-cốt-lõi-có-thể-kiểm-chứng-verifiable-mvp-core-loop)
   - 4.1. Luồng nghiệp vụ 5 bước chuẩn hóa
   - 4.2. Đặc tả 2 màn hình MVP (Jetpack Compose)
   - 4.3. UI/UX Công thái học buồng bệnh (Clinical Ergonomics)
5. [LỘ TRÌNH PHÁT TRIỂN PHÂN KỲ (PHASED ROADMAP)](#5-lộ-trình-phát-triển-phân-kỳ-phased-roadmap)
   - Phase 1: MVP Trực tuyến (Online-First Core Loop)
   - Phase 2: Cơ chế Giám sát & Thông báo đẩy (Push Notifications)
   - Phase 3: Trợ lý Ghi chú Giọng nói (Voice Drafting & SBAR Integration)
   - Phase 4: Bộ nhớ đệm Ngoại tuyến & Bảo vệ Dữ liệu PHI (Secure Offline Caching)
6. [KỊCH BẢN THỬ NGHIỆM & KIỂM CHỨNG TẠI CHỖ (E2E VERIFICATION SCRIPT)](#6-kịch-bản-thử-nghiệm--kiểm-chứng-tại-chỗ-e2e-verification-script)
7. [MỤC TIÊU NGHIÊN CỨU & PHƯƠNG PHÁP ĐO LƯỜNG (RESEARCH HYPOTHESES & KPIS)](#7-mục-tiêu-nghiên-cứu--phương-pháp-đo-lường-research-hypotheses--kpis)

---

## 1. BỐI CẢNH & PHẠM VI DỰ ÁN (SCOPE & OBJECTIVES)

### 1.1. Vấn đề thực tế
* Trong hệ thống quản lý viện dưỡng lão NHMS, phân hệ Web quản trị (`/admin/dashboard`, `/admin/care-tasks`, `/admin/care-plans`) đóng vai trò là **Trung tâm Điều hành (Command Center)** dành cho Bác sĩ, Giám đốc Điều dưỡng (DON) và Quản trị viên.
* Tuy nhiên, đội ngũ Hộ lý (CNA) và Điều dưỡng viên chăm sóc trực tiếp (Bedside Nurse) liên tục di chuyển giữa các phòng bệnh. Việc bắt buộc nhân viên phải quay về trạm điều dưỡng để xem danh sách công việc trên máy tính để bàn làm gián đoạn quy trình chăm sóc và kéo dài thời gian cập nhật trạng thái nhiệm vụ.

### 1.2. Định vị MVP (Tối giản & Khả thi)
* **Không làm:** Không chuyển toàn bộ các phân hệ phức tạp của Web (quản lý nhập viện, thiết lập cơ sở, tính phí LOC, phân quyền hệ thống) sang Android.
* **Tập trung làm:** Xây dựng một ứng dụng Android tinh gọn mang tên **`NHMS CareCompanion`** dành riêng cho vai trò **CNA / Điều dưỡng ca trực**, tập trung vào đúng một luồng giá trị cốt lõi:
  $$\text{Đăng nhập ca trực} \longrightarrow \text{Xem nhiệm vụ được giao} \longrightarrow \text{Mở thông tin cần thiết} \longrightarrow \text{Xác nhận hoàn thành} \longrightarrow \text{Đồng bộ về Web}$$

---

## 2. KIẾN TRÚC HỆ THỐNG THỰC TẾ (SYSTEM ARCHITECTURE)

### 2.1. Phân tầng kiến trúc tổng thể

```mermaid
flowchart TD
    subgraph ANDROID["📱 Ứng dụng Android (NHMS CareCompanion)"]
        UI["Jetpack Compose UI<br/>(Material Design 3 · Công thái học buồng bệnh)"]
        VM["ViewModels & UI State (StateFlow)"]
        REPO["Repository Pattern (Online-First)"]
        NET["Retrofit Client + OkHttp Interceptor"]
        UI --> VM --> REPO --> NET
    end

    subgraph BACKEND["⚡ Máy chủ Spring Boot (Port 8080)"]
        AUTH_FILTER["JwtAuthenticationFilter<br/>(SessionStore Token Resolver)"]
        TASK_CTRL["CareTaskGroupedController<br/>(/api/v1/tasks/*)"]
        AI_VOICE["AiController<br/>(/api/v1/ai/voice/*)"]
        RISK_CTRL["AiController<br/>(/api/v1/ai/risk/*)"]
    end

    subgraph DB["🗄️ Cơ sở dữ liệu SQL Server"]
        T_TASKS["dbo.care_tasks"]
        T_USERS["dbo.users & dbo.roles"]
        T_OBS["dbo.voice_care_observations"]
    end

    NET -- "HTTP JSON (Bearer Token)" --> AUTH_FILTER
    AUTH_FILTER --> TASK_CTRL & AI_VOICE & RISK_CTRL
    TASK_CTRL --> T_TASKS
    AUTH_FILTER --> T_USERS
    AI_VOICE --> T_OBS
```

---

### 2.2. Cơ chế Xác thực Thực tế (Authentication Reality)

> [!IMPORTANT]
> **Hiện trạng Backend:** Backend hiện tại (`AuthServiceImpl.java:75`) sử dụng cơ chế **In-Memory Session Token** có tiền tố `mock-jwt-token-` kết hợp lưu trữ trong `sessionStore`, không phải mã hóa JSON Web Token (JWT RFC 7519) ký bằng secret key đối xứng/bất đối xứng.

* **Luồng xác thực của Android Client:**
  1. Gửi thông tin đăng nhập: `POST /api/v1/auth/login` với `{ username, password }`.
  2. Phản hồi nhận về:
     ```json
     {
       "token": "mock-jwt-token-a1b2c3d4",
       "sessionId": "sess_e5f6g7",
       "role": "CNA",
       "fullName": "Maria Santos",
       "mustChangePassword": false
     }
     ```
  3. **Lưu trữ phiên trên máy:** Lưu chuỗi `token`, `role`, `fullName` vào `EncryptedSharedPreferences` của Android.
  4. **Gửi kèm trong mọi Request:** Cấu hình `OkHttp Interceptor` tự động đính kèm Header:
     `Authorization: Bearer mock-jwt-token-a1b2c3d4`
  5. **Xử lý hết hạn phiên:** `JwtAuthenticationFilter` kiểm tra phiên trong `sessionStore`. Nếu máy chủ khởi động lại hoặc phiên bị xóa, backend trả về mã `401 Unauthorized`. Android Client bắt mã 401 qua OkHttp Authenticator/Interceptor $\rightarrow$ Xóa token đã lưu và chuyển hướng người dùng về màn hình Đăng nhập một cách an toàn.

---

### 2.3. Bảng Đối chiếu API Chuẩn xác (Source Code Verification)

Toàn bộ các endpoint dưới đây được trích xuất trực tiếp từ mã nguồn Java backend (`RouteConstants.java`, `CareTaskGroupedController.java`, `AiController.java`) và mã nguồn React frontend (`care-tasks-api.ts`, `ai-service.ts`):

| Nhóm chức năng | Endpoint thực tế trên Backend | HTTP Method | Trạng thái hiện tại | Mô tả & Cách ứng dụng cho Android |
| :--- | :--- | :--- | :--- | :--- |
| **Xác thực** | `/api/v1/auth/login` | `POST` | **Có sẵn 100%** | Nhận `token` phiên làm việc và định danh vai trò (`CNA`, `NURSE`). |
| **Nhiệm vụ theo CNA** | `/api/v1/tasks/by-cna` | `GET` | **Có sẵn 100%** | Nhận tham số query `cnaId={userId}` để tải toàn bộ danh sách task được giao cho nhân viên đang trực. |
| **Nhiệm vụ theo Phòng** | `/api/v1/tasks/by-resident` | `GET` | **Có sẵn 100%** | Nhận tham số query `residentId={id}` để xem danh sách việc cần làm khi đang đứng tại buồng cụ. |
| **Tìm kiếm Nhiệm vụ** | `/api/v1/tasks/search` | `GET` | **Có sẵn 100%** | Hỗ trợ lọc theo `status` (`PENDING`, `COMPLETED`), ngày thực hiện, phân trang (`page`, `size`). |
| **Hoàn thành Nhiệm vụ** | `/api/v1/tasks/{taskId}/completed` | `PATCH` | **Có sẵn 100%** | Gửi kèm body `{ "completedAt": "2026-11-19T20:00:00Z" }`. Đổi trạng thái task sang `COMPLETED` trên SQL Server. |
| **Báo cáo Bất thường** | `/api/v1/tasks/{taskId}/flag-abnormal` | `PATCH` | **Có sẵn 100%** | Gửi kèm `{ "isAbnormalFlagged": true }`. Đánh dấu phát hiện bất thường cần điều dưỡng trưởng chú ý. |
| **Bệnh án Tóm tắt** | `/api/v1/ai/resident-chart-context/{id}` | `GET` | **Có sẵn 100%** | Trả về đơn thuốc eMAR thực tế, điểm ADL, tiền sử sự cố 90 ngày của cụ để đối chiếu tại buồng. |
| **Bóc tách Ghi chú** | `/api/v1/ai/voice/parse` | `POST` | **Có sẵn 100%** | Nhận `{ "text": "...", "residentId": 1 }`. Bóc tách văn bản thô thành cấu trúc: sinh hiệu, nội dung quan sát. |
| **Xác nhận Ghi chú** | `/api/v1/ai/voice/confirm` | `POST` | **Có sẵn 100%** | Lưu bản ghi quan sát vào `dbo.voice_care_observations` và gắn vào task ca trực (có người kiểm duyệt). |
| **Lọc Task Ca trực** | `/api/v1/tasks/my-shift` | `GET` | *Chưa có (Đề xuất)* | *Không bắt buộc cho MVP.* Có thể dùng ngay `/tasks/by-cna?cnaId={userId}` với `userId` lưu sau login. |
| **Đăng ký Device Token** | `/api/v1/notifications/devices` | `POST` | *Chưa có (Xây mới P2)* | Dành cho Phase 2 khi tích hợp Firebase Cloud Messaging (FCM). |

---

## 3. RANH GIỚI AN TOÀN LÂM SÀNG & VAI TRÒ AI (CLINICAL SAFETY & AI BOUNDARIES)

### 3.1. Dữ liệu y tế khách quan vs Suy diễn lâm sàng
Một trong những lỗi nghiêm trọng nhất trong chuyển đổi số y tế là **AI tự ý suy diễn hành động điều trị mà không có căn cứ**:

> [!CAUTION]
> **Ranh giới an toàn bắt buộc:**
> * **Không suy diễn việc dùng thuốc:** Việc bệnh nhân có đơn thuốc `Diazepam 5mg - ACTIVE` trong eMAR chỉ có nghĩa là cụ *đang được bác sĩ kê đơn thuốc này*, tuyệt đối **KHÔNG ĐƯỢC SUY DIỄN** là *"Cụ vừa uống thuốc ngủ cách đây ít phút"* trừ khi có bản ghi phát thuốc điện tử (eMAR Administration Log) xác nhận cụ thể về mặt thời gian.
> * **Hiển thị trung thực nguồn và mốc thời gian:** Giao diện Android phải ghi rõ:
>   * `Đơn thuốc trong phác đồ điều trị: Diazepam 5mg (Kê lúc 09:00, 15/10/2026)`
>   * `Phân loại rủi ro: Thuốc an thần có thể gây buồn ngủ / mất thăng bằng (FRID)`
> * **Không đưa ra y lệnh vượt quá kế hoạch chăm sóc:** Các hành động nhắc nhở (hạ thành giường, kiểm tra chuông gọi, trang bị tất chống trượt) phải dẫn xuất từ **Kế hoạch Chăm sóc Cá nhân hóa (Care Plan)** đã được Hội đồng Chuyên môn cơ sở phê duyệt, không phải do mô hình ngôn ngữ tự ý bịa đặt.

---

### 3.2. Định nghĩa Đúng đắn về "Humanizer" trong Giao diện

* **Điều Humanizer KHÔNG PHẢI LÀ:**
  * Không phải là hàm thay thế chuỗi tự động (`string.replace("nó", "cụ")`). Việc thay thế chuỗi tùy tiện trên văn bản y khoa có thể làm sai lệch đại từ nhân xưng, thay đổi ngữ pháp và làm biến dạng hồ sơ bệnh án.
  * Không phải là một công cụ AI tự động can thiệp hay sửa chữa ghi chép gốc của nhân viên y tế.
* **Điều Humanizer THỰC SỰ LÀ:**
  * **Nguyên tắc Viết nội dung Giao diện (Clinical UI Copywriting):** Thiết kế các thông điệp thông báo, lời nhắc nhở trên app bằng văn phong y tế nhã nhặn, ân cần, tôn trọng phẩm giá người cao tuổi:
    * *Tránh câu thô cứng:* `"Nhiệm vụ 802: Kiểm tra đối tượng nguy cơ ngã cao"`
    * *Thay bằng giao diện thân thiện:* `"Buồng 101 · Cụ Robert Anderson — Kiểm tra chuông gọi y tá trong tầm tay của cụ"`
  * **Bản nháp gợi ý (Drafting Assistance):** Khi điều dưỡng ghi âm lời dặn dò, hệ thống chỉ hỗ trợ sắp xếp các câu từ thành bản nháp chuẩn y khoa để **nhân viên tự đọc lại, chỉnh sửa và tự tay bấm xác nhận**.

---

### 3.3. Quy trình Ghi chú Giọng nói có Người duyệt (Human-in-the-Loop Voice Observation)

Tuyệt đối không để âm thanh tự động chuyển thành văn bản rồi tự ý ghi vào hồ sơ bệnh án hoặc biên bản bàn giao ca trực SBAR đã được phê duyệt. Quy trình trên Android phải tuân thủ nghiêm ngặt 4 bước an toàn:

```mermaid
sequenceDiagram
    autonumber
    actor Staff as Điều dưỡng / CNA
    participant App as Android CareCompanion
    participant STT as On-Device Speech Recognizer
    participant Backend as Spring Boot API (/api/v1/ai/voice)
    participant DB as SQL Server (dbo.voice_care_observations)

    Staff->>App: Nhấn giữ nút Mic và nói quan sát lâm sàng
    App->>STT: Xử lý âm thanh trên thiết bị (Tiếng Việt cục bộ)
    STT-->>App: Trả về văn bản thô (Transcript)
    App->>Backend: Gửi text thô: POST /ai/voice/parse
    Backend-->>App: Trả về bản nháp cấu trúc (Parsed: Vitals, Task Suggestion, Note)
    Note over App,Staff: MÀN HÌNH XÁC NHẬN (HUMAN-IN-THE-LOOP)<br/>Điều dưỡng xem lại, sửa lỗi chính tả/số liệu nếu cần
    Staff->>App: Bấm "Xác nhận & Lưu ghi chú"
    App->>Backend: Gửi bản ghi đã duyệt: POST /ai/voice/confirm
    Backend->>DB: Ghi vào dbo.voice_care_observations (Trạng thái: Chờ giao ca)
    Backend-->>App: HTTP 200 OK (Xác nhận hoàn tất)
```

1. **Bảo mật âm thanh:** Sử dụng nhận dạng giọng nói cục bộ trên máy (`Android SpeechRecognizer` của hệ điều hành), không gửi file ghi âm giọng nói thô chứa thông tin bệnh nhân (PHI) lên các API đám mây bên thứ ba chưa được kiểm chứng.
2. **Kiểm duyệt bắt buộc:** Màn hình app bắt buộc phải dừng lại ở bước hiển thị Transcript và Dữ liệu bóc tách. Chỉ khi người dùng bấm nút **`Xác nhận`**, dữ liệu mới được đẩy lên server.

---

## 4. ĐẶC TẢ MVP CỐT LÕI CÓ THỂ KIỂM CHỨNG (VERIFIABLE MVP CORE LOOP)

Nhằm đảm bảo tiến độ triển khai khả thi và chứng minh được kết quả trước Ban Giám khảo, phạm vi MVP được giới hạn vào **một luồng hoạt động trực tuyến duy nhất (Online-First)**:

### 4.1. Luồng nghiệp vụ 5 bước chuẩn hóa
1. **Bước 1 (Đăng nhập):** Nhân viên mở app, nhập tài khoản ca trực (`rebecca.thompson@nhms-demo.local`). App gọi `POST /api/v1/auth/login`, nhận và lưu token phiên làm việc.
2. **Bước 2 (Tải danh sách việc ca trực):** App gọi `GET /api/v1/tasks/by-cna?cnaId={userId}` để tải danh sách các thẻ nhiệm vụ được phân công trong ngày.
3. **Bước 3 (Xem thông tin tại giường):** Nhân viên bước đến buồng bệnh, mở xem thẻ nhiệm vụ: Tên cụ, số phòng, số giường, nội dung can thiệp đã được điều dưỡng trưởng phê duyệt.
4. **Bước 4 (Bấm hoàn thành):** Nhân viên thực hiện can thiệp thực tế (ví dụ: hạ thấp giường cụ Anderson), sau đó chạm nút **`Hoàn thành`** trên app. App gửi ngay `PATCH /api/v1/tasks/{taskId}/completed` với thời điểm hiện tại.
5. **Bước 5 (Kiểm chứng hai chiều):** Mở màn hình Web Admin (`/admin/care-tasks` hoặc `/admin/care-tasks?tab=by-resident`). Nhiệm vụ của cụ Anderson trên Web lập tức chuyển sang trạng thái xanh `COMPLETED` mà không cần reload lại trang.

---

### 4.2. Đặc tả 2 Màn hình MVP (Jetpack Compose Material 3)

#### Màn hình 1: Danh sách Nhiệm vụ Ca trực (`ShiftTaskListScreen`)
* **Header:** Tên nhân viên, vai trò (`CNA`), tiến độ hoàn thành ca trực (Ví dụ: `5/8 Hoàn thành`).
* **Pull-to-Refresh:** Kéo xuống để tải lại danh sách task mới nhất từ máy chủ.
* **Thẻ nhiệm vụ (`TaskCardItem`):**
  * Tên cụ: `Cụ Robert Anderson` (Font Semi-bold 16sp).
  * Vị trí: `Phòng 101 · Giường A`.
  * Nội dung: `Kiểm tra vị trí chuông gọi y tá luôn nằm trong tầm với của cư dân`.
  * Trạng thái: Badge màu vàng nhạt `PENDING` hoặc xanh lục `COMPLETED`.
  * Nút bấm lớn ($56 \times 56\text{ dp}$): Nút hoàn thành hình tròn có icon check, bấm vào phát rung nhẹ (`HapticFeedbackType.LongPress`) và đổi màu thẻ.

#### Màn hình 2: Chi tiết Cư dân Tối thiểu (`BedsideResidentSummarySheet`)
* Mở ra dưới dạng Modal Bottom Sheet khi bấm vào tên cụ.
* Hiển thị thông tin cơ bản: Ngày sinh/tuổi, giới tính, cấp độ chăm sóc (LOC Tier).
* Gọi `GET /api/v1/ai/resident-chart-context/{residentId}` để hiển thị danh sách thuốc đang dùng (chỉ đọc) và lưu ý vận động đã được phê duyệt.

---

### 4.3. UI/UX Công thái học buồng bệnh (Clinical Ergonomics)

* **Vùng tương tác ngón cái (Thumb-Driven Layout):** Nút hành động chính luôn nằm trong phạm vi $40\%$ chiều cao phía dưới màn hình, giúp thao tác một tay dễ dàng khi tay còn lại đang hỗ trợ người bệnh.
* **Mục tiêu chạm an toàn ($\ge 56\text{ dp}$):** Tránh hiện tượng bấm trượt khi ngón tay điều dưỡng đang đeo găng cao su y tế.
* **Phản hồi xúc giác (Haptic Confirmation):** Khi bấm hoàn thành task, điện thoại rung xác nhận để nhân viên biết lệnh đã thực thi mà không cần nhìn chăm chú vào màn hình.
* **Độ tương phản cao:** Tỷ lệ tương phản chữ/nền tối thiểu $4.5:1$ theo chuẩn WCAG 2.1 AA, dễ đọc dưới ánh đèn huỳnh quang bệnh viện.

---

## 5. LỘ TRÌNH PHÁT TRIỂN PHÂN KỲ (PHASED ROADMAP)

Để đảm bảo không "hứa trước những gì chưa làm được", dự án được chia thành 4 giai đoạn cụ thể:

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                          LỘ TRÌNH TRIỂN KHAI THEO 4 GIAI ĐOẠN                          │
├─────────────┬─────────────────────────────────────────────────┬────────────────────────┤
│ Giai đoạn   │ Phạm vi & Trọng tâm Kỹ thuật                    │ Điều kiện Nghiệm thu   │
├─────────────┼─────────────────────────────────────────────────┼────────────────────────┤
│ Giai đoạn 1 │ • Luồng MVP Trực tuyến (Online-First)           │ Chạy được luồng 5 bước │
│ (MVP Cốt lõi)│ • Đăng nhập với Session Token hiện có           │ Web ↔ Android hoàn hảo,│
│             │ • Tải task & Hoàn thành task qua PATCH API      │ 0 lỗi mạng.            │
├─────────────┼─────────────────────────────────────────────────┼────────────────────────┤
│ Giai đoạn 2 │ • Thông báo đẩy qua Firebase Cloud Messaging    │ Đo được độ trễ P50/P95;│
│ (Thông báo) │ • Xây dựng API đăng ký Device Token             │ Xử lý nhận tin khi app │
│             │ • Cơ chế gửi lại khi mất kết nối tạm thời       │ đang đóng/mở.          │
├─────────────┼─────────────────────────────────────────────────┼────────────────────────┤
│ Giai đoạn 3 │ • Nhận dạng giọng nói tiếng Việt on-device      │ Hoàn thành luồng       │
│ (Voice SBAR)│ • Tích hợp API /ai/voice/parse & confirm        │ Parse → Review →       │
│             │ • Màn hình kiểm duyệt bản nháp trước khi lưu    │ Confirm an toàn.       │
├─────────────┼─────────────────────────────────────────────────┼────────────────────────┤
│ Giai đoạn 4 │ • Bộ nhớ đệm cục bộ Room DB (Mã hóa SQLCipher)  │ Kiểm thử thành công    │
│ (Offline-Sec)│• Quản lý xung đột dữ liệu đa người dùng        │ chính sách xóa dữ liệu │
│             │ • Chính sách tự hủy dữ liệu bệnh án khi Logout  │ và chống gửi lặp task. │
└─────────────┴─────────────────────────────────────────────────┴────────────────────────┘
```

---

## 6. KỊCH BẢN THỬ NGHIỆM & KIỂM CHỨNG TẠI CHỖ (E2E VERIFICATION SCRIPT)

Kịch bản kiểm chứng thực tế dành cho đội ngũ phát triển và Ban Giám khảo:

1. **Chuẩn bị môi trường:**
   * Backend Spring Boot chạy tại `localhost:8080`.
   * Web Frontend chạy tại `localhost:5173`.
   * Thiết bị Android thật (kết nối qua USB/Wi-Fi cùng dải IP mạng nội bộ với máy tính) hoặc Android Emulator chạy Android 14 (API 34).
2. **Kịch bản thực thi:**
   * **Bước 1:** Trên Web, đăng nhập tài khoản Điều dưỡng trưởng, vào trang `/admin/care-tasks?tab=by-cna`. Chọn CNA Maria Santos, xác nhận có 1 nhiệm vụ đang ở trạng thái `PENDING`: *"Hạ giường nấc thấp nhất cho cụ Anderson"*.
   * **Bước 2:** Trên ứng dụng Android, đăng nhập bằng tài khoản của Maria Santos. Danh sách nhiệm vụ hiện ra đúng thẻ của cụ Anderson.
   * **Bước 3:** Trên Android, chạm nút hoàn thành. Ứng dụng hiển thị icon tích xanh và phát rung xác nhận.
   * **Bước 4:** Quay lại màn hình Web, quan sát thẻ nhiệm vụ: Trạng thái đã được cập nhật thành công sang `COMPLETED`, hiển thị mốc thời gian hoàn thành chính xác.

---

## 7. MỤC TIÊU NGHIÊN CỨU & PHƯƠNG PHÁP ĐO LƯỜNG (RESEARCH HYPOTHESES & KPIS)

> [!NOTE]
> Các chỉ số dưới đây là **Mục tiêu Nghiên cứu & Giả thuyết Thử nghiệm (Test Hypotheses)** được thiết lập cho đợt thử nghiệm thực địa, **KHÔNG PHẢI LÀ KẾT QUẢ ĐÃ CHỨNG MINH TRƯỚC**.

| Chỉ số Đo lường (KPI) | Giả thuyết Mục tiêu | Phương pháp Thu thập & Đo lường |
| :--- | :--- | :--- |
| **Độ trễ cập nhật trạng thái nhiệm vụ** | Đạt thời gian phản hồi $P50 < 1.5\text{ giây}$ và $P95 < 3.0\text{ giây}$ trong mạng nội bộ. | Đo thời gian từ lúc client gửi request `PATCH /tasks/{id}/completed` đến khi nhận được response 200 OK (lấy mẫu $N = 50$ lần gọi). |
| **Thời gian thao tác xác nhận tại giường** | Rút ngắn thời gian xác nhận một nhiệm vụ xuống $< 10\text{ giây}$ cho mỗi lượt chăm sóc. | Bấm giờ quan sát thực nghiệm (Time-motion study) so sánh giữa việc ghi chép sổ tay và thao tác 1-chạm trên điện thoại. |
| **Độ chính xác của Bản nháp Giọng nói** | Đạt tỷ lệ nhận diện đúng thuật ngữ điều dưỡng thông dụng $\ge 85\%$ trước khi nhân viên chỉnh sửa. | Đối chiếu giữa transcript tự động và bản ghi âm thực tế trên tập kiểm thử 30 mẫu câu quan sát buồng bệnh. |
| **Tỷ lệ xung đột dữ liệu nhiệm vụ** | Duy trì tỷ lệ xung đột trạng thái nhiệm vụ $< 1\%$ trong ca trực thông thường. | Kiểm tra nhật ký máy chủ về các trường hợp 2 nhân viên cùng cập nhật 1 task tại cùng một thời điểm. |

---

> [!TIP]
> **Kết luận:** Bản đặc tả này đã loại bỏ hoàn toàn các giả định thiếu căn cứ, bám sát $100\%$ hiện trạng mã nguồn hệ thống NHMS, thiết lập ranh giới an toàn y tế rõ ràng và sẵn sàng trở thành tài liệu kỹ thuật chuẩn mực để đội ngũ bắt tay triển khai thực tế.
