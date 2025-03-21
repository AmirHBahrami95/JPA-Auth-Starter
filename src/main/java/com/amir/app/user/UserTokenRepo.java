package com.amir.app.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface UserTokenRepo extends CrudRepository<UserToken,String>{
	
	UserToken save(UserToken ut);
	Optional<UserToken> findByToken(String token);
	Optional<UserToken> findByUsrUid(UUID uid);
}
