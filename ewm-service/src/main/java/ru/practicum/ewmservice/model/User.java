package ru.practicum.ewmservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 254, nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
}
