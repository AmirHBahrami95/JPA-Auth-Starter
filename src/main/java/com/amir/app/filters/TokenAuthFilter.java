package com.amir.app.filters;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.amir.app.auth.TokenAuthentication;
import com.amir.app.auth.TokenAuthenticationManager;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenAuthFilter extends BasicAuthenticationFilter{
	
	public TokenAuthFilter(TokenAuthenticationManager manager) {
		super(manager);
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
	throws IOException, ServletException {
		Optional<String> token=getBearerToken(request);
		if(token.isPresent()) {
			SecurityContextHolder.getContext().setAuthentication(
					super.getAuthenticationManager().authenticate(
							new TokenAuthentication(token.get(), null)));
		}
		chain.doFilter(request, response);
	}
	
	private Optional<String> getBearerToken(HttpServletRequest req) {
		String token=req.getHeader("Authorization");
		if(token==null) return Optional.empty(); 
		token= StringUtils.removeStart(token, "Bearer").trim();
		return Optional.of(token);
	}

}
