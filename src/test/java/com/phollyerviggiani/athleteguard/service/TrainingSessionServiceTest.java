// this is a test class for the training service class

package com.phollyerviggiani.athleteguard.service;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import com.phollyerviggiani.athleteguard.entity.TrainingSession;
import com.phollyerviggiani.athleteguard.repository.TrainingSessionRepository;

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
class TrainingSessionServiceTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private WorkloadService workloadService;

    @InjectMocks
    private TrainingSessionService trainingSessionService;

    // test #1: retrieve sessions for a given athlete
    @Test
    void shouldReturnSessionsForAthlete() {

        // ARRANGE

        Integer athleteId = 1;

        TrainingSession session1 = new TrainingSession();
        session1.setSessionDate(LocalDate.now());
        session1.setDurationMinutes(60);
        session1.setIntensity(5);

        TrainingSession session2 = new TrainingSession();
        session2.setSessionDate(LocalDate.now().minusDays(2));
        session2.setDurationMinutes(45);
        session2.setIntensity(6);

        List<TrainingSession> sessions = List.of(session1, session2);

        when(trainingSessionRepository.findByAthlete_AthleteId(athleteId))
                .thenReturn(sessions);

        // ACT

        List<TrainingSession> result =
                trainingSessionService.getSessionsForAthlete(athleteId);

        // ASSERT

        assertEquals(2, result.size());
        assertSame(sessions, result);

        verify(trainingSessionRepository).findByAthlete_AthleteId(athleteId);
    }

    // test #2: retrieve sessions for an athlete with no sessions
    @Test
    void shouldReturnEmptyListWhenAthleteHasNoSessions() {

        // ARRANGE

        Integer athleteId = 1;

        when(trainingSessionRepository.findByAthlete_AthleteId(athleteId))
                .thenReturn(List.of());

        // ACT

        List<TrainingSession> result =
                trainingSessionService.getSessionsForAthlete(athleteId);

        // ASSERT

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(trainingSessionRepository).findByAthlete_AthleteId(athleteId);
    }

    // test #3: save a training session for an athlete
    @Test
    void shouldSaveTrainingSession() {

        // ARRANGE

        Athlete athlete = new Athlete();

        TrainingSession session = new TrainingSession();
        session.setAthlete(athlete);
        session.setSessionDate(LocalDate.now());
        session.setDurationMinutes(60);
        session.setIntensity(5);
        session.setSessionType("Strength Training");

        when(trainingSessionRepository.save(session))
                .thenReturn(session);

        // ACT

        trainingSessionService.addSession(session);

        // ASSERT

        verify(trainingSessionRepository).save(session);
    }

    // test #4: ensure that the saved session is associated with the correct athlete
    @Test
    void shouldAssociateSessionWithCorrectAthlete() {

        // ARRANGE

        Athlete athlete = new Athlete();

        TrainingSession session = new TrainingSession();
        session.setAthlete(athlete);
        session.setSessionDate(LocalDate.now());
        session.setDurationMinutes(60);
        session.setIntensity(5);
        session.setSessionType("Strength Training");

        when(trainingSessionRepository.save(any(TrainingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT

        trainingSessionService.addSession(session);

        // ASSERT

        var sessionCaptor =
                org.mockito.ArgumentCaptor.forClass(TrainingSession.class);

        verify(trainingSessionRepository).save(sessionCaptor.capture());

        TrainingSession savedSession = sessionCaptor.getValue();

        assertSame(athlete, savedSession.getAthlete());
    }

    // test #5: ensure that the workload is recalculated after saving a session
    @Test
    void shouldRecalculateWorkloadAfterSavingSession() {

        // ARRANGE

        Athlete athlete = new Athlete();

        TrainingSession session = new TrainingSession();
        session.setAthlete(athlete);
        session.setSessionDate(LocalDate.now());
        session.setDurationMinutes(60);
        session.setIntensity(5);
        session.setSessionType("Strength Training");

        when(trainingSessionRepository.save(session))
                .thenReturn(session);

        // ACT

        trainingSessionService.addSession(session);

        // ASSERT

        verify(workloadService).calculateAndStoreWorkload(athlete);
    }

    // test #6: ensure that the saved session is returned after saving
    @Test
    void shouldReturnSavedSession() {

        // ARRANGE

        Athlete athlete = new Athlete();

        TrainingSession session = new TrainingSession();
        session.setAthlete(athlete);
        session.setSessionDate(LocalDate.now());
        session.setDurationMinutes(60);
        session.setIntensity(5);
        session.setSessionType("Strength Training");

        TrainingSession savedSession = new TrainingSession();
        savedSession.setSessionId(100);
        savedSession.setAthlete(athlete);
        savedSession.setSessionDate(session.getSessionDate());
        savedSession.setDurationMinutes(session.getDurationMinutes());
        savedSession.setIntensity(session.getIntensity());
        savedSession.setSessionType(session.getSessionType());

        when(trainingSessionRepository.save(session))
                .thenReturn(savedSession);

        // ACT

        TrainingSession result = trainingSessionService.addSession(session);

        // ASSERT

        assertNotNull(result);
        assertEquals(100, result.getSessionId());
        assertSame(savedSession, result);

        verify(workloadService).calculateAndStoreWorkload(athlete);
    }
}