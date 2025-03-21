package com.amir.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import com.amir.app.filters.TokenAuthFilter;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private TokenAuthFilter tokAuthFilth;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
	    .authorizeHttpRequests(c -> c
	    		// .requestMatchers("/api/user/logout").authenticated()
	    		.requestMatchers("/api/user/whoami").authenticated()
	        // .requestMatchers("/api/user/**").permitAll()
	        // .requestMatchers("/h2-ui").permitAll()
	        // .requestMatchers("/actuator/**").permitAll()
	    		.requestMatchers("/api/user/test/**").authenticated() // TODO @HasRole("ADMIN")
	        .anyRequest().permitAll()
	    )
	    .httpBasic(c->c.disable())
	    .formLogin(c -> c.disable())
	    .logout(c->c.disable())
	    .csrf(c->c.disable())
	    .cors(c->c.disable())	    
	    // .addFilterBefore(new IPLoggerFilter(), ChannelProcessingFilter.class)
	    .addFilterBefore(tokAuthFilth, AnonymousAuthenticationFilter.class)
	    .anonymous(c->c.disable())
	    /**/
    ;
    return http.build();
	}

}
