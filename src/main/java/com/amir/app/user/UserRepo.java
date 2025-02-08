package com.amir.app.user;

import java.util.List;
import java.util.Optional;
// import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepo extends CrudRepository<User,String>{
  // List<User> findByLname(String lname);
  Optional<User> findById(String id);
  Optional<User> findByUname(String uname);
  User save(UserDetails u); // what the fuck!?
  List<User> findAll();
  void deleteById(String id);
}
