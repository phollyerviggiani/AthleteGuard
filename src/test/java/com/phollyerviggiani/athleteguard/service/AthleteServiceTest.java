// this is the test class for the athlete service

package com.phollyerviggiani.athleteguard.service;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import com.phollyerviggiani.athleteguard.repository.AthleteRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AthleteServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @InjectMocks
    private AthleteService athleteService;

    // test #1: retrieve all the athletes
    @Test
    void shouldReturnAllAthletes() {

        // ARRANGE

        Athlete athlete1 = new Athlete();
        athlete1.setAthleteId(1);
        athlete1.setFirstName("John");
        athlete1.setLastName("Smith");

        Athlete athlete2 = new Athlete();
        athlete2.setAthleteId(2);
        athlete2.setFirstName("Alex");
        athlete2.setLastName("Brown");

        List<Athlete> athletes = List.of(athlete1, athlete2);

        when(athleteRepository.findAll()).thenReturn(athletes);

        // ACT

        List<Athlete> result = athleteService.getAllAthletes();

        // ASSERT

        assertEquals(2, result.size());
        assertSame(athletes, result);

        verify(athleteRepository).findAll();
    }

    // test #2: retrieve all the athletes when no athletes exist
    @Test
    void shouldReturnEmptyListWhenNoAthletesExist() {

        // ARRANGE

        when(athleteRepository.findAll()).thenReturn(List.of());

        // ACT

        List<Athlete> result = athleteService.getAllAthletes();

        // ASSERT

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(athleteRepository).findAll();
    }

    // test #3: retrieve an athlete by id
    @Test
    void shouldReturnAthleteById() {

        // ARRANGE

        Integer athleteId = 1;

        Athlete athlete = new Athlete();
        athlete.setAthleteId(athleteId);
        athlete.setFirstName("John");
        athlete.setLastName("Smith");

        when(athleteRepository.findById(athleteId))
                .thenReturn(Optional.of(athlete));

        // ACT

        Optional<Athlete> result = athleteService.getAthleteById(athleteId);

        // ASSERT

        assertTrue(result.isPresent());
        assertEquals(athleteId, result.get().getAthleteId());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());

        verify(athleteRepository).findById(athleteId);
    }

    // test #4: retrieve an athlete by id when the athlete does not exist
    @Test
    void shouldReturnEmptyWhenAthleteDoesNotExist() {

        // ARRANGE

        Integer athleteId = 999;

        when(athleteRepository.findById(athleteId))
                .thenReturn(Optional.empty());

        // ACT

        Optional<Athlete> result = athleteService.getAthleteById(athleteId);

        // ASSERT

        assertTrue(result.isEmpty());

        verify(athleteRepository).findById(athleteId);
    }

    // test #5: verify that the repository is called with the correct athlete id
    @Test
    void shouldCallRepositoryWithCorrectAthleteId() {

        // ARRANGE

        Integer athleteId = 5;

        when(athleteRepository.findById(athleteId))
                .thenReturn(Optional.empty());

        // ACT

        athleteService.getAthleteById(athleteId);

        // ASSERT

        verify(athleteRepository).findById(5);
        verify(athleteRepository, never()).findById(1);
    }

}