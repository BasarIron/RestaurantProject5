package com.grup7.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Slf4j(topic = "UserDAO")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surname;
    private LocalDate date;
    private String reservationCode = "RZ53-"+ UUID.randomUUID().toString().substring(0,10);

    @ManyToOne
    @JoinColumn(name = "table_id")
    private Table reservedTable;
}