package com.rocs.user.domain.person;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, nullable = false)
    private String lastName;

    @Column(length = 64, nullable = false)
    private String firstName;

    @Column(length = 32)
    private String middleName;

    @Temporal(TemporalType.DATE)
    private Date birthdate;

    @Column(length = 255)
    private String birthplace;

    @Column(length = 6)
    private String sex;

    @Column(length = 32)
    private String civilStatus;

    @Column(length = 32)
    private String citizenship;

    @Column(length = 32)
    private String religion;

    @Column(length = 64)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(length = 11)
    private String contactNumber;
}
