package com.rocs.user.domain.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rocs.user.domain.person.Person;
import com.rocs.user.domain.secretphrase.SecretPhrase;
import com.rocs.user.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends Person implements Serializable {

    @Column(length = 50, nullable = false, unique = true)
    private String employeeNumber;

    @Column(length = 32)
    private String positionInRc;

    @Temporal(TemporalType.DATE)
    private Date dateEmployed;

    @Column(length = 32)
    private String sssNo;

    @Column(length = 32)
    private String tinNo;

    @Column(length = 32)
    private String pagibigNo;

    @OneToOne
    @JoinColumn(name = "login_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @OneToOne
    @JoinColumn(name = "secret_phrase_id", referencedColumnName = "id")
    @JsonIgnore
    private SecretPhrase secretPhrase;
}
