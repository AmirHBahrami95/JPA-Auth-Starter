package com.amir.app.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserTokenRepo extends CrudRepository<UserToken,String>{
	
	// @Transactional
	UserToken save(UserToken ut);
	Optional<UserToken> findByToken(String token);
}
