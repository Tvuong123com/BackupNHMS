package com.eldercare.modules.ai.risk.service;

import com.eldercare.modules.ai.risk.entity.FallScreeningEntity;
import com.eldercare.modules.ai.risk.repository.FallScreeningRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

/** Records the six Morse Fall Scale items without interpreting a facility-specific cutoff. */
@Service
@RequiredArgsConstructor
public class FallScreeningService {
    private final FallScreeningRepository screenings;
    private final ResidentRepository residents;

    public record ScreeningRequest(Long residentId, Integer historyOfFalling,
                                   Integer secondaryDiagnosis, Integer ambulatoryAid,
                                   Integer ivTherapy, Integer gait, Integer mentalStatus) {}
    public record ScreeningResult(Long id, Long residentId, int totalScore,
                                  OffsetDateTime assessedAt, String assessedBy,
                                  String interpretation, String scaleName,
                                  String sourceUrl) {}
    public record LatestResponse(ScreeningResult screening) {}

    @Transactional
    public ScreeningResult record(ScreeningRequest request, String staffEmail) {
        if (request == null || request.residentId() == null) {
            throw new IllegalArgumentException("Resident is required");
        }
        var resident = residents.findByIdAndIsDeletedFalse(request.residentId())
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));
        if (resident.isChartLocked()) throw new IllegalStateException("Resident chart is locked");
        requireOption(request.historyOfFalling(), Set.of(0, 25), "History of falling");
        requireOption(request.secondaryDiagnosis(), Set.of(0, 15), "Secondary diagnosis");
        requireOption(request.ambulatoryAid(), Set.of(0, 15, 30), "Ambulatory aid");
        requireOption(request.ivTherapy(), Set.of(0, 20), "IV therapy");
        requireOption(request.gait(), Set.of(0, 10, 20), "Gait");
        requireOption(request.mentalStatus(), Set.of(0, 15), "Mental status");
        var row = new FallScreeningEntity();
        row.setResidentId(request.residentId());
        row.setAssessedBy(staffEmail);
        row.setHistoryOfFalling(request.historyOfFalling());
        row.setSecondaryDiagnosis(request.secondaryDiagnosis());
        row.setAmbulatoryAid(request.ambulatoryAid());
        row.setIvTherapy(request.ivTherapy());
        row.setGait(request.gait());
        row.setMentalStatus(request.mentalStatus());
        row.setTotalScore(request.historyOfFalling() + request.secondaryDiagnosis()
                + request.ambulatoryAid() + request.ivTherapy() + request.gait()
                + request.mentalStatus());
        row.setAssessedAt(OffsetDateTime.now());
        return toResult(screenings.save(row));
    }

    @Transactional(readOnly = true)
    public LatestResponse latest(Long residentId) {
        if (residentId == null || !residents.existsByIdAndIsDeletedFalse(residentId)) {
            throw new IllegalArgumentException("Resident not found");
        }
        return new LatestResponse(screenings.findFirstByResidentIdOrderByIdDesc(residentId)
                .map(this::toResult).orElse(null));
    }

    private static void requireOption(Integer value, Set<Integer> allowed, String label) {
        if (value == null || !allowed.contains(value)) {
            throw new IllegalArgumentException(label + " has an invalid score");
        }
    }

    private ScreeningResult toResult(FallScreeningEntity row) {
        return new ScreeningResult(row.getId(), row.getResidentId(), row.getTotalScore(),
                row.getAssessedAt(), row.getAssessedBy(),
                "Raw Morse Fall Scale score; apply the facility's approved assessment policy and clinical review.",
                "Morse Fall Scale (six items)",
                "https://www.ahrq.gov/sites/default/files/publications/files/fallpxtoolkit.pdf");
    }
}
