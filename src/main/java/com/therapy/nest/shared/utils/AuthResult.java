package com.therapy.nest.shared.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class AuthResult<T> {

	private T data;
	private String message;

	private boolean error;
	private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

	public AuthResult(T data, String message, boolean error, Collection<? extends GrantedAuthority> authorities) {
		this.data = data;
		this.message = message;
		this.error = error;
		this.authorities = authorities;
	}

}
