package com.payment.minipaytm.user.dto;

import com.payment.minipaytm.user.model.User;

// import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private String name;
    private String email;
    private String passwordHash;
    private String phone;

    public UserResponse(User user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.phone = user.getPhone();
    }
}
