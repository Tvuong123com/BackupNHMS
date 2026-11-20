# Kế hoạch nâng cấp AI cho ứng dụng Android CareCompanion

Tài liệu này xác định các tính năng AI cần đưa vào ứng dụng Android CareCompanion, dựa trên ba nguyên tắc: **Humanizer** (ngôn ngữ tự nhiên, lược bỏ từ ngữ sáo rỗng), **UI/UX** (công thái học điều dưỡng, thao tác tối giản tại giường) và **Android** (kỹ thuật Jetpack Compose, khai thác phần cứng gốc).

---

## 1. Ba trụ cột phát triển

### Trụ cột 1: Humanizer (Chuẩn hóa ngôn ngữ và hội thoại)
* Loại bỏ toàn bộ từ ngữ quảng cáo và sáo rỗng thường gặp của AI ("đột phá", "hệ sinh thái", "vượt trội", "không chỉ... mà còn...").
* Lược bỏ emoji trang trí không cần thiết trên giao diện nghiệp vụ.
* Câu trả lời của chatbot và văn bản hướng dẫn dùng đúng thuật ngữ điều dưỡng thực tế: ngắn gọn, đi thẳng vào hành động xử lý, có trích dẫn nguồn phác đồ (CDC STEADI, Beers 2023, thang đo Morse, Braden).

### Trụ cột 2: UI/UX (Công thái học điều dưỡng tại giường)
* **Kích thước vùng chạm lớn:** Toàn bộ nút thao tác chính (ghi âm, xác nhận, cắm cờ cảnh báo) có chiều cao tối thiểu 52 dp, khoảng cách mép tối thiểu 16 dp để điều dưỡng bấm chính xác khi đang đeo găng tay cao su y tế.
* **Quy tắc một tay (One-handed thumb zone):** Đặt nút micro và nút lưu ở nửa dưới màn hình để điều dưỡng cầm máy một tay, tay còn lại hỗ trợ cư dân.
* **Quy trình tối giản số bước (1-Tap Confirmation):** Sau khi ghi âm, hệ thống tự điền dữ liệu vào form xem nhanh. Điều dưỡng kiểm tra mắt trong 3 giây và bấm một nút để lưu vào hệ thống, không phải qua các bước xác nhận rườm rà.

### Trụ cột 3: Android (Kỹ thuật và kiến trúc gốc)
* **Nhận diện giọng nói On-Device:** Dùng trực tiếp `android.speech.SpeechRecognizer` tích hợp sẵn trong hệ điều hành Android. Hoạt động nhận diện tiếng Việt trực tiếp trên thiết bị, không cần đăng ký API Key trả phí của bên thứ ba.
* **Xử lý bất đồng bộ:** Tận dụng Kotlin Coroutines và `StateFlow` để lắng nghe trạng thái micro, cập nhật giao diện theo thời gian thực không gây giật lag.
* **Phân định kiến trúc:** Thiết bị di động chỉ thu âm và hiển thị; việc xử lý NLP phức tạp được gửi về Backend Spring Boot (`/api/v1/ai/voice/parse`) để giảm tải pin và nhiệt độ cho điện thoại.

---

## 2. Kiến trúc AI giữa Android và Backend

```
Thiết bị Android (Client)
└── SpeechRecognizer (Gốc Android, nhận diện giọng nói tiếng Việt)
    └── Gửi văn bản thô qua HTTP
        │
        ▼
Máy chủ Spring Boot (Backend)
└── /api/v1/ai/voice/parse & /chat
    ├── Lựa chọn 1: Google Gemini API (Nếu cấu hình API Key trên Web)
    ├── Lựa chọn 2: Ollama Qwen cục bộ (Chạy offline trên máy tính, bảo mật nội bộ)
    └── Dự phòng khẩn cấp: Bộ lọc Regex y tế (Tự động nhận diện Huyết áp, Mạch, % ăn)
```

Điều dưỡng tại buồng bệnh không cần quan tâm máy chủ dùng Gemini hay Ollama. Khi điều dưỡng nói xong, màn hình sẽ hiển thị kết quả phân tích tương ứng.

---

## 3. Các tính năng AI cụ thể trên Android

### Tính năng 1: Trợ lý ghi chú giọng nói tại giường (Voice-to-Care)
* **Mục đích:** Giúp hộ lý và điều dưỡng ghi nhận nhanh thông tin chăm sóc mà không cần gõ bàn phím.
* **Cách hoạt động:**
  1. Điều dưỡng nhấn giữ nút micro tại màn hình ca trực và đọc:  
     *"Cụ Robert Anderson phòng 101 ăn được 80% cháo, huyết áp 135 trên 85, cụ than hơi chóng mặt."*
  2. Android `SpeechRecognizer` chuyển giọng nói thành chuỗi văn bản.
  3. App gửi chuỗi này về `POST /api/v1/ai/voice/parse`.
  4. Backend trả về dữ liệu có cấu trúc: Tên cư dân (Robert Anderson), Huyết áp (135/85), Khẩu phần ăn (80%), Triệu chứng (chóng mặt), Cờ bất thường (true).
  5. Màn hình hiện bảng xem lại với các ô đã điền sẵn. Điều dưỡng bấm "Lưu vào bệnh án" để hoàn tất.

### Tính năng 2: Chatbot hỗ trợ phác đồ điều dưỡng thật
* **Mục đích:** Thay thế đoạn mã so khớp từ khóa if/else cũ bằng AI suy luận thật.
* **Cách hoạt động:**
  1. Khi điều dưỡng mở hộp thoại hỏi đáp và nhập câu hỏi (ví dụ: *"Cụ vừa ngã trong nhà vệ sinh, cần làm gì trước?"*).
  2. App gọi `POST /api/v1/ai/chat`.
  3. Backend dùng cơ chế RAG tra cứu tài liệu CDC STEADI và trả về hướng dẫn xử lý 4 bước: kiểm tra tri giác, không di chuyển vội, đo sinh hiệu, gọi bác sĩ trực.
  4. Giao diện hiển thị tin nhắn kèm chỉ dẫn nguồn tham chiếu y khoa.

### Tính năng 3: Rà soát thuốc tăng nguy cơ té ngã (FRID)
* **Mục đích:** Cảnh báo trước khi điều dưỡng phát các loại thuốc có tác dụng phụ làm cho người già dễ té ngã.
* **Cách hoạt động:**
  1. Tại màn hình thuốc của cư dân, thêm nút "Rà soát an toàn thuốc".
  2. App gửi danh sách thuốc về `POST /api/v1/ai/medication/check`.
  3. Hệ thống đối chiếu tiêu chuẩn Beers 2023, phát hiện các thuốc an thần (như Lorazepam) hoặc hạ áp (như Amlodipine).
  4. App hiển thị bảng nhắc nhở: cần hỗ trợ cụ đứng dậy trong vòng 2 giờ sau uống thuốc.

### Tính năng 4: Tự động tổng hợp biên bản giao ban ca SBAR
* **Mục đích:** Giảm thời gian chép sổ giao ban cuối ca trực từ 30 phút xuống 1 phút.
* **Cách hoạt động:**
  1. Cuối ca, điều dưỡng bấm "Tổng hợp bàn giao ca".
  2. App gọi `POST /api/v1/ai/summary/generate`.
  3. Hệ thống tổng hợp các công việc đã làm trong ngày, các trường hợp huyết áp cao và sự cố phát sinh.
  4. Tự động điền vào 4 mục: Tình huống (Situation), Bệnh sử (Background), Đánh giá (Assessment), Khuyến nghị ca sau (Recommendation).
  5. Điều dưỡng xem lại và bấm ký xác nhận bàn giao.

---

## 4. Kế hoạch triển khai mã nguồn

| Hạng mục | Công việc cụ thể | File liên quan |
| :--- | :--- | :--- |
| **Ghi chú giọng nói** | Viết `VoiceCareBottomSheet.kt`, tích hợp `SpeechRecognizer`, gửi dữ liệu đến `/api/v1/ai/voice/parse`. | `VoiceCareBottomSheet.kt`<br>`CareRepository.kt` |
| **Kết nối Chatbot thật** | Bỏ logic `when/contains` cũ trong `AiChatbotDialog.kt`, chuyển sang gọi `repository.sendAiChat()`. | `AiChatbotDialog.kt`<br>`ApiService.kt` |
| **Cảnh báo thuốc FRID** | Bổ sung nút gọi `/api/v1/ai/medication/check` trong màn hình thuốc, hiển thị cảnh báo tương tác. | `MedicationsListScreen.kt` |
| **Tổng hợp giao ban SBAR** | Kết nối nút tổng hợp với `/api/v1/ai/summary/generate`, tự động điền thẻ SBAR. | `ShiftHandoverScreen.kt` |

---

## 5. Quy trình kiểm thử

1. **Giao diện Compose:** Kiểm tra giao diện các hộp thoại mới, đảm bảo nút bấm to, chữ rõ ràng, không bị tràn viền khi xoay màn hình hoặc tăng kích thước chữ hệ thống.
2. **Kiểm thử máy ảo:** Mở máy ảo Pixel 8, chạy ứng dụng, thử nghiệm luồng ghi âm mẫu bằng tiếng Việt và kiểm tra kết quả lưu trong cơ sở dữ liệu SQL Server.
3. **Kiểm thử rớt mạng / tắt LLM:** Tắt kết nối internet hoặc tắt Ollama để kiểm chứng cơ chế tự động chuyển sang bộ lọc Regex y tế nội bộ, đảm bảo ứng dụng không bao giờ bị văng (crash) hoặc đứng hình.
