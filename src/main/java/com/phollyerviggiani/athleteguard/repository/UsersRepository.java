package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

// repository interface for the Users entity, extending JpaRepository to provide basic CRUD operations
public interface UsersRepository extends JpaRepository<Users, Integer> {
}