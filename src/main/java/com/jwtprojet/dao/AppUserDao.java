package com.jwtprojet.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtprojet.entities.AppUser;

public interface AppUserDao extends JpaRepository<AppUser, Long>{
	AppUser findByUsername(String username);
}
