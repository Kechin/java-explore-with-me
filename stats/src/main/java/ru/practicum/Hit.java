package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @ManyToOne
    @JoinColumn(name = "app", nullable = false)
    private App app;
    @Column(name = "requester_ip", nullable = false, length = 64)
    private String ip;
    @Column(nullable = false, length = 1000)
    private String uri;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime timeStamp;

}
