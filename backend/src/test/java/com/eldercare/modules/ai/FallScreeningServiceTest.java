package com.eldercare.modules.ai;

import com.eldercare.modules.ai.risk.entity.FallScreeningEntity;
import com.eldercare.modules.ai.risk.repository.FallScreeningRepository;
import com.eldercare.modules.ai.risk.service.FallScreeningService;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FallScreeningServiceTest {
    private final FallScreeningRepository screenings = mock(FallScreeningRepository.class);
    private final ResidentRepository residents = mock(ResidentRepository.class);
    private final FallScreeningService service = new FallScreeningService(screenings, residents);

    @Test
    void recordsAllSixMorseItemsAndRecalculatesTotalServerSide() {
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).build()));
        when(screenings.save(any())).thenAnswer(call -> {
            FallScreeningEntity row = call.getArgument(0);
            row.setId(2L);
            return row;
        });
        var request = new FallScreeningService.ScreeningRequest(7L, 25, 15, 30, 20, 10, 15);

        var result = service.record(request, "nurse@example.test");

        assertEquals(115, result.totalScore());
        assertEquals("nurse@example.test", result.assessedBy());
        assertTrue(result.interpretation().contains("facility's approved"));
        assertEquals("Morse Fall Scale (six items)", result.scaleName());
        assertTrue(result.sourceUrl().startsWith("https://www.ahrq.gov/"));
        verify(screenings).save(argThat(row -> row.getTotalScore() == 115
                && row.getResidentId().equals(7L)));
    }

    @Test
    void rejectsInvalidScoresAndLockedCharts() {
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).build()));
        assertThrows(IllegalArgumentException.class, () -> service.record(
                new FallScreeningService.ScreeningRequest(7L, 24, 0, 0, 0, 0, 0),
                "nurse@example.test"));
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).isChartLocked(true).build()));
        assertThrows(IllegalStateException.class, () -> service.record(
                new FallScreeningService.ScreeningRequest(7L, 25, 0, 0, 0, 0, 0),
                "nurse@example.test"));
        verifyNoInteractions(screenings);
    }
}
