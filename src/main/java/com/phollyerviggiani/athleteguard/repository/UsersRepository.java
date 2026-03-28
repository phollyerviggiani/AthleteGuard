package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// repository interface for the Users entity, extending JpaRepository to provide basic CRUD operations
public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email);
}