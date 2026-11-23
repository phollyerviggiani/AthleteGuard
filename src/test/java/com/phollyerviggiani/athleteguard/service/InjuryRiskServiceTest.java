// this test class is designed to validate the functionality of the InjuryRiskService class

package com.phollyerviggiani.athleteguard.service;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import com.phollyerviggiani.athleteguard.entity.InjuryRiskFlag;
import com.phollyerviggiani.athleteguard.entity.WorkloadSummary;
import com.phollyerviggiani.athleteguard.repository.InjuryRiskFlagRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InjuryRiskServiceTest {

    @Mock
    private InjuryRiskFlagRepository injuryRiskFlagRepository;

    @InjectMocks
    private InjuryRiskService injuryRiskService;

    // test #1: ensuring it classifies the high risk
    @Test
    void shouldClassifyHighRisk() {

        // ARRANGE

        Athlete athlete = new Athlete();

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAthlete(athlete);
        summary.setAcwr(1.6);

        // ACT

        injuryRiskService.evaluateRisk(summary);

        // ASSERT

        var flagCaptor =
                org.mockito.ArgumentCaptor.forClass(InjuryRiskFlag.class);

        verify(injuryRiskFlagRepository).save(flagCaptor.capture());

        InjuryRiskFlag savedFlag = flagCaptor.getValue();

        assertEquals("HIGH", savedFlag.getRiskLevel());

        assertEquals(
                "ACWR exceeds 1.5 indicating significant workload spike.",
                savedFlag.getReason()
        );

        assertSame(athlete, savedFlag.getAthlete());
        assertSame(summary, savedFlag.getWorkloadSummary());

        assertNotNull(savedFlag.getGeneratedAt());
    }

    // test #2: ensuring it classifies the moderate risk
    @Test
    void shouldClassifyModerateRisk() {

        // ARRANGE

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAcwr(1.3);

        // ACT

        injuryRiskService.evaluateRisk(summary);

        // ASSERT

        var flagCaptor =
                org.mockito.ArgumentCaptor.forClass(InjuryRiskFlag.class);

        verify(injuryRiskFlagRepository).save(flagCaptor.capture());

        InjuryRiskFlag savedFlag = flagCaptor.getValue();

        assertEquals("MODERATE", savedFlag.getRiskLevel());

        assertEquals(
                "ACWR moderately elevated above recommended range.",
                savedFlag.getReason()
        );

        assertNotNull(savedFlag.getGeneratedAt());
    }

    // test #3: ensuring it classifies the low risk
    @Test
    void shouldClassifyLowRisk() {

        // ARRANGE

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAcwr(1.0);

        // ACT

        injuryRiskService.evaluateRisk(summary);

        // ASSERT

        var flagCaptor =
                org.mockito.ArgumentCaptor.forClass(InjuryRiskFlag.class);

        verify(injuryRiskFlagRepository).save(flagCaptor.capture());

        InjuryRiskFlag savedFlag = flagCaptor.getValue();

        assertEquals("LOW", savedFlag.getRiskLevel());

        assertEquals(
                "Workload within optimal training range.",
                savedFlag.getReason()
        );

        assertNotNull(savedFlag.getGeneratedAt());
    }

    // test #4: ensuring it classifies the underload risk
    @Test
    void shouldClassifyUnderloadRisk() {

        // ARRANGE

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAcwr(0.7);

        // ACT

        injuryRiskService.evaluateRisk(summary);

        // ASSERT

        var flagCaptor =
                org.mockito.ArgumentCaptor.forClass(InjuryRiskFlag.class);

        verify(injuryRiskFlagRepository).save(flagCaptor.capture());

        InjuryRiskFlag savedFlag = flagCaptor.getValue();

        assertEquals("UNDERLOAD", savedFlag.getRiskLevel());

        assertEquals(
                "ACWR below optimal range; potential deconditioning risk.",
                savedFlag.getReason()
        );

        assertNotNull(savedFlag.getGeneratedAt());
    }

    // test #5: ensuring it classifies exactly at the high threshold
    @Test
    void shouldClassifyExactlyAtHighThreshold() {

        // ARRANGE

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAcwr(1.5);

        // ACT

        injuryRiskService.evaluateRisk(summary);

        // ASSERT

        var flagCaptor =
                org.mockito.ArgumentCaptor.forClass(InjuryRiskFlag.class);

        verify(injuryRiskFlagRepository).save(flagCaptor.capture());

        InjuryRiskFlag savedFlag = flagCaptor.getValue();

        assertEquals("MODERATE", savedFlag.getRiskLevel());
    }

    // test #6: ensuring it classifies exactly at the moderate threshold
    @Test
    void shouldClassifyExactlyAtModerateThreshold() {

        // ARRANGE

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAcwr(1.2);

        // ACT

        injuryRiskService.evaluateRisk(summary);

        // ASSERT

        var flagCaptor =
                org.mockito.ArgumentCaptor.forClass(InjuryRiskFlag.class);

        verify(injuryRiskFlagRepository).save(flagCaptor.capture());

        InjuryRiskFlag savedFlag = flagCaptor.getValue();

        assertEquals("LOW", savedFlag.getRiskLevel());
    }

    // test #7: ensuring it classifies exactly at the underload threshold
    @Test
    void shouldClassifyExactlyAtUnderloadThreshold() {

        // ARRANGE

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAcwr(0.8);

        // ACT

        injuryRiskService.evaluateRisk(summary);

        // ASSERT

        var flagCaptor =
                org.mockito.ArgumentCaptor.forClass(InjuryRiskFlag.class);

        verify(injuryRiskFlagRepository).save(flagCaptor.capture());

        InjuryRiskFlag savedFlag = flagCaptor.getValue();

        assertEquals("LOW", savedFlag.getRiskLevel());
    }

    // test #8: ensuring it saves the flag with the correct athlete and summary
    @Test
    void shouldSaveFlagWithCorrectAthleteAndSummary() {

        // ARRANGE

        Athlete athlete = new Athlete();

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAthlete(athlete);
        summary.setAcwr(1.6);

        // ACT

        injuryRiskService.evaluateRisk(summary);

        // ASSERT

        var flagCaptor =
                org.mockito.ArgumentCaptor.forClass(InjuryRiskFlag.class);

        verify(injuryRiskFlagRepository).save(flagCaptor.capture());

        InjuryRiskFlag savedFlag = flagCaptor.getValue();

        assertSame(athlete, savedFlag.getAthlete());
        assertSame(summary, savedFlag.getWorkloadSummary());

        assertEquals("HIGH", savedFlag.getRiskLevel());
        assertNotNull(savedFlag.getGeneratedAt());
    }
}