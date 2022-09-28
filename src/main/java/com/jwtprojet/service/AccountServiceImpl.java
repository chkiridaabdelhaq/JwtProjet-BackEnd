package com.jwtprojet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jwtprojet.dao.AppRoleDao;
import com.jwtprojet.dao.AppUserDao;
import com.jwtprojet.entities.AppRole;
import com.jwtprojet.entities.AppUser;

@Service
@Transactional
public class AccountServiceImpl implements AccountService{

	@Autowired
	private AppRoleDao roleDao;
	@Autowired
	private AppUserDao userDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Override
	public AppUser loadUserByUserName(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public AppUser addNewUser(AppUser appUser) {
		String pw = appUser.getPassword();
		appUser.setPassword(passwordEncoder.encode(pw));
		return userDao.save(appUser);
	}

	@Override
	public AppRole addNewRole(AppRole appRole) {
		
		return roleDao.save(appRole);
	}

	@Override
	public void addRoleToUsername(String username, String roleName) {
		AppUser appUser=userDao.findByUsername(username);
		AppRole appRole=roleDao.findByRoleName(roleName);
		appUser.getAppRoles().add(appRole);
	}

	@Override
	public List<AppUser> listUsers() {
		return userDao.findAll();
	}

}
