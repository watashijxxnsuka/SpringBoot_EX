package com.bit.springboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
//@Table(name = "T_MEMBER")
@Getter
@Setter
@DynamicInsert
public class Member {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(unique = true, length = 20)
    private String username;

    @Column(length = 300)
    private String password;

    private String email;

    @Column(unique = true)
    private String nickname;

    private String tel;

    @ColumnDefault("'ROLE_USER'")
    private String role;

//    @Transient
//    private LocalDateTime regdate;












}
