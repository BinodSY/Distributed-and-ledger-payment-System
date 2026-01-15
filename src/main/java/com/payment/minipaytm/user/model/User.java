package com.payment.minipaytm.user.model;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="users")
@Getter
@Setter
@NoArgsConstructor
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name="user_id", nullable=false,updatable=false)
        private UUID userId;
        private String name;
        private String email;
        @Column(name="password_hash", nullable=false)
        private String passwordHash;
        private String phone;
        @Column(name="created_at", nullable=false)
        private Instant createdAt;

        @PrePersist
        protected void onCreate(){
            this.createdAt = Instant.now();
        }

}
