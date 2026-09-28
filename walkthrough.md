# Hệ thống Quản lý Viện dưỡng lão và Chăm sóc Sức khỏe Người cao tuổi (NHMS)

## 1. Tên đồ án
**Nghiên cứu và triển khai Hệ thống Quản lý Viện dưỡng lão và Chăm sóc Sức khỏe Người cao tuổi (Nursing Home Management System - NHMS)**

## 2. Mô tả dự án
Hệ thống Quản lý Viện dưỡng lão (NHMS) là một giải pháp công nghệ toàn diện được thiết kế để tự động hóa, số hóa và tối ưu hóa toàn bộ các hoạt động vận hành, hành chính, tài chính và chăm sóc y tế tại các cơ sở dưỡng lão hoặc trung tâm chăm sóc người cao tuổi.

Dự án này tập trung vào việc giải quyết các thách thức trong quản lý thủ công truyền thống thông qua việc xây dựng một hệ thống đa phân hệ bảo mật cao. Hệ thống cho phép quản lý toàn trình từ khâu tiếp nhận cư dân, lập kế hoạch chăm sóc sức khỏe cá nhân hóa, quản lý cấp phát thuốc điện tử (eMAR), giám sát và xử lý các sự cố/rủi ro y tế, tối ưu hóa điều phối nhân lực và tính toán thanh toán hóa đơn tự động.

Hệ thống được thiết kế tuân thủ nghiêm ngặt các tiêu chuẩn về bảo mật thông tin sức khỏe y tế (như HIPAA) thông qua cơ chế phân quyền chặt chẽ và ghi nhật ký truy vết chi tiết (Audit Trail) đối với các hành vi truy cập dữ liệu nhạy cảm.

### Mục tiêu của đề tài
* 📋 **Tự động hóa toàn diện nghiệp vụ hành chính:** Số hóa quy trình tiếp nhận cư dân (Resident Intake), quản lý phòng/giường (Facility Setup), và điều phối nhân sự (Human Resources).
* 🩺 **Nâng cao chất lượng chăm sóc sức khỏe:** Xây dựng các phân hệ lập kế hoạch chăm sóc (Care Plan) chi tiết và kiểm soát quy trình cấp phát thuốc điện tử (eMAR) nhằm tránh sai sót y khoa.
* 🛡️ **Quản lý rủi ro hiệu quả:** Thiết lập hệ thống ghi nhận, theo dõi sự cố (Incident Tracking) và cấu hình các quy định xử lý sự cố (SLA Rules) để đảm bảo an toàn cho cư dân.
* 🔒 **Đảm bảo bảo mật y tế:** Triển khai các giải pháp an ninh thông tin, mã hóa và hệ thống giám sát truy vết (Audit Trail / PHI Access Logs) đáp ứng các yêu cầu khắt khe của HIPAA.
* 💳 **Tối ưu hóa tài chính:** Tự động hóa quá trình lập hóa đơn, áp dụng các biểu phí dịch vụ và quản lý thanh toán của cư dân (Finance & Billing).

### Kết quả dự kiến của đề tài
* 💻 **Hệ thống phần mềm hoàn chỉnh:** Giao diện Web SPA (React + TypeScript) hiện đại, trực quan cho nhân viên/giảng viên quản lý; kết hợp với hệ thống Backend RESTful APIs (Spring Boot + SQL Server) mạnh mẽ và bảo mật.
* 🗃️ **Cơ sở dữ liệu tập trung:** Thiết kế và tối ưu hóa hệ quản trị cơ sở dữ liệu lưu trữ thông tin cư dân, bệnh án, thuốc men và tài chính an toàn.
* 🛡️ **Hệ thống bảo mật vững chắc:** Tích hợp Spring Security, JWT và cơ chế Audit Log hoạt động ổn định giúp phát hiện và ngăn chặn các truy cập trái phép vào thông tin sức khỏe cá nhân (PHI).
* 📈 **Báo cáo đánh giá chi tiết:** Đánh giá hiệu suất vận hành, độ chính xác của quy trình eMAR và tính ổn định của hệ thống trong môi trường thử nghiệm thực tế.

---

## 3. Nội dung thực hiện

### 📂 Nghiên cứu tổng quan và thiết kế hệ thống
* Tìm hiểu các nghiệp vụ vận hành thực tế tại viện dưỡng lão và các yêu cầu bảo mật thông tin y tế theo tiêu chuẩn HIPAA.
* Thiết kế kiến trúc hệ thống tổng thể (Client-Server), thiết kế luồng dữ liệu (Dataflow) và thiết kế chi tiết cơ sở dữ liệu quan hệ trên SQL Server.

### ⚙️ Xây dựng môi trường phát triển và công cụ thực hiện
* Khởi dựng cấu trúc dự án Frontend (React, TypeScript, Vite) và Backend (Spring Boot, Java, Maven).
* Cấu hình kết nối SQL Server và tích hợp các thư viện bổ trợ như Spring Data JPA, Spring Security, JWT, Lombok, JUnit và Mockito.

### 🏠 Xây dựng Module Quản lý Cư dân và Cơ sở vật chất (Resident Intake & Facility Setup)
* Phát triển các chức năng tiếp nhận cư dân (Resident Intake): Hồ sơ cá nhân (Resident Profile), đánh giá cấp độ chăm sóc (Care Level), danh bạ liên hệ gia đình (Family Contacts), và quản lý sổ tiếp nhận (Admission Ledger).
* Phát triển phân hệ thiết lập cơ sở (Facility Setup): Định cấu hình thông tin chi nhánh, quản lý phòng và giường bệnh (Room & Bed management).

### 💊 Triển khai Chăm sóc, Lịch dùng thuốc và Quản lý sự cố (Care Plan, eMAR & Risk Incident)
* Xây dựng Module Kế hoạch chăm sóc (Care Plan) phục vụ điều dưỡng lập lịch chăm sóc định kỳ cho cư dân.
* Phát triển phân hệ eMAR (Electronic Medication Administration Record) quản lý danh mục thuốc, đơn thuốc của cư dân và ghi nhận lịch sử cho uống thuốc theo thời gian thực.
* Xây dựng phân hệ Quản lý sự cố (Risk & Incident Tracking) ghi nhận các tai nạn/sự cố (như té ngã) kèm mức độ nghiêm trọng và thiết lập các quy tắc SLA xử lý nhanh.

### 💵 Xây dựng các Module Nhân sự, Tài chính và Bảo mật (HR, Finance & Security Telemetry)
* Phát triển Module Nhân sự (Human Resources): Quản lý thông tin nhân viên, lập lịch trực và kiểm soát tỷ lệ tuân thủ nhân sự.
* Phát triển Module Tài chính (Finance & Billing): Cấu hình biểu giá dịch vụ, tự động hóa tính toán hóa đơn hàng tháng cho cư dân.
* Tích hợp cơ chế an ninh: Lọc bảo mật JWT, mã hóa mật khẩu, phân quyền Role-based Access Control (RBAC).
* Xây dựng hệ thống Security Telemetry ghi vết hoạt động truy cập thông tin nhạy cảm của cư dân (PHI Access Audit Trail).

### 🧪 Kiểm thử và Hoàn thiện hệ thống
* Thực hiện viết Unit Test cho tầng Service/Repository của Backend.
* Kiểm thử tích hợp (Integration Test) kiểm tra tính nhất quán dữ liệu giữa các module và giao tiếp API.
* Tối ưu hóa hiệu năng, chỉnh sửa lỗi giao diện và hoàn thiện các tài liệu báo cáo, slide thuyết trình đồ án.

---

## 📅 Kế hoạch thực hiện (Lộ trình chi tiết)

| Thời gian | Nội dung thực hiện |
| :--- | :--- |
| **Tuần thứ 1**<br>*(từ 27/07/2026 đến 02/08/2026)* | Nghiên cứu tổng quan về nghiệp vụ quản lý viện dưỡng lão và các quy chuẩn HIPAA. Thiết kế Cơ sở dữ liệu quan hệ và thiết lập cấu trúc khung dự án (Backend & Frontend). |
| **Tuần thứ 2**<br>*(từ 03/08/2026 đến 09/08/2026)* | Phát triển Module Xác thực (Auth), Tiếp nhận cư dân (Resident Intake: Resident Profile, Family Contacts) và Thiết lập cơ sở vật chất (Facility Setup: Room & Bed). |
| **Tuần thứ 3**<br>*(từ 10/08/2026 đến 16/08/2026)* | Xây dựng Module Chăm sóc & Y tế: Lập kế hoạch chăm sóc (Care Plan), Quản lý cấp phát thuốc điện tử (eMAR) và Module Quản lý nhân sự (HR Staffing). |
| **Tuần thứ 4**<br>*(từ 17/08/2026 đến 23/08/2026)* | Xây dựng Module Quản lý rủi ro & Sự cố (Risk & Incident Tracking), thiết lập các quy tắc SLA, tích hợp Module Tài chính & Tính toán hóa đơn (Finance & Billing). |
| **Tuần thứ 5**<br>*(từ 24/08/2026 đến 30/08/2026)* | Tích hợp cơ chế bảo mật nâng cao và Nhật ký truy vết (HIPAA Audit Trail / Security Telemetry). Kiểm thử toàn diện hệ thống (Unit Test), sửa lỗi giao diện, tối ưu hóa hiệu năng và hoàn thiện báo cáo, slide báo cáo. |

> [!NOTE]
> Lộ trình trên được xây dựng dựa trên mốc thời gian bắt đầu từ ngày **27/07/2026** làm điểm mốc khởi đầu của Tuần thứ 1. Các tuần kế tiếp nối tiếp liên tục theo chu kỳ 7 ngày.
