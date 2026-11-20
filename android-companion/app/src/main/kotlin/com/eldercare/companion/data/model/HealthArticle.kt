package com.eldercare.companion.data.model

import androidx.annotation.DrawableRes
import com.eldercare.companion.R
import com.eldercare.companion.localization.AppLanguage

data class HealthArticle(
    val id: String,
    val titleVi: String,
    val titleEn: String,
    val summaryVi: String,
    val summaryEn: String,
    val contentVi: String,
    val contentEn: String,
    @DrawableRes val imageRes: Int,
    val author: String,
    val date: String,
    val categoryVi: String,
    val categoryEn: String
) {
    fun getTitle(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) titleVi else titleEn
    fun getSummary(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) summaryVi else summaryEn
    fun getContent(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) contentVi else contentEn
    fun getCategory(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) categoryVi else categoryEn

    companion object {
        fun getSampleArticles(): List<HealthArticle> = listOf(
            HealthArticle(
                id = "art-1",
                titleVi = "Kích hoạt cảm biến giường và phòng ngừa té ngã ca đêm",
                titleEn = "Activating Bed Sensors & Night-Shift Fall Prevention",
                summaryVi = "Hướng dẫn quy trình kích hoạt thảm áp lực không dây và rà soát môi trường buồng bệnh người cao tuổi.",
                summaryEn = "Step-by-step protocol for enabling wireless pressure bed mats and nighttime environmental checks.",
                contentVi = """
                    Trong môi trường viện dưỡng lão, hơn 60% các vụ té ngã xảy ra vào khoảng thời gian từ 22:00 đến 06:00 sáng khi người cao tuổi tự ý rời giường đi vệ sinh.
                    
                    Quy trình chuẩn bị ca đêm cho điều dưỡng viên:
                    1. Kiểm tra bề mặt và nguồn pin của cảm biến giường áp lực (Bed Sensor Pad).
                    2. Duy trì đèn ngủ ánh sáng vàng dịu (20-30 lux) và dọn sạch thảm trơn, dép không chống trượt trên lối đi.
                    3. Kéo thanh chắn giường 2 bên đúng chuẩn lâm sàng.
                    4. Đặt chuông gọi y tá (Call Bell) trong tầm với tay thuận của cụ.
                    5. Đối với cư dân có thang điểm DFRS > 6, lập lịch rà soát buồng bệnh định kỳ mỗi 60 phút.
                """.trimIndent(),
                contentEn = """
                    In eldercare facilities, over 60% of fall incidents occur between 22:00 and 06:00 when residents attempt unassisted nocturnal bathroom transfers.
                    
                    Night-shift nursing protocol:
                    1. Inspect surface and battery levels of wireless pressure bed pads.
                    2. Maintain soft warm night lighting (20-30 lux) and remove rugs or non-skid slippers from walking paths.
                    3. Position dual bed safety rails according to clinical standards.
                    4. Ensure nurse call bell is firmly within reach of the resident's dominant hand.
                    5. For residents with DFRS score > 6, schedule bedside checks every 60 minutes.
                """.trimIndent(),
                imageRes = R.drawable.img_care_walking,
                author = "ThS. BS. Nguyễn Hoài Nam",
                date = "19/11/2026",
                categoryVi = "An toàn té ngã",
                categoryEn = "Fall Safety"
            ),
            HealthArticle(
                id = "art-2",
                titleVi = "Rà soát thuốc FRID và giảm liều an thần theo tiêu chuẩn Beers",
                titleEn = "Reviewing FRID Medications & Sedative Deprescribing",
                summaryVi = "Nhận diện các nhóm thuốc gây nguy cơ té ngã cao (FRIDs) và hướng dẫn cảnh báo dược lâm sàng.",
                summaryEn = "Identifying Fall-Risk Increasing Drugs and clinical pharmacy safety alerts under Beers Criteria.",
                contentVi = """
                    Thuốc tăng nguy cơ ngã (FRIDs - Fall-Risk Increasing Drugs) bao gồm thuốc an thần nhóm Benzodiazepine, thuốc hạ áp, lợi tiểu và thuốc chống loạn thần.
                    
                    Khuyến cáo dành cho điều dưỡng viên khi cấp phát thuốc:
                    1. Kiểm tra huyết áp tư thế trước khi cấp thuốc giãn mạch hoặc lợi tiểu liều cao.
                    2. Theo dõi sát biểu hiện lú lẫn, lơ mơ hoặc dáng đi lảo đảo trong vòng 2 giờ sau khi dùng thuốc ngủ.
                    3. Ghi nhận ngay trên phần mềm CareCompanion khi cụ có biểu hiện hoa mắt chóng mặt tư thế đứng (Orthostatic Hypotension).
                    4. Đề xuất bác sĩ điều trị rà soát đơn thuốc theo tiêu chuẩn AGS Beers Criteria 2023.
                """.trimIndent(),
                contentEn = """
                    Fall-Risk Increasing Drugs (FRIDs) encompass Benzodiazepines, antihypertensives, loop diuretics, and antipsychotics.
                    
                    Bedside nursing recommendations during medication administration:
                    1. Check postural blood pressure prior to giving potent vasodilators or high-dose diuretics.
                    2. Closely monitor for somnolence, confusion, or unsteady gait within 2 hours post-sedation.
                    3. Promptly flag orthostatic dizziness symptoms via CareCompanion mobile chart.
                    4. Suggest clinical pharmacy review following AGS Beers Criteria 2023 guidelines.
                """.trimIndent(),
                imageRes = R.drawable.img_doctor_consult,
                author = "DS. Lâm Thu Hà",
                date = "18/11/2026",
                categoryVi = "Dược lâm sàng",
                categoryEn = "Pharmacotherapy"
            ),
            HealthArticle(
                id = "art-3",
                titleVi = "Chế độ dinh dưỡng chống nghẹn sặc và bù nước cho người cao tuổi",
                titleEn = "Dysphagia Management & Geriatric Hydration Protocols",
                summaryVi = "Quy chuẩn chế biến thức ăn xay nhuyễn IDDSI và giải pháp bù nước khoa học chống kiệt sức.",
                summaryEn = "IDDSI pureed diet standards and scientific hydration protocols for frail elderly residents.",
                contentVi = """
                    Rối loạn nuốt (Dysphagia) là nguyên nhân hàng đầu dẫn đến viêm phổi hít sặc ở người cao tuổi. Chăm sóc dinh dưỡng đòi hỏi sự phối hợp nghiêm ngặt giữa điều dưỡng và bếp ăn.
                    
                    Các nguyên tắc vàng khi cho ăn tại giường:
                    1. Đặt cụ ngồi thẳng góc 90 độ khi ăn và duy trì tư thế ngồi tối thiểu 30 phút sau bữa ăn.
                    2. Kiểm tra độ đặc của cháo súp theo cấp độ IDDSI Level 4 (Pureed) đối với cụ nuốt khó.
                    3. Không ép ăn nhanh, quan sát cử động nuốt hoàn chỉnh trước khi bón thìa tiếp theo.
                    4. Đảm bảo lượng nước tối thiểu 1500ml/ngày chia đều từng ngụm nhỏ nhằm chống hạ natri máu.
                """.trimIndent(),
                contentEn = """
                    Dysphagia is the primary trigger of aspiration pneumonia among elderly care residents. Nutritional management requires rigorous coordination.
                    
                    Key bedside feeding guidelines:
                    1. Position resident upright at 90 degrees during meals and maintain upright posture for at least 30 minutes post-prandial.
                    2. Verify meal texture adheres to IDDSI Level 4 (Pureed) for residents with swallowing difficulty.
                    3. Pace spoon feeding slowly, ensuring complete swallowing before offering the next spoon.
                    4. Maintain minimum daily fluid intake of 1500ml distributed in small sips to prevent dehydration.
                """.trimIndent(),
                imageRes = R.drawable.img_elderly_meal,
                author = "CN. Dinh dưỡng Lê Văn Thịnh",
                date = "17/11/2026",
                categoryVi = "Dinh dưỡng",
                categoryEn = "Nutrition"
            ),
            HealthArticle(
                id = "art-4",
                titleVi = "Không gian sinh thái và hoạt động dưỡng sinh tăng cường thăng bằng",
                titleEn = "Healing Gardens & Balance Therapy for Active Aging",
                summaryVi = "Tác động tích cực của không gian xanh nội viện và các bài tập thái cực quyền dưỡng sinh đối với sa sút trí tuệ.",
                summaryEn = "Positive impacts of therapeutic greenery and Tai Chi balance exercises on dementia and mood.",
                contentVi = """
                    Không gian cảnh quan xanh tại viện dưỡng lão không chỉ nâng cao tinh thần mà còn là phương pháp trị liệu không dùng thuốc (Therapeutic Horticulture).
                    
                    Lợi ích lâm sàng đã được chứng minh:
                    1. Giảm 45% tần suất kích động buổi chiều (Sundowning) ở bệnh nhân Alzheimer.
                    2. Tăng cường khả năng phối hợp thần kinh - cơ bắp thông qua các bài tập bước bậc thang và tư thế đứng thăng bằng.
                    3. Cải thiện nhịp sinh học tự nhiên nhờ tiếp xúc ánh sáng mặt trời sáng sớm, tăng tổng hợp Vitamin D tự nhiên.
                """.trimIndent(),
                contentEn = """
                    Therapeutic outdoor environments in elderly facilities serve as essential non-pharmacological interventions for dementia and emotional wellbeing.
                    
                    Clinically verified outcomes:
                    1. 45% reduction in afternoon sundowning agitation episodes among Alzheimer residents.
                    2. Enhanced neuromuscular coordination through mild stepped walking and balance posture drills.
                    3. Restored circadian sleep rhythm via morning natural sunlight exposure, promoting endogenous Vitamin D synthesis.
                """.trimIndent(),
                imageRes = R.drawable.img_facility_wellness,
                author = "ThS. Trị liệu Đoàn Thanh Vân",
                date = "16/11/2026",
                categoryVi = "Trị liệu tâm lý",
                categoryEn = "Therapy"
            ),
            HealthArticle(
                id = "art-5",
                titleVi = "Cầu nối gia đình: Cập nhật nhật ký chăm sóc người thân từ xa",
                titleEn = "Family Portal: Remote Eldercare Journaling & Trust",
                summaryVi = "Ứng dụng cổng thông tin số giúp thân nhân an tâm theo dõi sức khỏe và hình ảnh sinh hoạt của người thân.",
                summaryEn = "Digital family connectivity tools allowing relatives to transparently track health logs and daily activities.",
                contentVi = """
                    Sự gắn kết gia đình là liều thuốc tinh thần vô giá đối với người cao tuổi sống tại viện dưỡng lão.
                    
                    Hệ thống NHMS Mobile kết nối thân nhân:
                    1. Ghi nhận hình ảnh sinh hoạt, tập luyện và bữa ăn hàng ngày do điều dưỡng viên chụp và gửi duyệt.
                    2. Thông báo minh bạch về tình trạng sức khỏe, chỉ số sinh hiệu và các lần thăm khám định kỳ.
                    3. Tạo lịch hẹn thăm trực tiếp hoặc cuộc gọi video định kỳ giúp người cao tuổi luôn cảm nhận được tình thương yêu.
                """.trimIndent(),
                contentEn = """
                    Family involvement remains an indispensable cornerstone of emotional wellbeing for long-term care residents.
                    
                    NHMS digital family connectivity highlights:
                    1. Bedside photo journaling of recreational moments and mealtime participation curated by care staff.
                    2. Transparent real-time updates regarding vital signs, medication logs, and clinical checkups.
                    3. Easy scheduling of in-person visitation or assisted video calls, preventing social isolation.
                """.trimIndent(),
                imageRes = R.drawable.img_family_visit,
                author = "ĐD. Phạm Thảo Vy",
                date = "15/11/2026",
                categoryVi = "Kết nối gia đình",
                categoryEn = "Family Care"
            ),
            HealthArticle(
                id = "art-6",
                titleVi = "Phục hồi chức năng vận động và bài tập với khung tập đi walker",
                titleEn = "Physical Rehabilitation & Walker Mobility Training",
                summaryVi = "Hướng dẫn chỉnh chiều cao khung walker chuẩn và các bài tập tăng sức mạnh chi dưới sau tai biến.",
                summaryEn = "Standardized walker height adjustments and lower-limb strengthening routines post-stroke.",
                contentVi = """
                    Sử dụng dụng cụ trợ giúp vận động sai quy cách là nguyên nhân tiềm ẩn gây trượt ngã nguy hiểm.
                    
                    Quy chuẩn căn chỉnh khung tập đi (Walker):
                    1. Tay vịn của khung walker phải ngang bằng với nếp gấp cổ tay của cư dân khi đứng thẳng buông thõng tay.
                    2. Góc gập khuỷu tay duy trì lý tưởng từ 20 đến 30 độ khi nắm chặt tay cầm.
                    3. Luôn kiểm tra 4 nút đệm cao su dưới chân khung để tránh trượt trên sàn gạch men.
                    4. Hướng dẫn cư dân luôn bước chân yếu vào giữa khung trước, sau đó mới bước chân khỏe.
                """.trimIndent(),
                contentEn = """
                    Incorrect usage of assistive walking devices presents a frequent yet preventable source of balance loss and fall injuries.
                    
                    Standardized walker configuration guidelines:
                    1. Walker handgrips must align with the resident's wrist crease when standing upright with relaxed arms.
                    2. Maintain elbow flexion between 20 to 30 degrees while holding handgrips securely.
                    3. Inspect all four rubber tip ferrules to prevent slipping on smooth vinyl or tile flooring.
                    4. Instruct resident to step forward with affected limb first into the center of the frame, followed by the stronger limb.
                """.trimIndent(),
                imageRes = R.drawable.img_rehab_exercise,
                author = "KTV. Phục hồi chức năng Vũ Đức Trọng",
                date = "14/11/2026",
                categoryVi = "Phục hồi chức năng",
                categoryEn = "Rehabilitation"
            )
        )
    }
}
