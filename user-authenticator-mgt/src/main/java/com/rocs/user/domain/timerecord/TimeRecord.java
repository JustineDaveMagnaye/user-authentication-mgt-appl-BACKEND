package com.rocs.user.domain.timerecord;

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
public class TimeRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, nullable = false)
    private String employeeNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timeIn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeOut;

    private long totalHours;

    @Column(length = 255, nullable = false)
    private String createdAt;
}
