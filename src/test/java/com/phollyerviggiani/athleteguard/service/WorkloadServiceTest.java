// This class is a test class, used to test the workload service
package com.phollyerviggiani.athleteguard.service;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import com.phollyerviggiani.athleteguard.entity.TrainingSession;
import com.phollyerviggiani.athleteguard.entity.WorkloadSummary;
import com.phollyerviggiani.athleteguard.repository.TrainingSessionRepository;
import com.phollyerviggiani.athleteguard.repository.WorkloadSummaryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private WorkloadSummaryRepository workloadSummaryRepository;

    @Mock
    private InjuryRiskService injuryRiskService;

    @InjectMocks
    private WorkloadService workloadService;

    // test #1: standard case with enough sessions in the last 28 days, so a summary should be saved
    @Test
    void shouldCalculateAndStoreWorkloadCorrectly() {

        // ARRANGE

        // Create an athlete
        Athlete athlete = new Athlete();
        athlete.setAthleteId(1);

        // Create three training sessions
        TrainingSession session1 = new TrainingSession();
        session1.setSessionDate(LocalDate.now());
        session1.setDurationMinutes(60);
        session1.setIntensity(5);

        TrainingSession session2 = new TrainingSession();
        session2.setSessionDate(LocalDate.now().minusDays(2));
        session2.setDurationMinutes(60);
        session2.setIntensity(5);

        TrainingSession session3 = new TrainingSession();
        session3.setSessionDate(LocalDate.now().minusDays(4));
        session3.setDurationMinutes(60);
        session3.setIntensity(5);

        List<TrainingSession> sessions =
                List.of(session1, session2, session3);

        // Tell the mocked repository what to return
        when(trainingSessionRepository.findByAthlete_AthleteId(1))
                .thenReturn(sessions);

        // Make the mocked save method return the summary it receives
        when(workloadSummaryRepository.save(any(WorkloadSummary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // ACT

        workloadService.calculateAndStoreWorkload(athlete);


        // ASSERT

        // Capture the summary passed to the repository
        var summaryCaptor = org.mockito.ArgumentCaptor.forClass(
                WorkloadSummary.class);

        verify(workloadSummaryRepository).save(summaryCaptor.capture());

        WorkloadSummary savedSummary = summaryCaptor.getValue();

        // Verify workload calculations
        assertEquals(900.0 / 7.0,
                savedSummary.getAcuteWorkload(), 0.01);

        assertEquals(900.0 / 28.0,
                savedSummary.getChronicWorkload(), 0.01);

        assertEquals(4.0,
                savedSummary.getAcwr(), 0.01);

        // Verify the summary belongs to the correct athlete
        assertEquals(athlete, savedSummary.getAthlete());

        // Verify injury risk evaluation was triggered
        verify(injuryRiskService).evaluateRisk(savedSummary);
    }


    // test #2: not enough sessions in the last 28 days, so no summary should be saved
    @Test
    void shouldNotSaveSummaryWhenInsufficientSessions() {

        // ARRANGE

        // Create an athlete
        Athlete athlete = new Athlete();
        athlete.setAthleteId(1);

        // Create two training sessions within the last 28 days
        TrainingSession session1 = new TrainingSession();
        session1.setSessionDate(LocalDate.now());
        session1.setDurationMinutes(60);
        session1.setIntensity(5);

        TrainingSession session2 = new TrainingSession();
        session2.setSessionDate(LocalDate.now().minusDays(3));
        session2.setDurationMinutes(45);
        session2.setIntensity(6);

        List<TrainingSession> sessions = List.of(session1, session2);

        // Tell the mocked repository to return these sessions
        when(trainingSessionRepository.findByAthlete_AthleteId(1))
                .thenReturn(sessions);


        // ACT

        workloadService.calculateAndStoreWorkload(athlete);


        // ASSERT

        // Verify that no workload summary was saved
        verify(workloadSummaryRepository, never())
                .save(any(WorkloadSummary.class));

        // Verify that injury risk evaluation was not triggered
        verify(injuryRiskService, never())
                .evaluateRisk(any(WorkloadSummary.class));
    }


    // test #3: no sessions at all, so no summary should be saved
    @Test
    void shouldNotSaveSummaryWhenNoSessionsExist() {

        // ARRANGE

        // Create an athlete
        Athlete athlete = new Athlete();
        athlete.setAthleteId(1);

        // Configure the repository to return an empty list
        when(trainingSessionRepository.findByAthlete_AthleteId(1))
                .thenReturn(List.of());


        // ACT

        workloadService.calculateAndStoreWorkload(athlete);


        // ASSERT

        // Verify that no workload summary was saved
        verify(workloadSummaryRepository, never())
                .save(any(WorkloadSummary.class));

        // Verify that injury risk evaluation was not triggered
        verify(injuryRiskService, never())
                .evaluateRisk(any(WorkloadSummary.class));
    }


    // test #4: all sessions are older than 28 days, so no summary should be saved
    @Test
    void shouldNotSaveSummaryWhenTrainingHistoryIsTooOld() {

        // ARRANGE

        Athlete athlete = new Athlete();
        athlete.setAthleteId(1);

        LocalDate today = LocalDate.now();

        TrainingSession session1 = new TrainingSession();
        session1.setSessionDate(today.minusDays(61));
        session1.setDurationMinutes(60);
        session1.setIntensity(5);

        TrainingSession session2 = new TrainingSession();
        session2.setSessionDate(today.minusDays(65));
        session2.setDurationMinutes(45);
        session2.setIntensity(6);

        TrainingSession session3 = new TrainingSession();
        session3.setSessionDate(today.minusDays(70));
        session3.setDurationMinutes(30);
        session3.setIntensity(4);

        when(trainingSessionRepository.findByAthlete_AthleteId(1))
                .thenReturn(List.of(session1, session2, session3));

        // ACT

        workloadService.calculateAndStoreWorkload(athlete);

        // ASSERT

        verify(workloadSummaryRepository, never())
                .save(any(WorkloadSummary.class));

        verify(injuryRiskService, never())
                .evaluateRisk(any(WorkloadSummary.class));
    }

    // test #5: some sessions are older than 28 days, but there are enough recent sessions to calculate a summary
    @Test
    void shouldExcludeOlderSessionsFromAcuteWorkload() {

        // ARRANGE

        Athlete athlete = new Athlete();
        athlete.setAthleteId(1);

        LocalDate today = LocalDate.now();

        TrainingSession session1 = new TrainingSession();
        session1.setSessionDate(today);
        session1.setDurationMinutes(60);
        session1.setIntensity(5);

        TrainingSession session2 = new TrainingSession();
        session2.setSessionDate(today.minusDays(3));
        session2.setDurationMinutes(60);
        session2.setIntensity(5);

        TrainingSession session3 = new TrainingSession();
        session3.setSessionDate(today.minusDays(10));
        session3.setDurationMinutes(60);
        session3.setIntensity(5);

        when(trainingSessionRepository.findByAthlete_AthleteId(1))
                .thenReturn(List.of(session1, session2, session3));

        when(workloadSummaryRepository.save(any(WorkloadSummary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT

        workloadService.calculateAndStoreWorkload(athlete);

        // ASSERT

        var summaryCaptor =
                org.mockito.ArgumentCaptor.forClass(WorkloadSummary.class);

        verify(workloadSummaryRepository).save(summaryCaptor.capture());

        WorkloadSummary savedSummary = summaryCaptor.getValue();

        assertEquals(600.0 / 7.0, savedSummary.getAcuteWorkload(), 0.001);
        assertEquals(900.0 / 28.0, savedSummary.getChronicWorkload(), 0.001);
        assertEquals(600.0 / 900.0 * 28.0 / 7.0,
                savedSummary.getAcwr(), 0.001);

        verify(injuryRiskService).evaluateRisk(savedSummary);
    }

    // test #6: sessions that fall exactly on the boundaries of the acute and chronic workload windows should be included in the calculations
    @Test
    void shouldIncludeSessionsOnWorkloadWindowBoundaries() {

        // ARRANGE

        Athlete athlete = new Athlete();
        athlete.setAthleteId(1);

        LocalDate today = LocalDate.now();

        TrainingSession session1 = new TrainingSession();
        session1.setSessionDate(today.minusDays(7));
        session1.setDurationMinutes(60);
        session1.setIntensity(5);

        TrainingSession session2 = new TrainingSession();
        session2.setSessionDate(today.minusDays(28));
        session2.setDurationMinutes(60);
        session2.setIntensity(5);

        TrainingSession session3 = new TrainingSession();
        session3.setSessionDate(today);
        session3.setDurationMinutes(60);
        session3.setIntensity(5);

        when(trainingSessionRepository.findByAthlete_AthleteId(1))
                .thenReturn(List.of(session1, session2, session3));

        when(workloadSummaryRepository.save(any(WorkloadSummary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT

        workloadService.calculateAndStoreWorkload(athlete);

        // ASSERT

        var summaryCaptor =
                org.mockito.ArgumentCaptor.forClass(WorkloadSummary.class);

        verify(workloadSummaryRepository).save(summaryCaptor.capture());

        WorkloadSummary savedSummary = summaryCaptor.getValue();

        assertEquals(600.0 / 7.0,
                savedSummary.getAcuteWorkload(), 0.001);

        assertEquals(900.0 / 28.0,
                savedSummary.getChronicWorkload(), 0.001);

        verify(injuryRiskService).evaluateRisk(savedSummary);
    }

    // test #7: sessions that are exactly 60 days old should not be included in the calculations, but sessions that are 59 days old should be included
    @Test
    void shouldAllowTrainingHistoryExactly60DaysOld() {

        // ARRANGE

        Athlete athlete = new Athlete();
        athlete.setAthleteId(1);

        LocalDate today = LocalDate.now();

        TrainingSession session1 = new TrainingSession();
        session1.setSessionDate(today.minusDays(60));
        session1.setDurationMinutes(60);
        session1.setIntensity(5);

        TrainingSession session2 = new TrainingSession();
        session2.setSessionDate(today.minusDays(61));
        session2.setDurationMinutes(60);
        session2.setIntensity(5);

        TrainingSession session3 = new TrainingSession();
        session3.setSessionDate(today.minusDays(62));
        session3.setDurationMinutes(60);
        session3.setIntensity(5);

        when(trainingSessionRepository.findByAthlete_AthleteId(1))
                .thenReturn(List.of(session1, session2, session3));

        // ACT

        workloadService.calculateAndStoreWorkload(athlete);

        // ASSERT

        verify(workloadSummaryRepository, never())
                .save(any(WorkloadSummary.class));

        verify(injuryRiskService, never())
                .evaluateRisk(any(WorkloadSummary.class));
    }

    // test #8: sessions that are exactly 28 days old should be included in the calculations, but sessions that are 29 days old should not be included
    @Test
    void shouldCalculateWorkloadWithExactlyThreeRecentSessions() {

        // ARRANGE

        Athlete athlete = new Athlete();
        athlete.setAthleteId(1);

        LocalDate today = LocalDate.now();

        TrainingSession session1 = new TrainingSession();
        session1.setSessionDate(today);
        session1.setDurationMinutes(60);
        session1.setIntensity(5);

        TrainingSession session2 = new TrainingSession();
        session2.setSessionDate(today.minusDays(10));
        session2.setDurationMinutes(45);
        session2.setIntensity(6);

        TrainingSession session3 = new TrainingSession();
        session3.setSessionDate(today.minusDays(20));
        session3.setDurationMinutes(30);
        session3.setIntensity(4);

        when(trainingSessionRepository.findByAthlete_AthleteId(1))
                .thenReturn(List.of(session1, session2, session3));

        when(workloadSummaryRepository.save(any(WorkloadSummary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT

        workloadService.calculateAndStoreWorkload(athlete);

        // ASSERT

        var summaryCaptor =
                org.mockito.ArgumentCaptor.forClass(WorkloadSummary.class);

        verify(workloadSummaryRepository).save(summaryCaptor.capture());

        WorkloadSummary savedSummary = summaryCaptor.getValue();

        assertEquals(300.0 / 7.0, savedSummary.getAcuteWorkload(), 0.001);
        assertEquals(690.0 / 28.0, savedSummary.getChronicWorkload(), 0.001);

        verify(injuryRiskService).evaluateRisk(savedSummary);
    }
}


