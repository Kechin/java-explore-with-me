package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.Category;
import ru.practicum.location.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(length = 2048)
    @NotBlank private String annotation;
    @ManyToOne(optional = true)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "CREATED_ON", nullable = true)
    private LocalDateTime createdOn;
    @Column(nullable = false, length = 128000)
    @NotBlank
    private String description;
    @Column(name = "EVENT_DATE", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne(optional = false)
    @JoinColumn(name = "location")
    private Location location;
    @Column(nullable = false)
    private Boolean paid;
    @Column(name = "PARTICIPANT_LIMIT", nullable = false)
    private Integer participantLimit;
    @Column(name = "PUBLISHED_ON", nullable = true)
    private LocalDateTime publishedOn;
    @Column(name = "REQUEST_MODERATION", nullable = false)
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = true, length = 64)
    private State state;
    @Column(nullable = false, length = 1024)
    private String title;


    public Event(Long id, String annotation, Category category, String description, User initiator, Boolean paid,
                 String title, LocalDateTime eventDate, Location location, Integer participantLimit, Boolean requestModeration) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.initiator = initiator;
        this.paid = paid;
        this.title = title;
        this.eventDate = eventDate;
        this.location = location;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
    }
}

