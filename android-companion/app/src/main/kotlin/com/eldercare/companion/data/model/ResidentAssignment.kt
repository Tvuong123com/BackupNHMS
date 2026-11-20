package com.eldercare.companion.data.model

import com.eldercare.companion.localization.AppLanguage

data class ResidentAssignment(
    val residentId: Long,
    val fullName: String,
    val room: String,
    val bed: String,
    val levelOfCare: String,
    val fallRiskScore: Int,
    val fallRiskLevel: String,
    val medicalNotesVi: String,
    val medicalNotesEn: String
) {
    fun getDisplayName() = "$fullName ($room-$bed)"
    fun getNotes(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) medicalNotesVi else medicalNotesEn

    companion object {
        fun getSampleAssignments(): List<ResidentAssignment> = listOf(
            ResidentAssignment(
                residentId = 1L,
                fullName = "Cụ Nguyễn Văn An",
                room = "P101",
                bed = "G01",
                levelOfCare = "LOC 3 (Trợ giúp toàn phần)",
                fallRiskScore = 8,
                fallRiskLevel = "HIGH",
                medicalNotesVi = "Tiền sử tai biến mạch máu não, yếu nửa người bên trái. Nguy cơ ngã cao khi thức dậy.",
                medicalNotesEn = "History of stroke, left-side hemiparesis. High fall risk during unassisted transfers."
            ),
            ResidentAssignment(
                residentId = 2L,
                fullName = "Cụ Trần Thị Mai",
                room = "P102",
                bed = "G01",
                levelOfCare = "LOC 2 (Trợ giúp bán phần)",
                fallRiskScore = 6,
                fallRiskLevel = "HIGH",
                medicalNotesVi = "Hội chứng hoàng hôn (Sundowning), hay bồn chồn kích động lúc 16:00 - 18:00.",
                medicalNotesEn = "Sundowning syndrome, agitation during late afternoon shifts. Wandering risk."
            ),
            ResidentAssignment(
                residentId = 3L,
                fullName = "Cụ Lê Văn Ba",
                room = "P103",
                bed = "G02",
                levelOfCare = "LOC 1 (Tự chủ tối đa)",
                fallRiskScore = 3,
                fallRiskLevel = "LOW",
                medicalNotesVi = "Tỉnh táo, đi lại được với gậy 4 chân. Cần nhắc uống thuốc hạ áp đúng giờ.",
                medicalNotesEn = "Alert and oriented, walks with quad cane. Needs blood pressure medication adherence."
            ),
            ResidentAssignment(
                residentId = 4L,
                fullName = "Cụ Phạm Thị Hoa",
                room = "P104",
                bed = "G01",
                levelOfCare = "LOC 2 (Trợ giúp bán phần)",
                fallRiskScore = 5,
                fallRiskLevel = "MEDIUM",
                medicalNotesVi = "Rối loạn nuốt thể nhẹ, ăn cháo mềm IDDSI 4, uống nước từng ngụm nhỏ.",
                medicalNotesEn = "Mild dysphagia, IDDSI Level 4 pureed diet, supervised hydration."
            )
        )
    }
}

data class ActivityScheduleItem(
    val id: String,
    val time: String,
    val timeBlock: TimeBlock,
    val titleVi: String,
    val titleEn: String,
    val residentId: Long?,
    val residentName: String?,
    val location: String,
    val categoryVi: String,
    val categoryEn: String,
    val isCompleted: Boolean = false
) {
    fun getTitle(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) titleVi else titleEn
    fun getCategory(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) categoryVi else categoryEn

    companion object {
        fun getSampleSchedule(): List<ActivityScheduleItem> = listOf(
            // Morning 06:00 - 10:00
            ActivityScheduleItem(
                id = "act-1",
                time = "06:30",
                timeBlock = TimeBlock.MORNING,
                titleVi = "Đo sinh hiệu buổi sáng & ghi nhận SpO2 tại giường",
                titleEn = "Morning Vitals & Bedside SpO2 Check",
                residentId = 1L,
                residentName = "Cụ Nguyễn Văn An (P101)",
                location = "Phòng P101",
                categoryVi = "Sinh hiệu",
                categoryEn = "Vitals",
                isCompleted = true
            ),
            ActivityScheduleItem(
                id = "act-2",
                time = "07:15",
                timeBlock = TimeBlock.MORNING,
                titleVi = "Hỗ trợ vệ sinh răng miệng & thay trang phục cá nhân",
                titleEn = "Oral Hygiene & Dressing Assistance",
                residentId = 2L,
                residentName = "Cụ Trần Thị Mai (P102)",
                location = "Phòng P102",
                categoryVi = "Vệ sinh",
                categoryEn = "Hygiene",
                isCompleted = true
            ),
            ActivityScheduleItem(
                id = "act-3",
                time = "08:00",
                timeBlock = TimeBlock.MORNING,
                titleVi = "Cấp phát thuốc sáng theo đơn eMAR (Amlodipine, Furosemide)",
                titleEn = "Morning eMAR Medication Round (Amlodipine, Furosemide)",
                residentId = 1L,
                residentName = "Cụ Nguyễn Văn An (P101)",
                location = "Phòng P101",
                categoryVi = "Thuốc men",
                categoryEn = "Medication",
                isCompleted = false
            ),
            ActivityScheduleItem(
                id = "act-4",
                time = "08:30",
                timeBlock = TimeBlock.MORNING,
                titleVi = "Phục vụ bữa sáng dinh dưỡng & kiểm tra nuốt sặc",
                titleEn = "Nutritional Breakfast & Dysphagia Supervision",
                residentId = 4L,
                residentName = "Cụ Phạm Thị Hoa (P104)",
                location = "Nhà ăn chung",
                categoryVi = "Dinh dưỡng",
                categoryEn = "Nutrition",
                isCompleted = false
            ),

            // Midday 10:00 - 14:00
            ActivityScheduleItem(
                id = "act-5",
                time = "10:15",
                timeBlock = TimeBlock.MIDDAY,
                titleVi = "Hỗ trợ đi dạo sân vườn sinh thái & hít thở khí trời",
                titleEn = "Healing Garden Walk & Fresh Air Stroll",
                residentId = 3L,
                residentName = "Cụ Lê Văn Ba (P103)",
                location = "Sân vườn trung tâm",
                categoryVi = "Vận động",
                categoryEn = "Mobility",
                isCompleted = false
            ),
            ActivityScheduleItem(
                id = "act-6",
                time = "11:30",
                timeBlock = TimeBlock.MIDDAY,
                titleVi = "Bữa trưa mềm dinh dưỡng & hỗ trợ cụ nuốt khó",
                titleEn = "Soft Diet Lunch & Puree Meal Assistance",
                residentId = 4L,
                residentName = "Cụ Phạm Thị Hoa (P104)",
                location = "Phòng P104",
                categoryVi = "Dinh dưỡng",
                categoryEn = "Nutrition",
                isCompleted = false
            ),
            ActivityScheduleItem(
                id = "act-7",
                time = "12:30",
                timeBlock = TimeBlock.MIDDAY,
                titleVi = "Chuyển tư thế nằm nghiêng 30 độ chống loét tì đè",
                titleEn = "30-Degree Lateral Repositioning for Pressure Injury Prevention",
                residentId = 1L,
                residentName = "Cụ Nguyễn Văn An (P101)",
                location = "Phòng P101",
                categoryVi = "Chống loét",
                categoryEn = "Wound Care",
                isCompleted = false
            ),

            // Evening 14:00 - 18:00
            ActivityScheduleItem(
                id = "act-8",
                time = "15:00",
                timeBlock = TimeBlock.EVENING,
                titleVi = "Hoạt động trị liệu âm nhạc & trò chuyện nhóm phòng ngừa Sundowning",
                titleEn = "Music Therapy & Social Group Discussion (Sundowning Mitigation)",
                residentId = 2L,
                residentName = "Cụ Trần Thị Mai (P102)",
                location = "Phòng sinh hoạt chung",
                categoryVi = "Trị liệu",
                categoryEn = "Therapy",
                isCompleted = false
            ),
            ActivityScheduleItem(
                id = "act-9",
                time = "16:45",
                timeBlock = TimeBlock.EVENING,
                titleVi = "Đo lại huyết áp buổi chiều & đánh giá nguy cơ chóng mặt tư thế",
                titleEn = "Afternoon Blood Pressure Check & Orthostatic Assessment",
                residentId = 3L,
                residentName = "Cụ Lê Văn Ba (P103)",
                location = "Phòng P103",
                categoryVi = "Sinh hiệu",
                categoryEn = "Vitals",
                isCompleted = false
            ),
            ActivityScheduleItem(
                id = "act-10",
                time = "17:30",
                timeBlock = TimeBlock.EVENING,
                titleVi = "Ký biên bản bàn giao ca trực SBAR cho điều dưỡng ca tối",
                titleEn = "Sign SBAR Clinical Shift Handover with Evening Nurse",
                residentId = null,
                residentName = null,
                location = "Trạm điều dưỡng Trực",
                categoryVi = "Giao ban",
                categoryEn = "Handover",
                isCompleted = false
            )
        )
    }
}

enum class TimeBlock(val labelVi: String, val labelEn: String, val hours: String) {
    MORNING("Ca Sáng", "Morning", "06:00 - 10:00"),
    MIDDAY("Ca Trưa", "Midday", "10:00 - 14:00"),
    EVENING("Ca Chiều", "Evening", "14:00 - 18:00");

    fun getLabel(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) labelVi else labelEn
}
