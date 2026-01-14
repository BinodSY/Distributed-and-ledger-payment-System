package com.payment.minipaytm.authentication.interfaces;
import com.payment.minipaytm.authentication.dto.AuthUserDTO;




public interface UserAuthPort {
    AuthUserDTO findByEmail(String email);
}
