# TÀI LIỆU TOÀN DIỆN VỀ KIẾN TRÚC HỆ THỐNG VÀ CHỨC NĂNG NHMS
## (Nursing Home Management System - ElderCare Management Suite)

---

## 📌 MỤC LỤC
1. [Giới Thiệu Tổng Quan Hệ Thống](#1-giới-thiệu-tổng-quan-hệ-thống)
2. [Chi Tiết Các Chức Năng Của Hệ Thống Web](#2-chi-tiết-các-chức-năng-của-hệ-thống-web)
   - [2.1. Cổng Thông Tin Công Khai (Public Website)](#21-cổng-thông-tin-công-khai-public-website)
   - [2.2. Cổng Quản Trị Lâm Sàng (ElderCare Admin Portal)](#22-cổng-quản-trị-lâm-sàng-eldercare-admin-portal)
3. [Chi Tiết Các Chức Năng Của Ứng Dụng Di Động Android (CareCompanion)](#3-chi-tiết-các-chức-năng-của-ứng-dụng-di-động-android-carecompanion)
4. [Mối Liên Kết Hai Chiều Giữa Web Và App Android](#4-mối-liên-kết-hai-chiều-giữa-web-và-app-android)
5. [Quy Trình Triển Khai Docker Compose & AWS EC2 (Không bao gồm Database)](#5-quy-trình-triển-khai-docker-compose--aws-ec2)
   - [5.1. Sơ Đồ Kiến Trúc & Luồng Vận Hành](#51-sơ-đồ-kiến-trúc--luồng-vận-hành)
   - [5.2. Mã HTML Trực Quan Quy Trình Docker Compose & AWS](#52-mã-html-trực-quan-quy-trình-docker-compose--aws)

---

## 1. Giới Thiệu Tổng Quan Hệ Thống

**NHMS (Nursing Home Management System)** là giải pháp chuyển đổi số toàn diện cho viện dưỡng lão và cơ sở chăm sóc nội trú người cao tuổi. Hệ thống kết hợp chặt chẽ giữa:
* **Hệ thống Web điều hành tập trung (Web Portal):** Phục vụ ban lãnh đạo, bác sĩ phụ trách, điều dưỡng trưởng (DON/RN) trong việc theo dõi, thiết lập phác đồ, đánh giá rủi ro và điều phối ca trực.
* **Cổng thông tin gia đình (Public Website):** Cung cấp các công cụ tương tác AI giúp người nhà tìm hiểu dịch vụ, sàng lọc điều kiện nhập viện và theo dõi định kỳ.
* **Ứng dụng di động cận giường (Android CareCompanion App):** Trao quyền cho hộ lý (CNA) và điều dưỡng ca trực thực hiện công việc rảnh tay ngay tại giường bệnh với sự trợ lực của AI giọng nói.
* **Động cơ Trí tuệ nhân tạo Lâm sàng (Clinical AI Engine - Google Gemini):** Hỗ trợ quyết định y khoa (CDSS), nhận diện nguy cơ té ngã đa chiều (DFRS 2.0), rà soát an toàn dược lý eMAR và tự động hóa biên bản giao ban ca trực SBAR.

---

## 2. Chi Tiết Các Chức Năng Của Hệ Thống Web

### 2.1. Cổng Thông Tin Công Khai (Public Website)

#### A. Chức Năng Thường (Standard Web Features)
1. **Trang chủ & Giới thiệu cơ sở (`HomePage`, `AboutPage`):** Trình bày sứ mệnh, không gian sinh hoạt, tiêu chuẩn y tế, các chứng nhận chất lượng và hình ảnh thực tế của viện.
2. **Danh mục Dịch vụ Chăm sóc Chuyên sâu (`ServicesPages`):**
   * *Điều dưỡng chuyên biệt (Skilled Nursing):* Chăm sóc vết loét, tiêm truyền, phục hồi chức năng sau tai biến.
   * *Chăm sóc Sa sút trí tuệ (Memory Care):* Khu vực an toàn cho người cao tuổi suy giảm trí nhớ, Alzheimer.
   * *Vật lý trị liệu (Physical Therapy):* Phục hồi vận động cơ xương khớp.
   * *Trị liệu giải trí & Sinh hoạt cộng đồng (Recreational Therapy):* Hoạt động yoga dưỡng sinh, văn nghệ, kích thích não bộ.
   * *Hỗ trợ tâm lý gia đình (Family Support):* Tư vấn tài chính, bảo hiểm y tế và chăm sóc cuối đời.
3. **Cơ sở vật chất & Buồng phòng (`FacilitiesPage`):** Tra cứu tiện nghi buồng bệnh (giường y tế nâng hạ, thanh chắn an toàn, chuông cấp cứu, phòng đơn/đôi).
4. **Đội ngũ chuyên gia (`TeamPage`):** Giới thiệu trình độ chuyên môn của các bác sĩ chuyên khoa lão, điều dưỡng trưởng và kỹ thuật viên phục hồi.
5. **Đăng ký tiếp nhận & Liên hệ (`AdmissionsPage`, `ContactPage`):** Biểu mẫu gửi yêu cầu nhập viện, hẹn lịch tham quan trực tiếp.

#### B. Chức Năng AI Trên Website (Public AI Widgets)
* **W1: AI Family Concierge Chatbot (`website-chat-widget.tsx`):**  
  Trợ lý ảo thông minh trực tuyến 24/7, đóng vai trò chuyên viên tư vấn tiếp đón người nhà. Chatbot giải đáp cặn kẽ chi phí từng gói dịch vụ, chế độ dinh dưỡng, quy định thăm nom và hướng dẫn chuẩn bị hồ sơ bệnh án.
* **W2: AI Admission Eligibility Pre-Screener (`ai-eligibility-screener.tsx`):**  
  Bộ sàng lọc nhanh điều kiện tiếp nhận hồ sơ. Người nhà nhập độ tuổi, tình trạng đi lại, các bệnh lý nền và nhu cầu hỗ trợ (ăn uống, tắm giặt). AI sẽ đối chiếu với năng lực chuyên môn của viện để đưa ra khuyến nghị: *Đủ điều kiện tiếp nhận ngay*, *Cần bác sĩ hội chẩn trước* hoặc *Phù hợp với khu Memory Care*.
* **W3: AI FAQ Smart Assistant (`ai-faq-search.tsx`):**  
  Công cụ tìm kiếm ngữ nghĩa thông minh cho mục Hỏi - Đáp. Người nhà có thể hỏi bằng câu tự nhiên (ví dụ: *"Cụ có bảo hiểm y tế tuyến huyện thì chi trả thế nào?"*), AI sẽ tự động phân tích và trích xuất câu trả lời chính xác từ bộ quy chế của viện.
* **W4: AI Care Needs Assessment Quiz (`ai-care-quiz.tsx`):**  
  Bài trắc nghiệm tương tác giúp gia đình lượng hóa mức độ cần trợ giúp của người thân theo tiêu chuẩn ADL (Katz Index), từ đó tự động gợi ý gói dịch vụ phù hợp nhất.
* **W5: AI Family Digest Preview (`ai-family-digest-preview.tsx`):**  
  Tạo bản xem trước báo cáo sức khỏe mẫu của người cao tuổi trong tuần, giúp người nhà hình dung cách viện dưỡng lão cập nhật tình hình ăn ngủ, vận động và tâm trạng của cụ.
* **W6: AI Memory Care Risk Explainer (`ai-memory-risk-checker.tsx`):**  
  Công cụ đánh giá nguy cơ sa sút trí tuệ và đi lạc (Elopement Risk). Giải thích khoa học cho gia đình vì sao người cao tuổi có hành vi kích động khi trời chập tối (Sundowning) và các phương pháp chăm sóc không dùng thuốc.

---

### 2.2. Cổng Quản Trị Lâm Sàng (ElderCare Admin Portal)

#### A. Chức Năng Thường (Standard Admin & Clinical Features)
1. **Trung Tâm Chỉ Huy (Operations Dashboard):**
   * Hiển thị chỉ số thời gian thực: Tổng số cư dân nội trú, tỷ lệ lấp đầy giường bệnh, số lượng sự cố khẩn cấp đang mở, tỷ lệ hoàn thành nhiệm vụ ca trực trong ngày.
   * Bảng điều hướng nhanh tới các luồng nghiệp vụ quan trọng.
2. **Quy Trình Tiếp Nhận & Đánh Giá Lâm Sàng (Resident Intake & Assessment):**
   * *Pre-Admission Screening:* Đăng ký thông tin ban đầu, lưu trữ hồ sơ y bạ tuyến trước.
   * *Assessment Ledger:* Thực hiện bảng đánh giá lâm sàng toàn diện: tính điểm sinh hoạt hàng ngày (ADL Score), thang đo nguy cơ ngã Morse (MFS), phân loại mức độ chăm sóc (Level of Care - LOC) và buồng phòng chỉ định.
   * *Admission Ledger:* Quản lý nhập viện chính thức, phân bổ giường bệnh và thủ tục xuất viện (Discharge).
3. **Quản Lý Toàn Diện Hồ Sơ Cư Dân (Resident Charting):**
   * Quản lý nhân thân, số bảo hiểm, thông tin người giám hộ/liên hệ khẩn cấp.
   * Lịch sử bệnh án điện tử (EHR), tiền sử dị ứng, hồ sơ chuyển tầng/chuyển viện.
   * Bảng theo dõi dấu hiệu sinh tồn (Vital Signs): Biểu đồ huyết áp, nhịp tim, nhịp thở, nhiệt độ, chỉ số SpO2.
   * Sổ theo dõi thuốc điện tử (eMAR): Đơn thuốc điều trị, liều dùng, khung giờ uống, trạng thái đã cấp phát.
4. **Thiết Kế & Quản Lý Kế Hoạch Chăm Sóc (Care Plan Design):**
   * Xây dựng phác đồ cá nhân hóa gồm Mục tiêu chăm sóc (Care Goals) và Can thiệp điều dưỡng (Care Interventions).
   * Kiểm tra tuân thủ quy chuẩn y tế và đánh giá định kỳ sau 30/60/90 ngày.
5. **Trung Tâm Điều Phối Ca Trực (Care Tasks Center):**
   * Phân bổ nhiệm vụ theo Hộ lý (`By CNA`) hoặc theo Cư dân (`By Resident`).
   * Phân công đích danh nhân viên, đổi ca, gắn cờ theo dõi nhiệm vụ chậm tiến độ hoặc bất thường.
6. **Quản Trị Sự Cố & Cam Kết Phản Hồi (Incident & SLA Management):**
   * Ghi nhận và thụ lý các sự cố: Té ngã, Đi lạc, Xô xát, Bỏ bữa, Nhầm thuốc, Hỏng hóc thiết bị.
   * Thiết lập cấp độ nghiêm trọng (Low, Medium, High, Critical) và bộ đếm ngược SLA giải quyết sự cố.
7. **Quản Trị Hệ Thống & Nhân Sự (Facility & System Admin):**
   * Quản lý buồng phòng, giường bệnh theo khu nhà, tầng, phòng chức năng.
   * Quản lý người dùng và phân quyền RBAC đa cấp độ (Admin, DON, Nurse, CNA, Admission Staff).
   * Nhật ký kiểm toán an ninh (Audit Logs) ghi nhận mọi hành vi truy cập dữ liệu nhạy cảm.

#### B. Chức Năng AI Trên Web Quản Trị (Clinical Decision Support - CDSS)
* **A1: Dynamic Fall Risk Stratification (Động Cơ DFRS 2.0):**  
  Mô hình phân tầng rủi ro té ngã đa chiều thời gian thực (dựa trên nghiên cứu của Khademi et al., *PLOS ONE* và Lindemann et al., *Nature Sci Rep*). Động cơ liên tục tổng hợp:
  * Tần suất và mức độ sự cố ngã trong 90 ngày gần nhất (Incident Velocity).
  * Gánh nặng dược lý từ các nhóm thuốc làm tăng nguy cơ ngã (FRID - Fall-Risk-Increasing Drugs trong eMAR).
  * Điểm suy giảm khả năng tự vận động (ADL Mobility Subscore).
  * Dao động chỉ số sinh tồn (Huyết áp tư thế, nhịp tim).
  * Xuất ra điểm số DFRS (0 - 100) và xếp hạng rủi ro: **Cao (HIGH)**, **Vừa (MODERATE)**, **Thấp (LOW)**.
* **A2: AI Clinical Risk Review & 1-Click Intervention (`+ Assign to Shift`):**  
  Hiển thị danh sách cư dân ưu tiên can thiệp trong ca trực. Cho phép điều dưỡng trưởng nhấn **`+ Assign to Shift`** để chuyển đổi ngay lập tức một khuyến nghị AI thành một nhiệm vụ thực tế trong ca trực, đẩy thẳng xuống điện thoại của hộ lý buồng bệnh.
* **A3: AI Shift Handover Workflow (Giao Ban Ca Trực Tự Động Chuẩn SBAR):**  
  Hệ thống AI tự động quét toàn bộ nhật ký ca trực vừa kết thúc (nhiệm vụ đã làm, sự cố xảy ra, các ghi chú giọng nói tại giường) để xuất ra biên bản giao ban chuẩn y tế quốc tế **SBAR**:
  * *S - Situation:* Cụ nào sốt, cụ nào có nguy cơ ngã cao trong ca.
  * *B - Background:* Bệnh lý nền và phác đồ đang áp dụng.
  * *A - Assessment:* Đánh giá diễn biến sức khỏe trong ca trực.
  * *R - Recommendation:* Đề xuất hành động trọng tâm cho kíp trực ca sau.
* **A4: AI Care Plan Smart Suggestions:**  
  Dựa trên hồ sơ bệnh án và điểm đánh giá ADL, AI tự động soạn thảo bản nháp kế hoạch chăm sóc với các mục tiêu SMART và can thiệp cụ thể để điều dưỡng phê duyệt.
* **A5: AI Medication Safety & Beers Criteria Checker:**  
  Rà soát đơn thuốc eMAR, tự động gắn nhãn cảnh báo các loại thuốc an thần, chống trầm cảm, hạ huyết áp có nguy cơ gây lú lẫn, hạ huyết áp tư thế hoặc chóng mặt ở người cao tuổi.
* **A6: AI Elopement Risk Evaluation:**  
  Đánh giá nguy cơ người bệnh sa sút trí tuệ tự ý rời khỏi khuôn viên an toàn. Phân tích lịch sử kích động ban đêm để đề xuất bật cảm biến cửa, vòng đeo định vị RFID hoặc tăng tần suất thăm buồng.
* **A7: AI Incident Root-Cause Analysis:**  
  Tự động phân loại nguyên nhân gốc rễ của sự cố té ngã (do sàn trơn trượt, do cố tự với đồ vật, do tác dụng phụ của thuốc) và đề xuất can thiệp khắc phục ngăn ngừa tái diễn.
* **A8: AI Clinical Semantic Search & RAG:**  
  Tìm kiếm thông tin thông minh bằng ngôn ngữ tự nhiên trên toàn bộ cơ sở dữ liệu y bạ (ví dụ: *"Những cư dân nào từng có tiền sử dị ứng Penicillin hoặc từng ngã trong tháng 7?"*).
* **A9: AI Voice Care Input Trên Web:**  
  Hỗ trợ điều dưỡng trực tại quầy đọc ghi chú lâm sàng nhanh qua Micro máy tính để AI tự động điền vào biểu mẫu bệnh án.

---

## 3. Chi Tiết Các Chức Năng Của Ứng Dụng Di Động Android (CareCompanion)

### 3.1. Chức Năng Thường (Core Mobile Features)
1. **Xác thực phiên làm việc an toàn (Authentication & Session):**
   * Đăng nhập bảo mật, lưu mã Token phiên trực an toàn bằng `EncryptedSharedPreferences`.
   * Tự động nhận diện vai trò (CNA/Nurse) và nạp danh sách công việc của ca trực hiện tại.
2. **Quản lý Cư dân theo Buồng phòng (Assigned Residents & Rooms):**
   * Xem danh sách cư dân thuộc quyền quản lý của kíp trực theo từng số buồng, số giường.
   * Xem tóm tắt nhanh: Tên, tuổi, mức độ chăm sóc, người liên hệ khẩn cấp và các lưu ý đặc biệt.
3. **Màn hình Quản lý Nhiệm vụ Ca trực (Shift Tasks Screen):**
   * **Bộ lọc trực quan:** *Tất cả (All)*, *Cần làm (Pending)*, *Đã xong (Completed)*.
   * **Thao tác 1 chạm hoàn thành nhiệm vụ (`Complete ✓`):** Chạm nút hoàn thành ngay tại giường bệnh, tự động ghi nhận mốc thời gian hoàn tất gửi về máy chủ.
   * **Báo cáo bất thường tức thì (`Flag Abnormal 🚩`):** Gạt công tắc cảnh báo đỏ khi phát hiện cư dân có biểu hiện lạ (ví dụ: bỏ ăn, sốt cao, từ chối nằm giường cảm biến).
4. **Tra cứu Sinh hiệu & Lịch uống thuốc:**
   * Xem giờ uống thuốc trong ca của từng cư dân để chuẩn bị và nhắc nhở.
   * Xem lại các lần đo huyết áp, nhịp tim gần nhất.
5. **Giao diện Song ngữ & Tối ưu hóa Tiện dụng:**
   * Chuyển đổi linh hoạt giữa **Tiếng Việt** và **Tiếng Anh**.
   * Thiết kế nút bấm kích thước lớn, tương phản cao, thuận tiện thao tác khi đang đeo găng tay y tế.

### 3.2. Chức Năng AI Trên App Android (Bedside AI Capabilities)
* **AA1: Ghi Chú Giọng Nói Cận Giường (Bedside Voice Care Note):**  
  Tích hợp bộ nhận dạng giọng nói ngoại tuyến/trực tuyến của Android (`SpeechRecognizer`). Nhân viên chỉ cần nhấn giữ biểu tượng Micro và nói tự nhiên nội dung chăm sóc vừa thực hiện.
* **AA2: AI Bóc Tách Thực Thể Lâm Sàng (Clinical Entity Extraction):**  
  Chuỗi giọng nói được gửi tới AI Gemini để tự động nhận diện và trích xuất thành các trường dữ liệu y khoa chuẩn:
  * Huyết áp tâm thu / tâm trương (mmHg)
  * Nhịp tim (bpm)
  * Nhiệt độ cơ thể (°C)
  * Phần trăm lượng thức ăn tiêu thụ trong bữa (0 - 100%)
  * Trạng thái đã uống thuốc trong ca
  * Triệu chứng và ghi chú lâm sàng bất thường
* **AA3: Cơ Chế Duyệt An Toàn Cận Giường (Human-in-the-loop Bedside Confirmation):**  
  Ứng dụng mở bảng trượt (`VoiceCareBottomSheet`) hiển thị các chỉ số mà AI đã trích xuất. Hộ lý xem lại số liệu bằng mắt, có thể chỉnh sửa nếu cần và bấm **"Lưu vào bệnh án"** để cam kết trách nhiệm chuyên môn.
* **AA4: Tự Động Hoàn Thành Nhiệm Vụ Ca Trực Qua Giọng Nói:**  
  Nếu câu nói của hộ lý chứa nội dung hoàn thành một nhiệm vụ đang giao (ví dụ: *"Đã hỗ trợ ăn trưa cho cụ phòng 101"*), hệ thống AI tự động đối chiếu và đổi trạng thái của task đó sang `COMPLETED`.

---

## 4. Mối Liên Kết Hai Chiều Giữa Web Và App Android

Hệ thống NHMS vận hành như một thực thể thống nhất với vòng lặp phản hồi khép kín giữa **Trung tâm Chỉ huy Web** và **Tiền tuyến Cận giường Android**:

```
 ┌────────────────────────────────────────────────────────────────────────┐
 │                      WEB ADMIN (TRUNG TÂM CHỈ HUY)                     │
 │  • Động cơ DFRS 2.0 phân tích rủi ro ngã từ dữ liệu toàn viện          │
 │  • Điều dưỡng trưởng bấm "+ Assign to Shift" tạo nhiệm vụ can thiệp    │
 └───────────────────────────────────┬────────────────────────────────────┘
                                     │ (1) Giao việc xuống ca trực
                                     │     API: GET /api/v1/tasks/by-cna
                                     ▼
 ┌────────────────────────────────────────────────────────────────────────┐
 │                    APP ANDROID (TIỀN TUYẾN CẬN GIƯỜNG)                 │
 │  • Nhiệm vụ mới xuất hiện tại danh sách "Cần làm" của Hộ lý (CNA)      │
 │  • Hộ lý đến buồng bệnh thực hiện can thiệp (bật cảm biến, khóa bánh)   │
 │  • Hộ lý bấm Hoàn thành (✓) hoặc đọc Giọng nói AI (Voice Note)         │
 └───────────────────────────────────┬────────────────────────────────────┘
                                     │ (2) Báo cáo dữ liệu thực tế ngược lên
                                     │     API: PATCH /tasks/{id}/completed
                                     │     API: POST /ai/voice/confirm
                                     ▼
 ┌────────────────────────────────────────────────────────────────────────┐
 │                     ĐỒNG BỘ THỜI GIAN THỰC LÊN WEB                     │
 │  • Trạng thái Task trên Web Dashboard lập tức chuyển sang "COMPLETED"  │
 │  • Chỉ số Huyết áp, Nhịp tim cập nhật ngay vào Biểu đồ Vital Signs     │
 │  • Dữ liệu giọng nói tự động nạp vào Biên bản Giao ban Ca trực (SBAR)  │
 └────────────────────────────────────────────────────────────────────────┘
```

### Bảng Đối Chiếu Mối Liên Kết Nghiệp Vụ:

| Hoạt Động Trên Web Admin | ➔ | Tác Động Tương Ứng Trên App Android |
| :--- | :---: | :--- |
| Điều dưỡng bấm `+ Assign to Shift` trong phần AI Review | ➔ | Xuất hiện thẻ nhiệm vụ `FALL_PREVENTION` mới trong danh sách ca trực của hộ lý trên điện thoại. |
| Bác sĩ kê đơn thuốc mới trong mục eMAR | ➔ | Danh mục thuốc của cư dân trên app cập nhật giờ uống và cảnh báo lưu ý. |
| Điều dưỡng gắn cờ theo dõi đặc biệt cho cụ (ví dụ: nguy cơ đi lạc) | ➔ | Biểu tượng cảnh báo an toàn màu đỏ xuất hiện nổi bật cạnh tên cụ trên app. |

| Hoạt Động Trên App Android | ➔ | Tác Động Tương Ứng Trên Web Admin |
| :--- | :---: | :--- |
| Hộ lý bấm nút Hoàn thành (`Complete ✓`) trên điện thoại | ➔ | Thẻ nhiệm vụ trên Web chuyển xanh (`COMPLETED`), cập nhật tiến độ KPI ca trực. |
| Hộ lý gạt công tắc Báo cáo bất thường (`Flag Abnormal`) | ➔ | Cảnh báo khẩn cấp nhảy lên màn hình Dashboard của Điều dưỡng trưởng. |
| Hộ lý đọc chỉ số sinh tồn qua Micro trên Android | ➔ | Bản ghi xuất hiện trong bảng `vital_signs` và biểu đồ sinh tồn trên Web. |
| Hộ lý lưu các ghi chú quan sát buồng bệnh trong suốt ca | ➔ | AI gom toàn bộ ghi chú để xuất biên bản bàn giao ca trực chuẩn SBAR trên Web. |

---

## 5. Quy Trình Triển Khai Docker Compose & AWS EC2
*(Lưu ý: Phần này trình bày chuyên biệt về Kiến trúc Triển khai Phần mềm, Đóng gói Container, Định tuyến Mạng và Máy chủ Đám mây AWS — **không bao gồm Database** theo đúng yêu cầu)*.

### 5.1. Sơ Đồ Kiến Trúc & Luồng Vận Hành

```
[Local Machine: Windows PC]
  ├── Mã nguồn Frontend (React Vite + Tailwind)
  ├── Mã nguồn Backend (Spring Boot 3 + Java 17 + Gemini AI SDK)
  ├── Cấu hình Nginx Reverse Proxy (nginx.conf)
  └── Đóng gói: tar -czvf nhms-app.tar.gz
           │
           │  Truyền bảo mật: scp -i nhms-key.pem nhms-app.tar.gz ubuntu@3.27.120.176:~/
           ▼
[AWS Cloud: EC2 Instance (c7i-flex.large, Ubuntu 26.04 LTS)]
  │
  ├── Tường lửa AWS Security Group: Mở Port 80 (HTTP) và Port 22 (SSH)
  │
  └── DOCKER COMPOSE ORCHESTRATION (Mạng nội bộ Bridge Network):
        │
        ├── [Container 1: nhms-frontend] (Port ngoài 80 -> Port trong 80)
        │     ├── Máy chủ Web Nginx Alpine phục vụ bộ bundle React SPA
        │     └── REVERSE PROXY NỘI BỘ:
        │           • /api/ ──► chuyển tiếp tới http://backend:8080/api/
        │           • /auth/ ─► chuyển tiếp tới http://backend:8080/auth/
        │           • / ──────► phục vụ file tĩnh index.html
        │
        └── [Container 2: nhms-backend] (Port nội bộ 8080)
              ├── OpenJDK 17 Runtime thực thi Spring Boot (app.jar)
              ├── Xử lý toàn bộ logic nghiệp vụ xác thực, cư dân, ca trực
              └── Kết nối Cloud API ra bên ngoài: Google AI Studio (Gemini Flash)
```

---

### 5.2. Mã HTML Trực Quan Quy Trình Docker Compose & AWS

Dưới đây là toàn bộ mã nguồn HTML tự chứa (Self-contained) có thể mở trực tiếp trên mọi trình duyệt để xem sơ đồ tương tác 5 bước triển khai:

```html
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Quy Trình Triển Khai Docker Compose & AWS</title>
  <script src="https://www.gstatic.com/antigravity/web/dev/tailwindcss.min.js"></script>
  <style>
    body { background-color: #0f172a; color: #f8fafc; font-family: system-ui, sans-serif; }
    .flow-card { background: #1e293b; border: 1px solid #334155; transition: all 0.25s ease; }
    .flow-card:hover { border-color: #60a5fa; transform: translateY(-2px); }
  </style>
</head>
<body class="p-6">
  <div class="max-w-6xl mx-auto space-y-6">
    <div class="border-b border-slate-700 pb-4">
      <span class="px-3 py-1 rounded-full text-xs font-semibold bg-blue-500/10 text-blue-400 border border-blue-500/20">
        ☁️ Cloud DevOps Architecture
      </span>
      <h1 class="text-2xl font-bold text-white mt-2">Quy Trình Triển Khai Docker Compose & AWS EC2</h1>
      <p class="text-sm text-slate-400">Máy chủ mục tiêu: AWS EC2 (3.27.120.176) - Hệ thống NHMS</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <!-- Bước 1 -->
      <div class="flow-card p-5 rounded-xl">
        <span class="text-xs font-bold px-2.5 py-1 rounded bg-blue-600 text-white">BƯỚC 1</span>
        <h3 class="text-base font-bold text-white mt-3">💻 Đóng Gói Tại Máy Cá Nhân</h3>
        <p class="text-xs text-slate-300 mt-2">Nén mã nguồn loại trừ dependencies nặng:</p>
        <code class="block bg-slate-900 p-2.5 rounded text-[11px] text-emerald-400 mt-2 font-mono">
          tar -czvf nhms-app.tar.gz --exclude=node_modules --exclude=target .
        </code>
      </div>

      <!-- Bước 2 -->
      <div class="flow-card p-5 rounded-xl">
        <span class="text-xs font-bold px-2.5 py-1 rounded bg-indigo-600 text-white">BƯỚC 2</span>
        <h3 class="text-base font-bold text-white mt-3">🔐 Truyền File Bảo Mật (SCP)</h3>
        <p class="text-xs text-slate-300 mt-2">Truyền qua kênh mã hóa SSH RSA-2048:</p>
        <code class="block bg-slate-900 p-2.5 rounded text-[11px] text-indigo-300 mt-2 font-mono break-all">
          scp -i nhms-key.pem nhms-app.tar.gz ubuntu@3.27.120.176:~/
        </code>
      </div>

      <!-- Bước 3 -->
      <div class="flow-card p-5 rounded-xl">
        <span class="text-xs font-bold px-2.5 py-1 rounded bg-purple-600 text-white">BƯỚC 3</span>
        <h3 class="text-base font-bold text-white mt-3">☁️ Hạ Tầng Máy Chủ AWS EC2</h3>
        <p class="text-xs text-slate-300 mt-2">Cấu hình máy chủ và quy tắc tường lửa:</p>
        <ul class="text-xs text-slate-300 mt-2 space-y-1">
          <li>• Instance: c7i-flex.large (Ubuntu 26.04 LTS)</li>
          <li>• Mở cổng: Port 80 (HTTP) và Port 22 (SSH)</li>
          <li>• Cài đặt: Docker Engine v29 & Compose v5.8</li>
        </ul>
      </div>
    </div>

    <!-- Bước 4 & 5 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div class="flow-card p-5 rounded-xl">
        <span class="text-xs font-bold px-2.5 py-1 rounded bg-sky-600 text-white">BƯỚC 4</span>
        <h3 class="text-base font-bold text-white mt-3">🐳 Điều Phối Container (Docker Compose)</h3>
        <p class="text-xs text-slate-300 mt-2">Chạy <code class="text-sky-300">docker compose up -d --build</code> gồm 2 dịch vụ:</p>
        <div class="mt-3 space-y-2 text-xs">
          <div class="bg-slate-900 p-3 rounded-lg border border-slate-700">
            <b class="text-sky-400">1. nhms-frontend (Cổng 80):</b> Nginx Alpine phục vụ giao diện React SPA và Reverse Proxy mọi request <code class="text-amber-300">/api/</code> sang <code class="text-emerald-300">backend:8080</code>.
          </div>
          <div class="bg-slate-900 p-3 rounded-lg border border-slate-700">
            <b class="text-emerald-400">2. nhms-backend (Cổng 8080):</b> Ứng dụng Spring Boot 3 xử lý nghiệp vụ, kết nối Google Gemini AI.
          </div>
        </div>
      </div>

      <div class="flow-card p-5 rounded-xl">
        <span class="text-xs font-bold px-2.5 py-1 rounded bg-emerald-600 text-white">BƯỚC 5</span>
        <h3 class="text-base font-bold text-white mt-3">🚀 Khách Hàng Truy Cập Sử Dụng</h3>
        <p class="text-xs text-slate-300 mt-2">Điểm tiếp nhận đồng nhất qua Cổng 80:</p>
        <div class="mt-3 space-y-2 text-xs">
          <div class="bg-slate-900 p-3 rounded-lg">
            <b class="text-blue-400 block">🖥️ Trình duyệt Web (Admin & Public):</b>
            <span class="font-mono text-slate-300">http://3.27.120.176/</span>
          </div>
          <div class="bg-slate-900 p-3 rounded-lg">
            <b class="text-emerald-400 block">📱 Ứng dụng Di động Android (CareCompanion):</b>
            <span class="font-mono text-slate-300">http://3.27.120.176/api/v1/</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
```

---
*Tài liệu được biên soạn phục vụ công tác kỹ thuật, bàn giao và vận hành hệ thống NHMS.*
