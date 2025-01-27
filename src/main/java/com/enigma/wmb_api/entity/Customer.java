package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.Constant;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = Constant.CUSTOMER_TABLE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 16)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
}
