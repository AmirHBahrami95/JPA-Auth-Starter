package com.amir.app.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
	
	@Autowired
	private UserService uServ;
	
	@PostMapping(path = "/register",consumes="application/json",produces="application/json")
	public ResponseEntity<Map<String,String>> postRegister(@RequestBody UserDto u){
		Map<String,String> answer=new HashMap<>();
		if(uServ.isUnameTaken(u.getUname())) {
			answer.put("error","User Already Exists"); // TODO later use i18n in front
			answer.put("token",null);
			return ResponseEntity.badRequest().body(answer);
		}
		Optional<String> token=uServ.register(User.of(u));
		if(token.isEmpty()) return ResponseEntity.internalServerError().build();
		answer.put("token",token.get());
		return ResponseEntity.ok(answer);
	}
	
	/** 
	 * 	TODO fix the behaviour of when the user is already logged in (has a bearer header)
	 *  in the Auth Filters not here!, this endpoint is Finalized, so think thrice before
	 *  using it.  
	 *  
	 *  TODO fix the problem of only one session at a time being saved, but the guy logins 
	 *  again - just tell him he's already logged in (although this needs a lot more thinking
	 *  but for now it's ok)
	 *  
	 * */
	@PostMapping(path = "/login",consumes="application/json",produces="application/json")
	public ResponseEntity<Map<String,String>> postLogin(@RequestBody UserDto ud){
		Map<String,String> answer=new HashMap<>();
		Optional<String> token=Optional.empty();
		try{
			token=uServ.login(ud);
			answer.put("token",token.get());
			return ResponseEntity.ok(answer);
		}catch(PasswordsMismatchException pme) {
			answer.put("error","Wrong Password");
			return ResponseEntity.badRequest().body(answer);
		}catch(UsernameNotFoundException unfe) {
			return ResponseEntity.notFound().build();
		}catch(Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	@GetMapping(path="/{id}",produces="application/json")
	public ResponseEntity<User> getUserById(@PathVariable("id") String id){
		Optional<User> u=uServ.findById(id);
		if(u.isEmpty()) return ResponseEntity.notFound().build();
		u.get().setPassw(null);
		u.get().setPhoneNo(null);
		return ResponseEntity.of(u);
	}
	
	@GetMapping(path="/whoami",produces="application/json")
	public ResponseEntity<UserDto> whoami(Authentication a){
		return ResponseEntity.ofNullable((UserDto)a.getDetails());
	}
	
	// TODO beware for production!
	@GetMapping(path="/test/all",produces="application/json")
	public ResponseEntity<List<User>> getTestUserAll(){
		return ResponseEntity.ok(uServ.findAll());
	}
	
	@GetMapping(path="/test/by-token/{token}",produces="application/json")
	public ResponseEntity<UserDto> getTestByToken(@PathVariable("token") String token){
		Optional<UserToken> u=uServ.findUserByToken(token);
		if(u.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ofNullable(u.get().getUsr().toDto());
	}
	
	// TODO add reportUser(String username, String report)
	
}
