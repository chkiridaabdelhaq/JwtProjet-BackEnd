package com.jwtprojet.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtprojet.entities.AppRole;

public interface AppRoleDao extends JpaRepository<AppRole, Long>{
	AppRole findByRoleName(String roleName);
}
