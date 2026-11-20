package com.eldercare.companion.localization

enum class AppLanguage(val code: String, val label: String, val flag: String) {
    VIETNAMESE("vi", "Tiếng Việt", "🇻🇳"),
    ENGLISH("en", "English", "🇬🇧")
}

object AppStrings {

    fun appName(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "NHMS CareCompanion"
        AppLanguage.ENGLISH -> "NHMS CareCompanion"
    }

    // Tabs
    fun tabHome(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Trang chủ"
        AppLanguage.ENGLISH -> "Home"
    }
    fun tabActivities(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Hoạt động"
        AppLanguage.ENGLISH -> "Activities"
    }
    fun tabTasks(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Nhiệm vụ"
        AppLanguage.ENGLISH -> "Tasks"
    }
    fun tabNotifications(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Thông báo"
        AppLanguage.ENGLISH -> "Alerts"
    }
    fun tabAccount(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Tài khoản"
        AppLanguage.ENGLISH -> "Account"
    }

    // Login
    fun loginSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Trợ lý lâm sàng số cho Điều dưỡng & CNA"
        AppLanguage.ENGLISH -> "Digital Clinical Companion for Nurses & CNAs"
    }
    fun username(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Tên đăng nhập / Email"
        AppLanguage.ENGLISH -> "Username / Email"
    }
    fun password(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Mật khẩu"
        AppLanguage.ENGLISH -> "Password"
    }
    fun signIn(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Đăng nhập ca trực"
        AppLanguage.ENGLISH -> "Sign in to Shift"
    }
    fun serverConfig(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Cấu hình địa chỉ máy chủ"
        AppLanguage.ENGLISH -> "Server Configuration"
    }

    // Shift Info
    fun currentShift(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Ca Sáng: 06:00 - 14:00"
        AppLanguage.ENGLISH -> "Morning Shift: 06:00 - 14:00"
    }
    fun shiftProgress(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Tiến độ hoàn thành ca trực"
        AppLanguage.ENGLISH -> "Shift Completion Progress"
    }
    fun tasksCompleted(lang: AppLanguage, completed: Int, total: Int) = when (lang) {
        AppLanguage.VIETNAMESE -> "Đã hoàn thành $completed/$total đầu việc chăm sóc"
        AppLanguage.ENGLISH -> "Completed $completed of $total care tasks"
    }

    // Task Filters
    fun filterPending(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Cần làm"
        AppLanguage.ENGLISH -> "Pending"
    }
    fun filterCompleted(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Đã xong"
        AppLanguage.ENGLISH -> "Completed"
    }
    fun filterAll(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Tất cả"
        AppLanguage.ENGLISH -> "All Tasks"
    }

    // Task Card
    fun confirmCare(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Xác nhận đã chăm sóc"
        AppLanguage.ENGLISH -> "Confirm Care Completed"
    }
    fun careCompletedBadge(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Đã chăm sóc"
        AppLanguage.ENGLISH -> "Care Completed"
    }
    fun flagAbnormal(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Gắn cờ bất thường"
        AppLanguage.ENGLISH -> "Flag Abnormal"
    }
    fun abnormalFlaggedBadge(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "CẢNH BÁO BẤT THƯỜNG"
        AppLanguage.ENGLISH -> "ABNORMAL FLAGGED"
    }
    fun room(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Phòng"
        AppLanguage.ENGLISH -> "Room"
    }
    fun bed(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Giường"
        AppLanguage.ENGLISH -> "Bed"
    }
    fun emptyPendingTasks(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Tuyệt vời! Ca trực không còn nhiệm vụ tồn đọng."
        AppLanguage.ENGLISH -> "Great job! No pending tasks remaining in this shift."
    }

    // Home Screen Cards
    fun highFallRiskAlertTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Cảnh báo Nguy cơ Ngã (DFRS Cao)"
        AppLanguage.ENGLISH -> "Fall Risk Warnings (High DFRS)"
    }
    fun highFallRiskAlertDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Có 2 cư dân cần kích hoạt cảm biến giường ca đêm"
        AppLanguage.ENGLISH -> "2 residents require bed sensor pads & close monitoring"
    }
    fun latestSbarHandoverTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Biên bản giao ban SBAR gần nhất"
        AppLanguage.ENGLISH -> "Latest SBAR Clinical Handover"
    }
    fun latestSbarHandoverDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Ca Đêm bàn giao: Cụ phòng 104 có dấu hiệu sốt nhẹ lúc 04:00"
        AppLanguage.ENGLISH -> "Night shift handover: Room 104 showed mild fever at 04:00"
    }
    fun quickJumpTasks(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Xem danh sách việc cần làm ngay"
        AppLanguage.ENGLISH -> "Jump to Pending Care Tasks"
    }

    // Activities Screen
    fun activitiesTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Lịch Sinh Hoạt Thường Nhật (ADL)"
        AppLanguage.ENGLISH -> "Daily Living Schedule (ADL)"
    }
    fun activitiesSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Lịch trình trị liệu, dinh dưỡng và giao lưu tập thể"
        AppLanguage.ENGLISH -> "Scheduled therapy, nutrition, and communal recreation"
    }

    // Notifications Screen
    fun alertsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Trung Tâm Cảnh Báo & Báo Động"
        AppLanguage.ENGLISH -> "Alerts & Notifications Hub"
    }
    fun alertsSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Báo động khẩn cấp từ giường và nhắc việc ca trực"
        AppLanguage.ENGLISH -> "Emergency bedside alarms and critical shift updates"
    }
    fun clearAll(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Đã xem tất cả"
        AppLanguage.ENGLISH -> "Mark All Read"
    }

    // Account Screen
    fun accountTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Hồ Sơ Nhân Viên & Cài Đặt"
        AppLanguage.ENGLISH -> "Staff Profile & Settings"
    }
    fun staffRole(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Vai trò y tế:"
        AppLanguage.ENGLISH -> "Clinical Role:"
    }
    fun staffId(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Mã nhân viên:"
        AppLanguage.ENGLISH -> "Staff ID:"
    }
    fun appLanguageLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Ngôn ngữ hiển thị (Language)"
        AppLanguage.ENGLISH -> "Display Language"
    }
    fun connectionStatus(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Trạng thái máy chủ:"
        AppLanguage.ENGLISH -> "Server Status:"
    }
    fun onlineStatus(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Trực tuyến (Online)"
        AppLanguage.ENGLISH -> "Online & Synchronized"
    }
    fun endShiftLogout(lang: AppLanguage) = when (lang) {
        AppLanguage.VIETNAMESE -> "Kết thúc ca trực & Đăng xuất"
        AppLanguage.ENGLISH -> "End Shift & Sign Out"
    }
}
