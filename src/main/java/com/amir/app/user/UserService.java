package com.amir.app.user;

import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Quick Explaination: This service acts as a bridge between the Spring Security 
 * version of a UserDetailsManager and a Domain UserService. the only reason it 
 * implements UserDetailsManager is if future me wanted to actually switch 
 * using standard spring security functions 
 * 
 * the biggest caveat is, spring security's "loadUserByUsername" is supposed to
 * load users by their unique identifier. they just so happen to call it "username"
 * instead of "id"
 * 
 * whereas in my version, a UUID called "id" is used as pk, and "uname" is supposed
 * to be a unqiue constraint (& nothing more)
 * 
 * so there's some conversion going on in all methods in this class...
 * 
 * ps. There's a usesless spring security method called "changePassword" which is 
 * supposed to change the CURRENT USER's password! like What the ACTUAL FUCK?! it doesn't
 * even receive a UserDetails Object as a parameter! it's fucking rancid!
 * */
@Service
public class UserService implements UserDetailsManager {
	
	private final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepo  userRepo;
	
	@Autowired
	private UserTokenRepo tokenRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Input User object should already be existent, so check before passing down here.
	 * */
	@Transactional
	public Optional<String> login(UserDto u)throws PasswordsMismatchException{
		
		// back checks
		Optional<User> found=userRepo.findByUname(u.getUname());
		if(found.isEmpty()) throw new UsernameNotFoundException("No Such User Found");
		else if(!passwordEncoder.matches(u.getPassw(),found.get().getPassword()))
			throw new PasswordsMismatchException("Passowrds Mismatch");
		
		// making & returning a user token
		UserToken saved=createUserSession(found.get());
		return Optional.of(saved.getToken());
	}
	
	@Transactional
	public Optional<String> register(User u){
		createUser(u);
		UserToken saved=createUserSession(u);
		return Optional.of(saved.getToken());
	}
	
	@Transactional
	private UserToken createUserSession(User u) {
		
		// retreive already existing
		Optional<UserToken> alreadyExisting=tokenRepo.findByUsrUid(u.getUid());
		if(alreadyExisting.isPresent()) return alreadyExisting.get();
		
		// create new session
		UserToken ut=new UserToken();
		ut.setUsr(u);
		ut.setToken(createRandString());
		UserToken saved=tokenRepo.save(ut);
		return saved;
	}
	
	@Override
	public void createUser(UserDetails user) {
		String passString=passwordEncoder.encode(user.getPassword());
		User u= (User) user;
		u.setPassw(passString);
		userRepo.save(u);
	}
	
	//--------------------------------------------------- domain
	public Optional<UserToken> findUserByToken(String token) throws UsernameNotFoundException{return tokenRepo.findByToken(token);} 
	public Optional<User> findById(UUID id) {return userRepo.findById(id);}
	public List<User> findAll(){return userRepo.findAll();}
	public Optional<User> findByUname(String uname){return userRepo.findByUname(uname);} 
	public boolean isUnameTaken(String username) {return userRepo.findByUname(username).isPresent();} 
	
	// --------------------------------------------------- spring-security
	@Override	
	public boolean userExists(String uuid) {return userRepo.findByUid(UUID.fromString(uuid)).isPresent();} 
	
	@Override
	public void deleteUser(String uuid) {userRepo.deleteByUid(UUID.fromString(uuid));} 

	/**NO PASSWORD ENCODING HERE, BEWARE!*/
	@Override
	public void updateUser(UserDetails user) {userRepo.save(user);} 
	
	/** not used as of now, reserved for Authorization Server (maaaayybe) */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findByUid(UUID.fromString(username)).get(); // bridging
	}

	/***
	 * WHAT THE FUCK?! ffs. who desigend this shit?! FUCK YOU SPRING SECURITY!
	 * */
	@Override
	public void changePassword(String oldPassword, String newPassword) { 
		logger.warn("spring security is a fucking prick for designing UserDetails class (& co) like this "
				+ "- it's rigid as fuck! ... also, you shouldn't be getting this message");
	}
	
	/** To be used to change a user's password. 
	 * you know - the way it's fucking meant to be (without an authz server)
	 * */
	public void changePassword(UUID uid,String oldPassword,String newPassword) {
		Optional<User> u=userRepo.findByUid(uid);
		if(u.isPresent() && passwordEncoder.matches(oldPassword,u.get().getPassword())) {
			u.get().setPassw(newPassword);
			userRepo.save(u.get());
		} 
	}
	
	private String createRandString(){
		String str=RandomStringUtils.secure().next(UserToken.TOKEN_LENGTH);
		return HexFormat.of().formatHex(str.getBytes());
	}

}
