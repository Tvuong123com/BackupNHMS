package com.eldercare.modules.ai.website.service;

import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelRateEntity;
import com.eldercare.modules.admin.facility_setup.care_level.repository.CareLevelRateRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Read-only, non-resident LOC examples for the public website assistant. */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebsitePricingKnowledge {
    private final CareLevelRateRepository rateRepository;
    private final FacilityRepository facilityRepository;

    @Transactional(readOnly = true)
    public String answer(boolean vietnamese) {
        try {
            LocalDate today = LocalDate.now();
            Map<Long, String> facilities = facilityRepository.findByIsDeletedFalse().stream()
                    .collect(Collectors.toMap(f -> f.getId(), f -> f.getName()));
            List<CareLevelRateEntity> candidates = rateRepository.findAllWithCareLevel().stream()
                    .filter(r -> r.getCareLevel() != null && !Boolean.TRUE.equals(r.getCareLevel().getIsDeleted()))
                    .filter(r -> facilities.containsKey(r.getFacilityId()))
                    .filter(r -> r.getDailyRate() != null && r.getDailyRate().signum() > 0)
                    .filter(r -> r.getEffectiveFrom() != null && !r.getEffectiveFrom().isAfter(today))
                    .toList();
            Map<String, CareLevelRateEntity> latest = new LinkedHashMap<>();
            candidates.stream()
                    .sorted(Comparator.comparing(CareLevelRateEntity::getEffectiveFrom).reversed()
                            .thenComparing(CareLevelRateEntity::getId, Comparator.reverseOrder()))
                    .forEach(r -> latest.putIfAbsent(r.getFacilityId() + ":" + r.getCareLevel().getId(), r));
            if (latest.isEmpty()) return vietnamese
                    ? "Hiện chưa có mức giá LOC đã xác minh trong hệ thống. Vui lòng hỏi bộ phận tiếp nhận để nhận báo giá."
                    : "There are no verified LOC rates in the system. Please ask Admissions for a quote.";

            boolean allCurrent = latest.values().stream().allMatch(r ->
                    r.getEffectiveTo() == null || !r.getEffectiveTo().isBefore(today));
            String rates = latest.values().stream()
                    .sorted(Comparator.comparing((CareLevelRateEntity r) -> r.getCareLevel().getId())
                            .thenComparing(CareLevelRateEntity::getFacilityId))
                    .map(r -> r.getCareLevel().getLevelName() + " (" + facilities.get(r.getFacilityId()) + "): $"
                            + r.getDailyRate().toPlainString() + (vietnamese ? "/ngày" : "/day"))
                    .collect(Collectors.joining("; "));
            if (vietnamese) {
                return (allCurrent ? "Mức LOC đang ghi nhận: " : "Ví dụ giá LOC đang lưu (chưa xác nhận còn hiệu lực): ")
                        + rates + ". Đây là giá theo ngày trong bảng LOC, không phải số tiền người bệnh tự trả hay mức bảo hiểm thanh toán. "
                        + "Giá thực tế tùy đánh giá, dịch vụ và hợp đồng; hãy xác nhận báo giá với bộ phận tiếp nhận.";
            }
            return (allCurrent ? "Recorded LOC daily rates: " : "Stored LOC rate examples (current validity unconfirmed): ")
                    + rates + ". These are daily LOC schedule amounts, not patient out-of-pocket costs or insurance payments. "
                    + "Actual charges depend on assessment, services, and agreement; confirm a quote with Admissions.";
        } catch (RuntimeException e) {
            log.warn("Public LOC rate lookup failed: {}", e.getMessage());
            return vietnamese
                    ? "Hiện không thể xác minh mức giá LOC; vui lòng hỏi bộ phận tiếp nhận để nhận báo giá."
                    : "LOC rates cannot be verified right now; please ask Admissions for a quote.";
        }
    }
}
