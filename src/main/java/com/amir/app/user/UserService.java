package com.amir.app.user;

import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

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
		UserToken ut=new UserToken();
		ut.setUsrUid(u.getId());
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
	
	// one-liners
	public Optional<UserToken> findUserByToken(String token) throws UsernameNotFoundException{return tokenRepo.findByToken(token);}
	public Optional<User> findById(String id) {return userRepo.findById(id);}
	public List<User> findAll(){return userRepo.findAll();}
	public Optional<User> findByUname(String uname){return userRepo.findByUname(uname);}
	@Override
	public boolean userExists(String username) {return userRepo.findById(username).isPresent();}
	public boolean isUnameTaken(String username) {return userRepo.findByUname(username).isPresent();}
	@Override
	public void deleteUser(String username) {userRepo.deleteById(username);}

	/**NO PASSWORD ENCODING HERE, BEWARE!*/
	@Override
	public void updateUser(UserDetails user) {userRepo.save(user);}
	
	/** not used as of now, reserved for Authorization Server (maaaayybe) */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findById(username).get();
	}

	/***
	 * WHAT THE FUCK?! ffs. who desigend this shit?! FUCK YOU SPRING SECURITY!
	 * */
	@Override
	public void changePassword(String oldPassword, String newPassword) { 
		
	}
	
	/** To be used to save a user's password. 
	 * you know - the way it's fucking meant to be (without an authz server)
	 * */
	public void changePassword(String uid,String oldPassword,String newPassword) {
		Optional<User> u=userRepo.findById(uid);
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
