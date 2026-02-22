package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Integer> {
}