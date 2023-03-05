package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.Category;
import ru.practicum.location.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
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
    @Column
    private String annotation;
    @ManyToOne(optional = true)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "CONFIRMED_REQUESTS")
    private Integer confirmedRequests;
    @Column(name = "CREATED_ON", nullable = true)
    private LocalDateTime createdOn;
    @Column(nullable = false)
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
    @Column(nullable = false)
    private String title;
    @Column(nullable = true)
    private Integer views;

    public Event(Long id, String annotation, Category category, String description, User initiator, Boolean paid,
                 String title, Integer views, Integer confirmedRequests, LocalDateTime eventDate, Location location, Integer participantLimit, Boolean requestModeration) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.initiator = initiator;
        this.paid = paid;
        this.title = title;
        this.views = views;
        this.confirmedRequests = confirmedRequests;
        this.eventDate = eventDate;
        this.location = location;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
    }
}

