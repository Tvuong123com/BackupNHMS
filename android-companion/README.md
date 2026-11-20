# NHMS CareCompanion - Trợ Lý Chăm Sóc Đầu Giường Lão Khoa (Point-of-Care Android App)

Ứng dụng di động chuyên biệt dành cho **Điều dưỡng viên & Hộ lý (CNA - Certified Nursing Assistant)** tại Viện dưỡng lão thông minh NHMS.

---

## 1. Giới thiệu & Mục tiêu
Tại các cơ sở chăm sóc lão khoa, điều dưỡng viên (CNA) liên tục di chuyển giữa các phòng bệnh. Trước đây, quy trình ghi nhận thường bị trễ từ 30–60 phút do phải quay lại máy tính tại quầy điều dưỡng (Nurse Station) hoặc ghi chép vào sổ tay rồi nhập lại.

**NHMS CareCompanion** mang đến giải pháp **Đầu giường (Point-of-Care)**:
- Thao tác 1 tay (One-handed Thumb Zone).
- Nút bấm lớn $\ge 52\text{ dp}$, tối ưu hóa cho điều dưỡng đang đeo găng tay y tế.
- Gắn cờ ghi nhận bất thường ngay khi phát hiện triệu chứng lâm sàng của người cao tuổi.
- Đồng bộ tức thì với cổng thông tin Quản trị Web React (`/admin/care-tasks`) qua REST API Spring Boot hiện có.

---

## 2. Kiến trúc & Công nghệ
- **Ngôn ngữ**: Kotlin (JVM Toolchain 17)
- **UI Toolkit**: Jetpack Compose & Material Design 3 (M3)
- **Kiến trúc**: MVVM (Model-View-ViewModel), Unidirectional Data Flow (UDF)
- **Bất đồng bộ**: Kotlin Coroutines & `StateFlow`
- **Mạng**: Retrofit 2.11.0 + OkHttp 3 / OkHttp 4 (với `AuthInterceptor` & Logging)
- **Serialization**: GSON 2.11.0
- **Phản hồi xúc giác**: Haptic Feedback khi xác nhận chăm sóc người bệnh

---

## 3. Bản đồ API kết nối trực tiếp với Backend Spring Boot
Ứng dụng tương thích 100% với các endpoint backend hiện hành:

| Chức năng | Phương thức & Tuyến đường | Mô tả |
| :--- | :--- | :--- |
| **Đăng nhập** | `POST /api/v1/auth/login` | Nhận `token`, `sessionId`, `role`, `name` |
| **Lấy hồ sơ cá nhân** | `GET /api/v1/auth/me` | Lấy `id`, `email`, họ tên đầy đủ của nhân viên |
| **Danh sách ca trực** | `GET /api/v1/tasks/by-cna?assignedCnaId={id}` | Lấy danh sách đầu việc chăm sóc được phân công theo ca |
| **Xác nhận hoàn thành** | `PATCH /api/v1/tasks/{taskId}/completed` | Cập nhật thời gian hoàn thành (`completedAt`) |
| **Cảnh báo bất thường** | `PATCH /api/v1/tasks/{taskId}/flag-abnormal` | Gắn cờ cảnh báo lâm sàng (`isAbnormalFlagged`) |

---

## 4. Hướng dẫn chạy và cài đặt

### A. Cài đặt trực tiếp file APK
File APK Debug đã được build thành công tại:
`android-companion/app/build/outputs/apk/debug/app-debug.apk`

Để cài đặt lên thiết bị thật hoặc máy ảo qua ADB:
```bash
adb install android-companion/app/build/outputs/apk/debug/app-debug.apk
```

### B. Biên dịch bằng Gradle
```bash
cd android-companion
./gradlew assembleDebug
```

### C. Chạy kiểm thử đơn vị (Unit Tests)
```bash
./gradlew testDebugUnitTest
```

### D. Cấu hình IP máy chủ linh hoạt
- **Trên máy ảo Android Emulator**: Mặc định kết nối tới `http://10.0.2.2:8080/api/v1/`.
- **Trên điện thoại Android thật**: Tại màn hình đăng nhập, bấm vào *"Cấu hình IP máy chủ (Backend)"* và nhập địa chỉ LAN của máy tính đang chạy Spring Boot (ví dụ: `http://192.168.1.15:8080/api/v1/`).
