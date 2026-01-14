package com.payment.minipaytm.user.reposistory;

import java.util.UUID;
import com.payment.minipaytm.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,UUID>{

    User findByEmail(String email);

}
