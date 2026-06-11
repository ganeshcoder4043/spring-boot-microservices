package com.microservice.user.service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User_Table")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @Column(name = "UserId")
    private String userId;

    @Column(name = "UserName")
    private String name;

    @Column(name = "UserEmail")
    private String email;

    @Column(name = "UserLocation")
    private String location;

    @Column(name = "UserAbout")
    private String about;

    /*  In microservices, ratings data comes from a "different service (Rating Service)",
        not from "User Service's database".
        . JPA ignores this field completely
        . No column created for ratings
        . Not saved to database
        . Runtime calculations, derived fields, or fields from other services
     */
    @Transient
    private List<Rating> ratings = new ArrayList<>();
}
