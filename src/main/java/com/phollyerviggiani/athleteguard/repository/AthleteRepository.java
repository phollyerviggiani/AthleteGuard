package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;


// repository interface for the Athlete entity, extending JpaRepository to provide basic CRUD operations
public interface AthleteRepository extends JpaRepository<Athlete, Integer> {
}