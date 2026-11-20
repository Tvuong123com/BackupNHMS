package com.eldercare.modules.ai.website;

import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelEntity;
import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelRateEntity;
import com.eldercare.modules.admin.facility_setup.care_level.repository.CareLevelRateRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import com.eldercare.modules.ai.website.service.WebsitePricingKnowledge;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebsitePricingKnowledgeTest {
    @Test
    void outdatedScheduleIsShownOnlyAsUnconfirmedExample() {
        CareLevelRateRepository rates = mock(CareLevelRateRepository.class);
        FacilityRepository facilities = mock(FacilityRepository.class);
        CareLevelEntity nursing = CareLevelEntity.builder()
                .id(4L).levelName("Skilled Nursing Care").isDeleted(false).build();
        CareLevelRateEntity historical = CareLevelRateEntity.builder()
                .id(8L).careLevel(nursing).facilityId(1L)
                .dailyRate(new BigDecimal("450.00"))
                .effectiveFrom(LocalDate.of(2025, 1, 1))
                .effectiveTo(LocalDate.of(2024, 12, 31)).build();
        when(rates.findAllWithCareLevel()).thenReturn(List.of(historical));
        when(facilities.findByIsDeletedFalse()).thenReturn(List.of(
                FacilityEntity.builder().id(1L).name("Demo Facility").build()));

        String answer = new WebsitePricingKnowledge(rates, facilities).answer(true);

        assertTrue(answer.contains("$450.00/ngày"));
        assertTrue(answer.contains("chưa xác nhận còn hiệu lực"));
        assertFalse(answer.contains("Mức LOC đang ghi nhận"));
    }

    @Test
    void currentRateIsIdentifiedAsRecordedRate() {
        CareLevelRateRepository rates = mock(CareLevelRateRepository.class);
        FacilityRepository facilities = mock(FacilityRepository.class);
        CareLevelEntity nursing = CareLevelEntity.builder()
                .id(4L).levelName("Skilled Nursing Care").isDeleted(false).build();
        CareLevelRateEntity current = CareLevelRateEntity.builder()
                .id(10L).careLevel(nursing).facilityId(1L)
                .dailyRate(new BigDecimal("475.00"))
                .effectiveFrom(LocalDate.now().minusDays(1)).build();
        when(rates.findAllWithCareLevel()).thenReturn(List.of(current));
        when(facilities.findByIsDeletedFalse()).thenReturn(List.of(
                FacilityEntity.builder().id(1L).name("Demo Facility").build()));

        String answer = new WebsitePricingKnowledge(rates, facilities).answer(false);

        assertTrue(answer.contains("Recorded LOC daily rates"));
        assertTrue(answer.contains("$475.00/day"));
    }
}
