# 🏥 ElderCare NHMS - Nursing Home Management System

> **Hệ sinh thái Toàn diện Quản lý Viện Dưỡng Lão & Chăm sóc Người cao tuổi tích hợp Clinical AI & Trợ lý Điều dưỡng Di động Android CareCompanion.**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-5.x-purple.svg)](https://vitejs.dev/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-violet.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Android-green.svg)](https://developer.android.com/jetpack/compose)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)
[![SQL Server](https://img.shields.io/badge/MSSQL-2022-red.svg)](https://www.microsoft.com/sql-server)
[![AI Engine](https://img.shields.io/badge/AI%20Engine-Gemini%20%7C%20Ollama-yellow.svg)](https://ai.google.dev/)

---

## 📋 Mục lục

1. [Giới thiệu Tổng quan](#1-giới-thiệu-tổng-quan)
2. [Kiến trúc Hệ thống & Ngăn xếp Công nghệ](#2-kiến-trúc-hệ-thống--ngăn-xếp-công-nghệ)
3. [Các Phân hệ Tính năng Chính](#3-các-phân-hệ-tính-năng-chính)
4. [Yêu cầu Môi trường Tiên quyết (Prerequisites)](#4-yêu-cầu-môi-trường-tiên-quyết-prerequisites)
5. [Hướng dẫn Khởi động trên Máy Hoàn Toàn Mới](#5-hướng-dẫn-khởi-động-trên-máy-hoàn-toàn-mới)
   - [Cách 1: Khởi động tự động bằng Docker Compose (Khuyên dùng)](#cách-1-khởi-động-tự-động-bằng-docker-compose-khuyên-dùng)
   - [Cách 2: Khởi động thủ công từng phân hệ (Chế độ Lập trình viên)](#cách-2-khởi-động-thủ-công-từng-phân-hệ-chế-độ-lập-trình-viên)
6. [Hướng dẫn Cài đặt & Chạy App Android CareCompanion](#6-hướng-dẫn-cài-đặt--chạy-app-android-carecompanion)
7. [Cấu hình Trí tuệ Nhân tạo (Clinical AI Engine)](#7-cấu-hình-trí-tuệ-nhân-tạo-clinical-ai-engine)
8. [Danh sách Tài khoản Dùng thử (Demo Accounts)](#8-danh-sách-tài-khoản-dùng-thử-demo-accounts)
9. [Chính sách Bảo mật & Quy tắc Đóng góp Mã nguồn](#9-chính-sách-bảo-mật--quy-tắc-đóng-góp-mã-nguồn)
10. [Xử lý Sự cố Thường gặp (Troubleshooting FAQs)](#10-xử-lý-sự-cố-thường-gặp-troubleshooting-faqs)

---

## 1. Giới thiệu Tổng quan

**ElderCare NHMS** là giải pháp phần mềm chuyên sâu được thiết kế dành cho các cơ sở dưỡng lão, viện điều dưỡng và trung tâm chăm sóc dài hạn. Hệ sinh thái kết nối chặt chẽ giữa:
* **Hội đồng Quản trị & Bác sĩ/Giám đốc Điều dưỡng (DON):** Quản lý buồng phòng, thẩm định kế hoạch chăm sóc, giám sát sự cố nội viện, kiểm soát chất lượng lâm sàng.
* **Đội ngũ Y tá & Điều dưỡng viên / Hộ lý đầu giường (CNA):** Ứng dụng di động thao tác nhanh tại giường bệnh, ghi nhận cữ thuốc, thực hiện nhiệm vụ ca trực, ghi chú giọng nói và bàn giao ca.
* **Gia đình Người cao tuổi:** Cổng thông tin tìm hiểu dịch vụ, hỗ trợ định hướng chăm sóc thông minh.
* **Hệ thống Trí tuệ Nhân tạo Lâm sàng (Clinical AI):** Tự động phát hiện nguy cơ té ngã theo thang điểm DFRS, rà soát tương tác thuốc nhóm FRID, tóm tắt biên bản giao ban chuẩn y khoa SBAR.

---

## 2. Kiến trúc Hệ thống & Ngăn xếp Công nghệ

```
                             KIẾN TRÚC TỔNG THỂ NHMS
                             
  ┌────────────────────────────────────────────────────────────────────────┐
  │                           NGƯỜI DÙNG CUỐI                              │
  └───────┬────────────────────────────────┬───────────────────────────────┘
          │ (Trình duyệt Web)              │ (Thiết bị Android / Máy ảo)
          ▼                                ▼
  ┌─────────────────────────┐      ┌──────────────────────────────────────┐
  │   Web Admin & Portal    │      │  Android CareCompanion App           │
  │   React 18 + TypeScript │      │  Kotlin + Jetpack Compose            │
  │   Tailwind CSS + Vite   │      │  Humanized Clinical UI               │
  │   (Port 80 / 5173)      │      │  (Retrofit, MVVM, Material 3)        │
  └───────────┬─────────────┘      └──────────────────┬───────────────────┘
              │                                       │
              │ REST API (JSON)                       │ REST API (JSON / 10.0.2.2)
              ▼                                       ▼
  ┌───────────────────────────────────────────────────────────────────────┐
  │                 BACKEND SERVER (Spring Boot 3.x)                      │
  │   - Spring Security & JWT Auth                                        │
  │   - Clinical Core Services (Care Plan, eMAR, Incidents, Staffing)     │
  │   - Hybrid Clinical AI Engine (Google Gemini API & Local Ollama)      │
  │   - HikariCP Connection Pool                                          │
  │   (Port 8080)                                                         │
  └───────────────────┬───────────────────────────────┬───────────────────┘
                      │                               │
                      ▼                               ▼
       ┌──────────────────────────────┐ ┌─────────────────────────────────┐
       │   CƠ SỞ DỮ LIỆU CHÍNH        │ │   AI LLM PROVIDERS              │
       │   Microsoft SQL Server 2022  │ │   - Cloud: Google AI Studio     │
       │   (Port 1433: Docker / Host) │ │     (Gemini 1.5/2.0/3.8 Flash)  │
       │   Database: NURSING_HOME_DB  │ │   - Local: Ollama CPU / GPU     │
       └──────────────────────────────┘ └─────────────────────────────────┘
```

### Chi tiết Ngăn xếp Công nghệ (Tech Stack)

| Phân hệ | Công nghệ sử dụng | Chức năng chính |
| :--- | :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.x, Spring Data JPA, Spring Security, Spring AI, Hibernate | Cung cấp RESTful API, quản trị nghiệp vụ y tế, xử lý phân quyền JWT, giao tiếp AI |
| **Frontend Web** | React 18, TypeScript, Vite, Tailwind CSS, TanStack Query/Table, Lucide Icons | Cổng quản trị viện, biểu đồ lâm sàng, lập kế hoạch chăm sóc, tiếp nhận cư dân |
| **Android App** | Kotlin, Jetpack Compose, Material 3, ViewModel, Coroutines & Flow, Retrofit 2 | Ứng dụng di động cho điều dưỡng/CNA, Humanized UI trực quan, hoạt động ngoại vi |
| **Database** | Microsoft SQL Server 2022 | Lưu trữ dữ liệu quan hệ, lịch sử lâm sàng, bảng sự cố, người dùng và phân quyền |
| **Clinical AI** | Google Gemini API (v1beta/v1), Ollama Local Model Fallback | Đánh giá nguy cơ té ngã DFRS, rà soát tương tác thuốc eMAR, tóm tắt giao ban SBAR |
| **DevOps & Deploy**| Docker, Docker Compose, Nginx Reverse Proxy, DuckDNS | Container hóa toàn bộ hệ thống, tự động tái khởi động `unless-stopped` |

---

## 3. Các Phân hệ Tính năng Chính

### 🌐 A. Web Quản trị & Cổng Lâm sàng (`/frontend`)
* **Hồ sơ Cư dân 360° & Tiếp nhận (Resident Intake):** Quản lý quy trình tiếp nhận, sàng lọc tiền nhập viện, chỉ định phòng, theo dõi thông tin người giám hộ và tiền sử bệnh án.
* **Thiết kế Kế hoạch Chăm sóc (Care Planning):** Lập mục tiêu chăm sóc (Care Goals), các biện pháp can thiệp (Interventions) và phân bổ công việc tự động cho điều dưỡng.
* **Quản lý Sự cố Nội viện (Incident Management):** Ghi nhận sự cố (té ngã, thất lạc, hành vi), phân loại mức độ nghiêm trọng và theo dõi hạn xử lý theo chuẩn SLA 24h.
* **Giám sát Nhiệm vụ Hộ lý (Care Task Monitoring):** Theo dõi tiến độ công việc theo từng cư dân hoặc theo từng điều dưỡng viên theo thời gian thực.
* **Cấu hình Viện & AI Engine:** Thiết lập mức độ chăm sóc (LOC Rates), quản lý tài khoản nhân viên, cấu hình linh hoạt khóa API Gemini hoặc máy chủ Ollama nội bộ.

### 📱 B. Ứng dụng Di động Android CareCompanion (`/android-companion`)
* **Humanized Clinical Design:** Thiết kế giao diện chuyên nghiệp dành cho môi trường y tế; loại bỏ các thuật ngữ AI máy móc, thẻ thông tin trực quan, màu sắc phân định rõ ràng.
* **Hồ sơ Cư dân 360° BottomSheet:** Nhấp vào bất kỳ cư dân nào để tra cứu ngay tiền sử bệnh, dị ứng, người liên hệ khẩn cấp và mức độ rủi ro.
* **Lịch uống thuốc eMAR chia ca:** Lọc thuốc theo thời điểm (Sáng / Trưa / Chiều / Tối), hiển thị cảnh báo thuốc dễ gây té ngã (FRID) và nút chạm xác nhận uống thuốc tức thì.
* **Đánh giá Nguy cơ Té ngã DFRS:** Tự động tính toán điểm số dựa trên 4 phân nhóm (Tiền sử té ngã, thuốc an thần/hạ áp, mức độ vận động ADL, sinh hiệu bất thường) kèm hướng dẫn can thiệp.
* **Tách biệt rõ ràng Nhiệm vụ & Hoạt động:**
  - **Trang Nhiệm vụ (Shift Tasks):** Giới hạn đúng 7 đầu việc cốt lõi của ca trực để điều dưỡng tập trung xử lý.
  - **Trang Hoạt động (Activities & Rounds):** Hiển thị 20 lịch trình sinh hoạt chung, trị liệu và đi buồng định kỳ.
* **Bàn giao Ca Trực SBAR Số hóa:** Cho phép điều dưỡng nhập bổ sung ghi chú phát sinh và ký xác nhận nhận ca điện tử.
* **Thông báo Lâm sàng Tương tác:** Hỗ trợ nhấp xem chi tiết sự cố/cảnh báo và tự động đánh dấu đã đọc.

---

## 4. Yêu cầu Môi trường Tiên quyết (Prerequisites)

Trước khi tiến hành cài đặt trên một máy tính hoàn toàn mới, hãy cài đặt các công cụ sau:

| Công cụ | Phiên bản khuyến nghị | Mục đích |
| :--- | :--- | :--- |
| **Git** | Bản mới nhất | Tải và đồng bộ mã nguồn |
| **Docker Desktop** | Bản mới nhất (bật WSL2 trên Windows) | Chạy SQL Server, Backend và Web Frontend trọn gói |
| **Java JDK** | OpenJDK / Eclipse Temurin 21 | Chạy và biên dịch mã nguồn Backend khi chạy thủ công |
| **Node.js & npm** | Node.js $\ge 18.x$, npm $\ge 9.x$ | Biên dịch và chạy Frontend React Vite |
| **Android Studio** | Android Studio Ladybug (2024.2+) | Chạy máy ảo Emulator và biên dịch Android Companion App |

> [!IMPORTANT]
> **Kiểm tra các cổng mạng khả dụng trên máy:** Đảm bảo các cổng sau không bị ứng dụng khác chiếm giữ:
> * `1433`: Microsoft SQL Server
> * `8080`: Spring Boot REST API
> * `5173` hoặc `80`: Web Frontend React Nginx / Vite
> * `5554 / 5555`: Cổng gỡ lỗi ADB kết nối thiết bị Android

---

## 5. Hướng dẫn Khởi động trên Máy Hoàn Toàn Mới

### Cách 1: Khởi động tự động bằng Docker Compose (Khuyên dùng)

Đây là phương thức nhanh nhất, không cần cài đặt JDK hay Node.js trên máy chủ, hệ thống sẽ tự động cấu hình và khởi tạo toàn bộ CSDL và dịch vụ.

#### Bước 1: Clone mã nguồn từ GitHub
Mở Terminal / PowerShell và chạy:
```bash
git clone https://github.com/Tvuong123com/BackupNHMS.git
cd BackupNHMS
```

#### Bước 2: Khởi chạy toàn bộ hệ thống
```bash
docker compose up -d --build
```
> Lệnh này sẽ:
> 1. Tải và khởi động Microsoft SQL Server 2022 (`nhms-db`) trên cổng `1433`.
> 2. Chờ CSDL sẵn sàng và tự động tạo cơ sở dữ liệu `NURSING_HOME_DB` (`nhms-db-init`).
> 3. Tự động đóng gói Maven và chạy Backend Spring Boot 3 (`nhms-backend`) trên cổng `8080`.
> 4. Tự động đóng gói Node.js và khởi chạy Nginx Web (`nhms-frontend`) trên cổng `80` và `5173`.

#### Bước 3: Kiểm tra trạng thái các dịch vụ
```bash
docker compose ps
```
Khi các dịch vụ hiển thị `Up` hoặc `Healthy`, hệ thống đã sẵn sàng hoạt động:
* 🌐 **Truy cập Web Quản trị:** [http://localhost:5173](http://localhost:5173) hoặc [http://localhost](http://localhost)
* ⚙️ **Kiểm tra Backend API:** [http://localhost:8080/api/v1/auth/me](http://localhost:8080/api/v1/auth/me)
* 🗄️ **Kết nối Database SQL Server:** `localhost:1433` (Username: `sa`, Password: `MatKhauCuaBan123!`)

---

### Cách 2: Khởi động thủ công từng phân hệ (Chế độ Lập trình viên)

Dành cho các lập trình viên cần chỉnh sửa mã nguồn, debug thời gian thực hoặc hot-reload giao diện.

#### Bước 2.1: Khởi động Cơ sở dữ liệu SQL Server
Bạn có thể dùng Docker Compose để bật riêng CSDL:
```bash
docker compose up -d db db-init
```
Hoặc sử dụng phiên bản SQL Server đã cài đặt sẵn trên máy tính với thông tin:
* **Host:** `localhost`
* **Port:** `1433`
* **Database:** `NURSING_HOME_DB`
* **User / Password:** `sa` / `MatKhauCuaBan123!`

#### Bước 2.2: Cấu hình và Khởi động Backend Spring Boot
1. Di chuyển vào thư mục backend và tạo file cấu hình môi trường `.env`:
   ```bash
   cd backend
   cp .env.example .env
   ```
2. Khởi chạy ứng dụng:
   - **Trên Windows PowerShell (khuyên dùng):**
     ```powershell
     .\start-dev-sqlserver.ps1
     ```
   - **Hoặc qua lệnh Maven:**
     ```bash
     ./mvnw spring-boot:run -Dmaven.test.skip=true
     # Trên Windows CMD: mvnw.cmd spring-boot:run -Dmaven.test.skip=true
     ```
   Khi màn hình hiển thị `Started BackendApplication in X.XXX seconds`, Backend đã hoạt động tại `http://localhost:8080`.

#### Bước 2.3: Cấu hình và Khởi động Frontend Web
Mở một cửa sổ Terminal mới:
1. Di chuyển vào thư mục frontend:
   ```bash
   cd frontend
   cp .env.example .env
   ```
2. Cài đặt các thư viện phụ thuộc:
   ```bash
   npm install
   ```
3. Chạy máy chủ phát triển Vite:
   ```bash
   npm run dev
   ```
4. Mở trình duyệt tại: **[http://localhost:5173](http://localhost:5173)**.

---

## 6. Hướng dẫn Cài đặt & Chạy App Android CareCompanion

Ứng dụng di động **Android CareCompanion** dành cho điều dưỡng viên hỗ trợ kết nối trực tiếp với Backend máy chủ.

### 1. Chuẩn bị Thiết bị hoặc Máy ảo Android
* **Sử dụng Máy ảo (Android Emulator):** 
  - Khuyến nghị tạo máy ảo **Pixel 8** chạy hệ điều hành **Android 14 (API 34)** hoặc **Android 15 (API 35)**.
  - Bật máy ảo qua Android Studio Device Manager hoặc dòng lệnh:
    ```bash
    emulator -avd <Tên_Máy_Ảo>
    ```
* **Sử dụng Điện thoại Android thật:**
  - Bật tính năng **USB Debugging** (Gỡ lỗi qua USB) trong Developer Options trên điện thoại.
  - Cắm cáp USB kết nối điện thoại với máy tính.
  - Chạy lệnh ánh xạ cổng mạng API:
    ```bash
    adb reverse tcp:8080 tcp:8080
    ```

### 2. Cài đặt và Chạy Ứng dụng
Mở Terminal tại thư mục gốc dự án:
```bash
cd android-companion

# Cách A: Biên dịch và cài đặt tự động vào thiết bị đang kết nối
./gradlew.bat installDebug   # Trên Windows
# ./gradlew installDebug     # Trên macOS / Linux

# Cách B: Mở trực tiếp thư mục android-companion bằng Android Studio và bấm nút Run (Shift + F10)
```

### 3. Mở Ứng dụng
* Nhấp vào biểu tượng dấu cộng y tế **CareCompanion** trên màn hình điện thoại/máy ảo.
* Hoặc chạy lệnh khởi động từ Terminal:
  ```bash
  adb shell am start -n com.eldercare.companion/.MainActivity
  ```

> [!TIP]
> **Cấu hình Kết nối Mạng Tự động:**
> * Trên máy ảo Android Emulator, địa chỉ IP **`10.0.2.2`** là cổng ngõ mặc định ánh xạ về `localhost` của máy tính. Ứng dụng CareCompanion đã được lập trình sẵn để tự động kết nối qua `http://10.0.2.2:8080/api/v1/`, hoạt động ngay lập tức mà không cần cấu hình IP thủ công.

---

## 7. Cấu hình Trí tuệ Nhân tạo (Clinical AI Engine)

Hệ thống hỗ trợ cơ chế Trí tuệ Nhân tạo Lai (Hybrid AI) linh hoạt và an toàn:

### Cách 1: Sử dụng Google Gemini API (Khuyên dùng)
1. Đăng nhập vào Web Quản trị bằng tài khoản Quản trị viên (`daniel.brooks@nhms-demo.local`).
2. Điều hướng tới mục **Cài đặt hệ thống (Settings)** $\rightarrow$ chọn tab **Trí tuệ Nhân tạo (AI Engine)**.
3. Nhập khóa Google AI Studio API Key của bạn (bắt đầu bằng `AIzaSy...`).
4. Chọn mô hình hoạt động (ví dụ: `gemini-1.5-flash`, `gemini-2.0-flash`, hoặc `gemini-3.8-flash`).
5. Bấm **Lưu & Kiểm tra kết nối**.
   > *Lưu ý:* Khóa API được lưu cục bộ an toàn trên trình duyệt hoặc bộ nhớ máy chủ quản trị, hoàn toàn không bị commit lên kho mã nguồn Git.

### Cách 2: Sử dụng Local Ollama (Chạy Offline - Không cần Internet)
1. Tải và cài đặt [Ollama](https://ollama.com/) trên máy tính.
2. Tải mô hình nhỏ gọn:
   ```bash
   ollama pull qwen2.5:1.5b
   ```
3. Tại giao diện Cài đặt AI của Web Quản trị, chuyển công tắc sang **Local Ollama**, hệ thống sẽ tự động định tuyến toàn bộ phân tích lâm sàng về máy chủ Ollama nội bộ (`http://localhost:11434`).

---

## 8. Danh sách Tài khoản Dùng thử (Demo Accounts)

Cơ sở dữ liệu mẫu đã được nạp sẵn danh sách người dùng cho từng chức vụ nghiệp vụ trong viện dưỡng lão:

| Email Đăng nhập | Mật khẩu | Chức vụ (Role) | Chức năng & Quyền hạn chính |
| :--- | :--- | :--- | :--- |
| `daniel.brooks@nhms-demo.local` | `Nhms@Demo2026` | **System Administrator** | **Quản trị viên toàn hệ thống.** Toàn quyền thiết lập cơ sở, phân quyền tài khoản, cấu hình AI. Trên App Android có sẵn 7 nhiệm vụ ca trực mẫu và thông báo cấp viện. |
| `rebecca.thompson@nhms-demo.local` | `123456` | **Director of Nursing (DON)** | **Giám đốc Điều dưỡng.** Phê duyệt Kế hoạch chăm sóc (Care Plan), kiểm duyệt báo cáo sự cố té ngã SLA 24h, ký duyệt bàn giao SBAR toàn viện. |
| `jessica.turner@nhms-demo.local` | `123456` | **CNA (Caregiver)** | **Điều dưỡng viên / Hộ lý đầu giường.** Chăm sóc trực tiếp, thực hiện eMAR, nhận cảnh báo thảm giường và ghi chú lâm sàng tại buồng. |
| `cna@eldercare.com` | `123456` | **CNA (Minh Le)** | **Điều dưỡng viên ca trực.** Nhận cảnh báo nguy cơ té ngã P102, nhắc cữ thuốc nhóm FRID và thực hiện nhiệm vụ ca trực. |
| `nurse@eldercare.com` | `123456` | **Registered Nurse (Lan Tran)** | **Y tá phụ trách buồng bệnh.** Đánh giá sinh hiệu, theo dõi chỉ số lâm sàng và phối hợp với bác sĩ điều trị. |

> [!NOTE]
> * Tài khoản Quản trị cấp cao `daniel.brooks@nhms-demo.local` sử dụng mật khẩu riêng: **`Nhms@Demo2026`** (phân biệt chữ hoa, chữ thường và ký tự đặc biệt).
> * Tất cả các tài khoản nhân viên y tế còn lại đều sử dụng mật khẩu mặc định là: **`123456`**.

---

## 9. Chính sách Bảo mật & Quy tắc Đóng góp Mã nguồn

Để đảm bảo tính an toàn thông tin y tế và tuân thủ các tiêu chuẩn bảo mật dữ liệu:

1. **Tuyệt đối không lưu trữ khóa bí mật trong mã nguồn:**
   * Không bao giờ commit các file chứa API Key (như `ai-settings.json`, Google AI Studio API Key, AWS Access Keys).
   * Không commit các file khóa cá nhân (`*.pem`, `*.key`, `*.jks`, `*.p12`).
   * Không commit các file cấu hình môi trường thực (`.env`, `.env.production`). Chỉ commit file mẫu (`.env.example`).
2. **Loại trừ dữ liệu sao lưu lớn:**
   * Các tệp sao lưu CSDL dung lượng lớn (`*.bak`, `*.mdf`, `*.ldf`, `nhms.bak`) và các tệp nén (`*.tar.gz`, `nhms-app.tar.gz`) đã được cấu hình tự động loại bỏ trong [.gitignore](file:///.gitignore).
3. **Biên dịch Android an toàn:**
   * Toàn bộ tệp thực thi `*.apk`, `*.aab` và thư mục `build/` đều được bỏ qua để giữ kho mã nguồn tinh gọn và minh bạch.

---

## 10. Xử lý Sự cố Thường gặp (Troubleshooting FAQs)

### ❓ 1. Backend báo lỗi không thể kết nối SQL Server (`Connection refused` hoặc `Login failed for user 'sa'`)
* **Nguyên nhân:** Container SQL Server chưa khởi động hoàn tất hoặc mật khẩu SA chưa trùng khớp.
* **Xử lý:**
  1. Kiểm tra trạng thái container: `docker ps`
  2. Khởi động lại container database: `docker compose up -d db db-init`
  3. Kiểm tra file `backend/.env` để đảm bảo mật khẩu khớp với cấu hình: `DB_PASSWORD=MatKhauCuaBan123!`

### ❓ 2. Cổng mạng `8080` hoặc `5173` bị chiếm dụng
* **Xử lý trên Windows (PowerShell):**
  ```powershell
  # Tìm PID tiến trình chiếm cổng
  netstat -ano | findstr 8080
  # Tắt tiến trình
  taskkill /PID <MÃ_PID> /F
  ```

### ❓ 3. Đăng nhập báo lỗi `Invalid credentials. Please try again.`
* **Xử lý:**
  * Với tài khoản Quản trị `daniel.brooks@nhms-demo.local`: Hãy chắc chắn nhập đúng mật khẩu **`Nhms@Demo2026`**.
  * Với các tài khoản y tế khác: Mật khẩu là **`123456`**.

### ❓ 4. App Android báo lỗi kết nối mạng khi tải dữ liệu
* **Xử lý:**
  1. Mở trình duyệt trên máy tính và kiểm tra Backend có đang chạy tại `http://localhost:8080/api/v1/auth/me`.
  2. Đảm bảo máy ảo Android đang kết nối Wi-Fi ảo (`AndroidWifi`).
  3. Nếu sử dụng điện thoại thật kết nối bằng cáp USB, hãy chạy lệnh:
     ```bash
     adb reverse tcp:8080 tcp:8080
     ```

---

<div align="center">
  <b>ElderCare NHMS - Chăm sóc Tận tâm, Ứng dụng Thông minh vì Sức khỏe Người cao tuổi.</b><br/>
  <i>Mọi thắc mắc và đóng góp xin gửi về repository chính thức hoặc liên hệ đội ngũ phát triển.</i>
</div>
