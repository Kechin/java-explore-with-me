package ru.practicum.main.compilation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.event.model.Event;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;
    @Column(unique = true, nullable = false, length = 1024)
    private String title;
    @Column
    private Boolean pinned;

    public Compilation(Set<Event> events, String title, Boolean pinned) {
        this.events = events;
        this.title = title;
        this.pinned = pinned;
    }
}
