package com.jwtprojet.web;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwtprojet.Utils.JwtUtil;
import com.jwtprojet.Utils.RoleUserUtil;
import com.jwtprojet.entities.AppRole;
import com.jwtprojet.entities.AppUser;
import com.jwtprojet.service.AccountService;

@RestController
public class AccountRestController {

	private AccountService accountService;
	
	public AccountRestController(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@GetMapping(path = "/users")
	@PostAuthorize("hasAuthority('USER')")
	public List<AppUser> appUser(){
		return accountService.listUsers();
	}
	
	@PostMapping(path = "/users")
	@PostAuthorize("hasAuthority('ADMIN')")
	public AppUser saveUser(@RequestBody AppUser appUser) {
		return accountService.addNewUser(appUser);
	}
	
	@PostMapping(path = "/roles")
	@PostAuthorize("hasAuthority('USER')")
	public AppRole saveRole(@RequestBody AppRole appRole) {
		return accountService.addNewRole(appRole);
	}
	
	@PostMapping(path = "/addRoleToUser")
	public void addRoleToUser(@RequestBody RoleUserUtil roleUserUtil) {
		accountService.addRoleToUsername(roleUserUtil.getUsername(), roleUserUtil.getPassword());
	}
	
	@GetMapping(path = "refreshToken")
	public void refreshToken(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String authToken= request.getHeader(JwtUtil.AUTH_HEADER);
		if(authToken!=null && authToken.startsWith(JwtUtil.PREFIX)) {
			try {
				String jwt=authToken.substring(JwtUtil.PREFIX.length());
				Algorithm algorithm= Algorithm.HMAC256(JwtUtil.SECRET);
				JWTVerifier jwtVerifier=JWT.require(algorithm).build();
				DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
				String username=decodedJWT.getSubject();
				AppUser appUser=accountService.loadUserByUserName(username);
				String jwtAccessToken = JWT.create()
						.withSubject(appUser.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis()+JwtUtil.EXPIRE_ACCESS_TOKEN))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", appUser.getAppRoles().stream().map(r->r.getRoleName()).collect(Collectors.toList()))
						.sign(algorithm);
				Map<String, String> idToken= new HashMap<>();
				idToken.put("access-token", jwtAccessToken);
				idToken.put("refresh-token", jwt);
				response.setContentType("application/json");
				new ObjectMapper().writeValue(response.getOutputStream(), idToken);
			} catch (Exception e) {
				throw e;
			}
		}else {
			throw new RuntimeException("Refresh token required!!!");
		}
	}
	
	@GetMapping(path = "/profile")
	public AppUser profile(Principal principal) {
		return accountService.loadUserByUserName(principal.getName());
	}
}
