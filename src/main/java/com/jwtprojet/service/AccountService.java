package com.jwtprojet.service;

import java.util.List;

import com.jwtprojet.entities.AppRole;
import com.jwtprojet.entities.AppUser;

public interface AccountService {

	AppUser loadUserByUserName(String username);
	AppUser addNewUser(AppUser appUser);
	AppRole addNewRole(AppRole appRole);
	void addRoleToUsername(String username,String roleName);
	List<AppUser> listUsers();
}
