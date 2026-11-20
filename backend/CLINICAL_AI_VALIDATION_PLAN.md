# Kế hoạch kiểm định AI lâm sàng — bối cảnh Hoa Kỳ

**Ngày lập:** 2026-10-05  
**Phạm vi:** F1–F10 và các tính năng AI công khai W1–W6, ưu tiên F4 (té ngã) và F9 (thuốc).  
**Tình trạng:** kế hoạch triển khai và kiểm định; chưa phải chứng nhận tuân thủ hoặc phê duyệt lâm sàng.

## 1. Mục đích sử dụng và ranh giới

| Luồng | Người dùng | Đầu ra được phép hiện nay | Đầu ra chưa được phép |
| --- | --- | --- | --- |
| F4 | Nurse, DON, người quản trị được phân quyền | Điểm Morse thô, lịch sử sự cố, độ đầy đủ hồ sơ, hàng đợi rà soát | Xác suất/nguy cơ ngã trong 7–30 ngày, nhãn nguy cơ lâm sàng chưa kiểm định |
| F9 | Nurse, DON, dược sĩ/bác sĩ khi có vai trò được thiết lập | Đơn thuốc, dị ứng, chẩn đoán trong hồ sơ, câu hỏi đối chiếu và trạng thái chưa kiểm tra tương tác | Kết luận an toàn để cấp phát, liều dùng, mức độ tương tác không có nguồn được phê duyệt |
| F1–F3, F5–F8, F10 | Nhân viên được phân quyền | Bản nháp, tóm tắt, tìm kiếm có nguồn; nhân viên xác nhận trước khi ghi hồ sơ | AI tự đổi y lệnh, kế hoạch điều trị, cửa ra vào hoặc công bố bản tin cho gia đình |
| W1–W6 | Khách truy cập | Thông tin dịch vụ và giáo dục chung | Chẩn đoán cá nhân, tư vấn thuốc, tiết lộ hồ sơ cư dân |

Phân loại pháp lý của từng chức năng cần chuyên gia pháp lý/quản lý chất lượng đánh giá theo mục đích sử dụng thực tế. Không suy luận rằng sản phẩm đã được FDA miễn trừ hay được HIPAA chứng nhận. Theo [hướng dẫn FDA về CDS (01/2026)](https://www.fda.gov/regulatory-information/search-fda-guidance-documents/clinical-decision-support-software), nhân viên y tế cần có thể tự xem cơ sở của khuyến nghị, gồm đầu vào, phương pháp, dữ liệu kiểm định và điểm chưa biết.

Bản Morse của [AHRQ](https://www.ahrq.gov/sites/default/files/publications/files/fallpxtoolkit.pdf) nằm trong bộ công cụ phòng ngã tại bệnh viện; cơ sở dưỡng lão phải xác nhận tính phù hợp với dân số và quy trình của mình. Nếu cơ sở theo chương trình công nhận Joint Commission, phải xác định bộ [National Performance Goals áp dụng từ 2026](https://www.jointcommission.org/en-us/standards/national-patient-safety-goals) cho loại hình cơ sở và đối chiếu với quy định tiểu bang; tài liệu này không tự xác nhận đạt công nhận.

## 2. Trách nhiệm và điều kiện mở khóa

| Vai trò | Trách nhiệm cần xác nhận bằng văn bản |
| --- | --- |
| Medical director / DON | Chốt mục đích sử dụng, quy trình sàng lọc té ngã, ngưỡng tại cơ sở, hành động sau cảnh báo, điều kiện dừng tính năng |
| Dược sĩ hoặc bác sĩ được ủy quyền | Chọn nguồn tương tác thuốc, duyệt ánh xạ sản phẩm/hoạt chất, kiểm tra cảnh báo và quyết định xử trí |
| Clinical informatics / QA | Định nghĩa ca kiểm thử, kiểm định trên dữ liệu cơ sở, theo dõi sai lệch và sự cố sau triển khai |
| Privacy / security | Đánh giá luồng ePHI, phân quyền, lưu vết, thời hạn lưu, bảo vệ truyền/lưu và BAA nếu có nhà cung cấp ngoài |
| Engineering | Kiểm thử, version logic/dữ liệu, kiểm soát thay đổi, cảnh báo lỗi, nút tắt tính năng |

Chỉ chuyển một tính năng sang hỗ trợ quyết định lâm sàng khi đủ tài liệu và chữ ký phê duyệt. [SAFER Organizational Responsibilities 2025](https://healthit.gov/wp-content/uploads/2025/06/SAFER-Guide-5.-Organizational-Responsibilities-Final.pdf) khuyến nghị đánh giá hiệu năng AI trên dữ liệu địa phương trước và sau khi dùng thường quy, cùng quy trình tắt khi có vấn đề. [HIPAA Security Rule](https://www.hhs.gov/hipaa/for-professionals/security/laws-regulations/index.html) yêu cầu kiểm soát truy cập và cơ chế ghi/xem hoạt động đối với ePHI ở các đơn vị thuộc phạm vi áp dụng.

## 3. Giai đoạn A — cơ sở dữ liệu và khả năng truy nguyên (bắt đầu ngay)

1. Mỗi phản hồi F4/F9 ghi thời điểm tính, phiên bản logic, ID bản ghi nguồn, độ đầy đủ và nguồn tham chiếu đang dùng. Không cho danh sách trống ngầm mang nghĩa “không dị ứng” hoặc “không có tương tác”.
2. F4 chỉ lấy cư dân `ACTIVE`, giữ điểm Morse sáu mục và các mục gốc; điểm sự cố là chỉ số ưu tiên rà soát, không phải xác suất. Đánh dấu rõ đánh giá ADL chưa xác nhận, thiếu Morse hoặc thiếu đánh giá.
3. F9 lấy đơn `ACTIVE`, dị ứng/chẩn đoán thực có trong hồ sơ và thuốc MAR nhập thủ công; phân biệt dữ liệu hồ sơ với dữ liệu nhập tay. Nếu không có cơ sở tương tác được phê duyệt, trả về `NOT_CONNECTED` và `REVIEW_REQUIRED` bất kể LLM nói gì.
4. Kiểm thử phân quyền, chart lock, bản ghi thiếu/cũ, trùng thuốc, tên dị ứng khớp chính xác, và việc dữ liệu người dùng truyền lên không thể giả mạo dị ứng/chẩn đoán trong hồ sơ.

**Nghiệm thu A:** mỗi đầu ra cho phép nhân viên truy về đúng hồ sơ nguồn và thấy rõ dữ liệu thiếu; 100% ca không có nguồn tương tác không được tự xác nhận dùng thuốc; test backend/frontend đạt. Đây là tiêu chí phần mềm, chưa phải độ chính xác lâm sàng.

## 4. Giai đoạn B — F4: kiểm định sàng lọc và dự báo

1. DON ban hành quy trình đánh giá Morse: thời điểm nhập viện, sau sự cố/thay đổi tình trạng và tần suất tái đánh giá. Sáu mục phải có thời điểm, người đánh giá, phiên bản thang và nguyên nhân sửa nếu có. Bảng AHRQ cung cấp cách tính nhưng lưu ý ngưỡng cần phù hợp từng cơ sở: [AHRQ Morse Fall Scale](https://www.ahrq.gov/sites/default/files/publications/files/fallpxtoolkit.pdf).
2. Thu thập dữ liệu dọc có cấu trúc: resident ID, thời điểm đánh giá, sáu mục, ADL/di chuyển nếu được xác nhận, thuốc liên quan khi có mã chuẩn, ngày/ngữ cảnh sự cố ngã; đánh dấu ra viện/chuyển viện và thời gian không theo dõi. Kiểm tra trùng sự cố, thời gian sai, hồ sơ bị khóa và thiếu dữ liệu.
3. Đóng băng định nghĩa nhãn trước khi nghiên cứu: ngã trong 7 ngày và 30 ngày sau thời điểm đánh giá; tách dữ liệu theo thời gian và theo cư dân để tránh rò rỉ. Không dùng dữ liệu xảy ra sau thời điểm dự báo làm đặc trưng đầu vào.
4. So sánh thang Morse với mô hình cơ sở đơn giản trước khi dùng ML. Báo cáo số lượng mẫu/sự kiện, sensitivity, specificity, PPV, calibration và hiệu năng theo nhóm tuổi, giới, đơn vị chăm sóc; có khoảng tin cậy. Nhóm lâm sàng chốt ngưỡng và mức chấp nhận trước khi xem tập kiểm định.
5. Chạy im lặng trên dữ liệu thực, đối chiếu với nhân viên, ghi nhận cảnh báo sai/bỏ sót, và chỉ mở nhãn dự báo sau khi được phê duyệt. Theo dõi drift và tái kiểm định khi quy trình/dân số thay đổi.

**Nghiệm thu B:** bộ dữ liệu, protocol, mã/phép tính, kết quả kiểm định độc lập, ngưỡng địa phương, biên bản DON/medical director và kế hoạch giám sát được lưu cùng phiên bản. Hiện chưa đủ dữ liệu để đạt bước này.

## 5. Giai đoạn C — F9: nguồn thuốc và xử trí cảnh báo

1. Cơ sở chỉ định một nguồn tương tác thuốc có quyền sử dụng phù hợp, lịch cập nhật, API/dataset, tài liệu mức độ bằng chứng và người chịu trách nhiệm. DailyMed chỉ là liên kết để tìm nhãn sản phẩm; [openFDA cảnh báo không dựa vào API nhãn để quyết định chăm sóc](https://open.fda.gov/apis/drug/label/understanding-the-api-results/).
2. Chuẩn hóa định danh thuốc theo hoạt chất, sản phẩm, hàm lượng, dạng bào chế, đường dùng; đối chiếu đơn hiện hành với MAR và thuốc tự dùng/PRN. Chuẩn hóa dị ứng theo chất và phản ứng; xác minh dữ liệu thiếu với người bệnh/hồ sơ và nhân viên.
3. Thiết kế hợp đồng bộ kiểm tra tương tác: ID nguồn, phiên bản/ngày cập nhật, cặp thuốc, mức độ và cơ chế có trích dẫn, áp dụng theo đường dùng/liều khi nguồn hỗ trợ, các trường hợp không ánh xạ được. `UNKNOWN` không được chuyển thành `SAFE`.
4. Trong luồng y lệnh/eMAR khi module đó được xây dựng, hiển thị cảnh báo trước lưu/cấp phát. Dược sĩ hoặc bác sĩ xử trí: xác nhận, chỉnh y lệnh, hoặc ghi lý do override theo chính sách; hệ thống lưu người/thời điểm/nguồn. Tập ca kiểm thử phải do người lâm sàng duyệt và bao gồm false positive/false negative.
5. Giám sát cảnh báo, override, lỗi mapping, mất kết nối và thay đổi phiên bản nguồn; khi nguồn hỏng, quay về `REVIEW_REQUIRED` và quy trình đối chiếu thủ công.

**Nghiệm thu C:** nguồn và hợp đồng sử dụng được phê duyệt, danh mục thuốc địa phương ánh xạ đạt ngưỡng do dược sĩ định trước, tập ca kiểm thử đạt ngưỡng định trước, có quy trình xử trí/audit và ký duyệt. Repository hiện chưa có giao diện tạo/sửa y lệnh; không thể tuyên bố đã chặn cấp phát tại điểm nhập lệnh.

## 6. Giai đoạn D — vận hành an toàn cho toàn bộ AI

| Tính năng | Rủi ro chính cần kiểm thử | Điều kiện vận hành |
| --- | --- | --- |
| F1 chat nhân viên | Nội dung bịa, lộ hồ sơ qua RAG | Phạm vi quyền theo vai trò, nguồn truy xuất hiển thị, không tự ghi y lệnh |
| F2 sự cố | Phân loại sai mức độ hoặc bỏ qua khẩn cấp | Nhân viên soát mô tả và mức độ trước khi lưu; kiểm thử ca báo cáo thiếu/mâu thuẫn |
| F3 kế hoạch chăm sóc | Mục tiêu không phù hợp cư dân | Chỉ bản nháp `PENDING_REVIEW`; nurse/DON duyệt theo quy trình cơ sở |
| F4 té ngã | Diễn giải điểm sàng lọc thành dự báo | Thang/đầu vào/nguồn rõ ràng; không bật dự báo khi chưa đạt giai đoạn B |
| F5 giao ca | Tóm tắt bỏ sót hoặc bịa sự kiện | Liệt kê nguồn, người giao ca duyệt và sửa trước sử dụng |
| F6 tìm kiếm | Tài liệu cũ, vượt quyền, prompt injection | Phân quyền từng tài liệu, ngày cập nhật, trích nguồn, thử truy vấn tấn công |
| F7 giọng nói | Nhận dạng sai sinh hiệu/đối tượng | Xác nhận từng trường và đúng cư dân trước khi ghi; không tự ghi eMAR |
| F8 đi lạc | Cảnh báo sai hoặc tự kích hoạt cửa | Chỉ ghi nhận hành vi quan sát; nhân viên quyết định theo chính sách an ninh |
| F9 thuốc | Bỏ sót tương tác/dị ứng hoặc yên tâm giả | `REVIEW_REQUIRED` khi thiếu nguồn; dược sĩ/bác sĩ xử trí theo giai đoạn C |
| F10 bản tin gia đình | Lộ ePHI hoặc thuật lại sai | Chỉ bản nháp admin, kiểm duyệt nội dung và quyền người nhận trước bất kỳ phát hành nào |
| W1–W6 công khai | Tư vấn y khoa cá nhân hoặc lộ thông tin cư dân | Chỉ giáo dục/dịch vụ chung, không dùng hồ sơ nội bộ, đánh giá câu trả lời nguy cơ cao |

- Phân quyền tối thiểu, hạn chế ePHI trong prompt/log/vector, xác thực nguồn tài liệu, chống prompt injection, giới hạn lưu dữ liệu; đánh giá BAA trước khi đưa ePHI lên dịch vụ ngoài.
- Mỗi bản nháp/khuyến nghị có nhãn AI, nguồn, phiên bản và người xác nhận; audit các thao tác đọc, tạo, sửa, chấp nhận/loại bỏ theo đánh giá rủi ro của cơ sở.
- Kiểm thử hồi quy các luồng F1–F10, W1–W6 với dữ liệu thiếu, xung đột, lỗi Ollama/SQL và phân quyền; thử người dùng thực tế, ghi nhận sự cố và có công tắc tắt tính năng.
- Đánh giá lại sau thay đổi mô hình, prompt, nguồn thuốc, thang điểm hoặc population; không dùng dữ liệu demo làm bằng chứng hiệu quả lâm sàng.

## 7. Thứ tự triển khai và trạng thái

| Bước | Phụ thuộc | Trạng thái ban đầu |
| --- | --- | --- |
| A1. Truy nguyên F4/F9, hiển thị dữ liệu thiếu và lấy vai trò đăng nhập thật | Mã và DB hiện có | Đã triển khai; 277 test backend và build frontend đạt |
| A2. Kiểm thử API/UI/SQL Server, kiểm tra phân quyền | Docker + dữ liệu thử | Chưa chạy lại sau A1; Docker Desktop hiện không khởi động được |
| B1. Quy trình Morse và thu thập dữ liệu dọc | DON/medical director | Chờ quy trình cơ sở |
| B2. Protocol, kiểm định và pilot im lặng | B1 + đủ dữ liệu | Chờ dữ liệu |
| C1. Nguồn tương tác và người ký duyệt | Quyết định của cơ sở | Chờ xác nhận |
| C2. Tích hợp, kiểm thử lâm sàng, audit xử trí | C1 + luồng y lệnh/eMAR | Chưa thực hiện |
| D1. Đánh giá HIPAA/SAFER và phát hành có kiểm soát | A–C + QA/privacy | Chưa thực hiện |
