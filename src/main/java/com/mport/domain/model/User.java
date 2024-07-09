package com.mport.domain.model;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "users")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String firstname;
    private String lastname;
    private String password;

}
