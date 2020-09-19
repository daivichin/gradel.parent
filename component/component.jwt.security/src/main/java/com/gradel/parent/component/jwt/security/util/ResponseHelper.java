package com.gradel.parent.component.jwt.security.util;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

public class ResponseHelper {
	public static void setToken(HttpServletResponse response, String token) {
		response.setHeader("Authorization", token);
	}
	public static void setStatus(HttpServletResponse response, HttpStatus status) {
		response.setStatus(status.value());
	}
	public static void setRegisteration(HttpServletResponse response, String registeration) {
		response.setHeader("Registeration", registeration);
	}
}
