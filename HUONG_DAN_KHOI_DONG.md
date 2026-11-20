# 🚀 Hướng Dẫn Toàn Diện Khởi Động Hệ Thống NHMS (Web & Android)

Tài liệu này hướng dẫn chi tiết từng bước để khởi động toàn bộ hệ sinh thái **Hệ thống Quản lý Viện dưỡng lão & Chăm sóc Người cao tuổi (NHMS - Nursing Home Management System)**, bao gồm:
1. **Cơ sở dữ liệu (Database)**: SQL Server (Docker Container).
2. **Backend**: Spring Boot 3 RESTful APIs (Cổng `8080`).
3. **Web Quản trị (Admin Web)**: React + TypeScript + Vite (Cổng `5173`).
4. **Ứng dụng Di động (Android Companion)**: Kotlin + Jetpack Compose (Chạy trên Máy ảo Android Emulator hoặc Thiết bị thật).

---

## 📑 Mục lục
- [1. Yêu cầu môi trường tiên quyết (Prerequisites)](#1-yêu-cầu-môi-trường-tiên-quyết-prerequisites)
- [2. Cách 1: Khởi động tự động bằng Docker Compose (Khuyên dùng)](#2-cách-1-khởi-động-tự-động-bằng-docker-compose-khuyên-dùng)
- [3. Cách 2: Khởi động thủ công từng thành phần (Chế độ Lập trình viên)](#3-cách-2-khởi-động-thủ-công-từng-thành-phần-chế-độ-lập-trình-viên)
  - [Bước 2.1: Khởi động Cơ sở dữ liệu SQL Server](#bước-21-khởi-động-cơ-sở-dữ-liệu-sql-server)
  - [Bước 2.2: Khởi động Backend Spring Boot](#bước-22-khởi-động-backend-spring-boot)
  - [Bước 2.3: Khởi động Web Quản trị Frontend](#bước-23-khởi-động-web-quản-trị-frontend)
  - [Bước 2.4: Khởi động Ứng dụng Di động Android CareCompanion](#bước-24-khởi-động-ứng-dụng-di-động-android-carecompanion)
- [4. Thông tin Tài khoản Dùng thử (Demo Accounts)](#4-thông-tin-tài-khoản-dùng-thử-demo-accounts)
- [5. Xử lý Sự cố Thường gặp (Troubleshooting)](#5-xử-lý-sự-cố-thường-gặp-troubleshooting)

---

## 1. Yêu cầu môi trường tiên quyết (Prerequisites)

Trước khi khởi động, hãy đảm bảo máy tính của bạn đã cài đặt các công cụ sau:

| Công cụ | Phiên bản khuyến nghị | Mục đích |
| :--- | :--- | :--- |
| **Docker Desktop** | Bản mới nhất (Hỗ trợ WSL2) | Chạy SQL Server, Backend & Frontend Container |
| **Java Development Kit (JDK)** | OpenJDK / Oracle JDK 21 | Chạy Backend Spring Boot |
| **Node.js & npm** | Node $\ge 18.x$, npm $\ge 9.x$ | Chạy Frontend React Vite |
| **Android SDK / Android Studio** | Android Studio Ladybug hoặc mới hơn | Chạy máy ảo Emulator và biên dịch Android App |
| **Git & PowerShell / Terminal** | Mặc định trên Windows / macOS / Linux | Thực thi các tập lệnh lệnh điều khiển |

> [!IMPORTANT]
> **Kiểm tra các cổng mạng (Ports):** Đảm bảo các cổng sau không bị ứng dụng khác chiếm dụng:
> * `1433`: SQL Server
> * `8080`: Backend Spring Boot API
> * `5173`: Frontend Web Vite
> * `5554 / 5555`: Android Emulator ADB Bridge

---

## 2. Cách 1: Khởi động tự động bằng Docker Compose (Khuyên dùng)

Cách này sẽ tự động tải image, cấu hình cơ sở dữ liệu và khởi động Backend cùng Web Frontend chỉ bằng một câu lệnh duy nhất.

### Bước 1: Mở Terminal tại thư mục gốc dự án
```powershell
cd d:\MockProject_062026_Nhom1-main
```

### Bước 2: Chạy Docker Compose
```powershell
docker compose up -d
```

### Bước 3: Kiểm tra trạng thái các Container
```powershell
docker compose ps
```
Khi thấy cả 3 dịch vụ `nhms-db`, `nhms-backend`, `nhms-frontend` đều ở trạng thái `Up / Healthy`:
* 🌐 **Truy cập Web Quản trị:** Mở trình duyệt tại [http://localhost:5173](http://localhost:5173)
* ⚙️ **Kiểm tra Backend API:** Mở trình duyệt tại [http://localhost:8080/api/v1/auth/me](http://localhost:8080/api/v1/auth/me)

> Để khởi động Ứng dụng Android, tiếp tục làm theo **[Bước 2.4](#bước-24-khởi-động-ứng-dụng-di-động-android-carecompanion)** bên dưới.

---

## 3. Cách 2: Khởi động thủ công từng thành phần (Chế độ Lập trình viên)

Dành cho nhà phát triển cần debug code, hot-reload frontend hoặc theo dõi log backend theo thời gian thực.

```
                      KIẾN TRÚC KẾT NỐI HỆ THỐNG NHMS
                      
  [ SQL Server Database ] (Port 1433: Docker container / Local)
            ▲
            │ JDBC Driver (sa / MatKhauCuaBan123!)
            ▼
  [ Backend Spring Boot ] (Port 8080: Java 21 REST API)
       ▲               ▲
       │               │
  HTTP/JSON       HTTP/JSON (10.0.2.2 / LAN IP)
       │               │
       ▼               ▼
[ Web Quản Trị ]  [ Android CareCompanion ]
(Vite: Port 5173) (Pixel Emulator / Phone)
```

---

### Bước 2.1: Khởi động Cơ sở dữ liệu SQL Server

Nếu máy bạn đã có sẵn container SQL Server (ví dụ: `sqlsv2019_container` hoặc `nhms-db`):

```powershell
# Khởi động container SQL Server
docker start sqlsv2019_container

# Hoặc nếu dùng docker-compose cho riêng database:
docker compose up -d db db-init
```

Kiểm tra kết nối và Database:
* **Host:** `localhost`
* **Port:** `1433`
* **Username:** `sa`
* **Password:** `MatKhauCuaBan123!`
* **Database Name:** `NURSING_HOME_DB`

---

### Bước 2.2: Khởi động Backend Spring Boot

Mở một cửa sổ Terminal mới:

```powershell
# 1. Di chuyển vào thư mục backend
cd d:\MockProject_062026_Nhom1-main\backend

# 2. Cách A: Khởi động bằng script PowerShell chuyên dụng (Tự động nạp .env)
.\start-dev-sqlserver.ps1

# Hoặc Cách B: Khởi động trực tiếp bằng Maven Wrapper
.\mvnw.cmd spring-boot:run -Dmaven.test.skip=true
```

> [!NOTE]
> Khi terminal hiển thị dòng thông báo:  
> `Started BackendApplication in X.XXX seconds (process running for ...)`  
> và `Tomcat started on port 8080 (http) with context path ''` nghĩa là Backend đã sẵn sàng!

---

### Bước 2.3: Khởi động Web Quản trị Frontend

Mở một cửa sổ Terminal mới:

```powershell
# 1. Di chuyển vào thư mục frontend
cd d:\MockProject_062026_Nhom1-main\frontend

# 2. Cài đặt các gói phụ thuộc (nếu là lần đầu chạy)
npm install

# 3. Khởi động máy chủ phát triển Vite
npm run dev
```

* Trình duyệt sẽ tự động mở hoặc bạn có thể nhấp vào link: **[http://localhost:5173](http://localhost:5173)**
* Giao diện Đăng nhập Web Quản trị NHMS sẽ xuất hiện.

---

### Bước 2.4: Khởi động Ứng dụng Di động Android CareCompanion

#### 1. Khởi động Máy ảo Android (Android Emulator)
* **Cách 1 (Qua Android Studio):** Mở Android Studio $\rightarrow$ Nhấp vào **Device Manager** ở thanh công cụ bên phải $\rightarrow$ Bấm nút **Play (▶)** tại máy ảo (khuyến nghị **Pixel 8 - API 35** hoặc API 34).
* **Cách 2 (Qua dòng lệnh PowerShell):**
  ```powershell
  # Liệt kê danh sách AVD có sẵn
  emulator -list-avds

  # Khởi động máy ảo (ví dụ tên máy ảo là Pixel_8_API_35)
  emulator -avd Pixel_8_API_35
  ```

Kiểm tra thiết bị đã kết nối:
```powershell
adb devices
# Kết quả hiển thị: emulator-5554    device
```

#### 2. Cài đặt và Chạy Ứng dụng Android

Mở một cửa sổ Terminal mới:

```powershell
# Di chuyển vào thư mục ứng dụng android
cd d:\MockProject_062026_Nhom1-main\android-companion

# Cách A: Cài đặt trực tiếp file APK Debug đã biên dịch sẵn (Nhanh nhất)
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Cách B: Biên dịch từ mã nguồn và tự động cài đặt
.\gradlew.bat installDebug
```

#### 3. Mở ứng dụng trên máy ảo
Sau khi cài đặt xong, bạn có thể:
* Nhấp vào biểu tượng dấu cộng y tế **CareCompanion** trên màn hình chính của máy ảo.
* Hoặc chạy lệnh sau từ Terminal để mở app ngay lập tức:
  ```powershell
  adb shell am start -n com.eldercare.companion/.MainActivity
  ```

> [!TIP]
> **Cơ chế liên thông mạng Máy ảo $\leftrightarrow$ Backend Máy tính:**
> * Trên máy ảo Android Emulator, địa chỉ IP **`10.0.2.2`** là cổng ngõ mặc định trỏ về `localhost` của máy tính.
> * Ứng dụng CareCompanion đã được cấu hình mặc định trỏ trực tiếp tới: `http://10.0.2.2:8080/api/v1/`, giúp ứng dụng liên thông dữ liệu tức thì với backend mà bạn không cần phải nhập IP thủ công.
> 
> **Nếu bạn chạy trên Điện thoại Android thật cắm cáp USB:**
> Chạy lệnh reverse cổng mạng qua ADB:
> ```powershell
> adb reverse tcp:8080 tcp:8080
> ```
> Khi đó điện thoại thật cũng sẽ gọi được API backend thông qua `localhost:8080` bình thường.

---

## 4. Thông tin Tài khoản Dùng thử (Demo Accounts)

Hệ thống sử dụng danh sách tài khoản chuẩn của viện dưỡng lão (được thiết lập sẵn trong cơ sở dữ liệu `NURSING_HOME_DB`). Bạn có thể sử dụng các tài khoản này trên cả Web Quản trị và Ứng dụng Android:

### 🏥 A. Đăng nhập Web Quản trị (Admin Web) & Android App

| Tài khoản (Email) | Mật khẩu | Chức vụ | Quyền hạn & Chức năng chính |
| :--- | :--- | :--- | :--- |
| `daniel.brooks@nhms-demo.local` | `Nhms@Demo2026` | **System Administrator** | **Tài khoản Quản trị chính của hệ thống.** Quản lý toàn viện, phân quyền nhân sự, buồng phòng. Trên App Android có sẵn **7 nhiệm vụ ca trực** và thông báo quản trị cấp viện. |
| `rebecca.thompson@nhms-demo.local` | `123456` | **DON - Director of Nursing** | **Giám đốc Điều dưỡng.** Phê duyệt Kế hoạch chăm sóc (Care Plan), kiểm duyệt biên bản sự cố té ngã SLA 24h, ký duyệt bàn giao SBAR toàn viện. |
| `jessica.turner@nhms-demo.local` | `123456` | **CNA_(Caregiver)** | **Điều dưỡng / Hộ lý đầu giường.** Chăm sóc cư dân, nhận cảnh báo thảm giường, cữ thuốc eMAR và thực hiện ghi chú SOP tại giường. |
| `cna@eldercare.com` | `123456` | **CNA (Minh Le)** | **Điều dưỡng viên giường bệnh.** Nhận thông báo tức thời, cảnh báo nguy cơ té ngã P102 và nhắc cữ thuốc nhóm FRID. |
| `nurse@eldercare.com` | `123456` | **Registered Nurse (Lan Tran)** | **Y tá phụ trách buồng bệnh.** Đánh giá sinh hiệu, theo dõi chỉ số lâm sàng và đơn thuốc. |

> [!IMPORTANT]
> * **Tài khoản Quản trị chính:** `daniel.brooks@nhms-demo.local` sử dụng mật khẩu duy nhất được bảo vệ: **`Nhms@Demo2026`**.
> * **Các tài khoản nhân viên khác** (DON, Bác sĩ, Y tá, Hộ lý CNA, Tiếp nhận Admission, Dinh dưỡng,...): đều sử dụng mật khẩu mặc định là **`123456`**.

---

## 5. Xử lý Sự cố Thường gặp (Troubleshooting)

### ❓ Sự cố 1: Backend báo lỗi kết nối SQL Server (`Connection refused` / `Login failed`)
* **Nguyên nhân:** Container Docker chứa SQL Server chưa bật, hoặc mật khẩu `sa` chưa khớp.
* **Cách khắc phục:**
  1. Kiểm tra container: `docker ps`
  2. Nếu container dừng, bật lại: `docker start sqlsv2019_container`
  3. Kiểm tra file `backend/.env` đảm bảo mật khẩu là: `DB_PASSWORD=MatKhauCuaBan123!`

### ❓ Sự cố 2: Cổng `8080` hoặc `5173` bị chiếm dụng
* **Cách kiểm tra tiến trình đang chiếm cổng trên Windows:**
  ```powershell
  netstat -ano | findstr 8080
  netstat -ano | findstr 5173
  ```
* **Cách tắt tiến trình chiếm cổng:**
  ```powershell
  taskkill /PID <PID_tìm_thấy> /F
  ```

### ❓ Sự cố 3: Đăng nhập báo lỗi `Invalid credentials. Please try again.`
* **Nguyên nhân:** Nhập sai mật khẩu hoặc tài khoản bị khóa / không tồn tại.
* **Cách khắc phục:** 
  * Với tài khoản Quản trị viên `daniel.brooks@nhms-demo.local`: Hãy chắc chắn nhập đúng mật khẩu **`Nhms@Demo2026`** (phân biệt hoa thường, có ký tự `@`).
  * Với các tài khoản còn lại (`rebecca.thompson@nhms-demo.local`, `jessica.turner@nhms-demo.local`, `cna@eldercare.com`,...): Mật khẩu là **`123456`**.

### ❓ Sự cố 4: App Android không tải được dữ liệu khi mở màn hình (Báo lỗi mạng)
* **Cách khắc phục:**
  1. Kiểm tra Backend Spring Boot đang chạy ở cổng 8080: mở [http://localhost:8080/api/v1/auth/me](http://localhost:8080/api/v1/auth/me) trên máy tính xem có phản hồi không.
  2. Đảm bảo máy ảo Android có mạng Internet/Wi-Fi ảo (`AndroidWifi`).
  3. Nếu dùng thiết bị thật cắm cáp, nhớ chạy lệnh `adb reverse tcp:8080 tcp:8080`.

---

🎉 **Chúc bạn có trải nghiệm vận hành và phát triển tuyệt vời cùng hệ sinh thái NHMS!**
