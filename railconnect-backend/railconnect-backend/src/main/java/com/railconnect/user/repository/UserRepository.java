package com.railconnect.user.repository;

import com.railconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; 

@Repository("coreUserRepository") 
public interface UserRepository extends JpaRepository<User, Long> {
    
}