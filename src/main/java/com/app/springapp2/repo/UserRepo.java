package com.app.springapp2.repo;

import com.app.springapp2.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, Long> {

    User findByUsername(String name);

}
