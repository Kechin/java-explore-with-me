package ru.practicum.main.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.category.Category;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.State;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findFirstByCategory(Category category);

    Page<Event> findAll(Pageable pageRequest);

    Set<Event> findAllByIdIn(List<Long> ids);


    //Initiator Category
    Page<Event> findAllByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween(Pageable pageable,
                                                                                       List<Long> users,
                                                                                       List<State> states,
                                                                                       List<Long> cats,
                                                                                       LocalDateTime start,
                                                                                       LocalDateTime end);

    //Initiator
    Page<Event> findAllByInitiator_IdInAndStateInAndEventDateIsBetween(Pageable pageable,
                                                                       List<Long> users,
                                                                       List<State> states,
                                                                       LocalDateTime start,
                                                                       LocalDateTime end);


    //Category
    Page<Event> findAllByStateInAndCategory_IdInAndEventDateIsBetween(Pageable pageable,
                                                                      List<State> states,
                                                                      List<Long> cats,
                                                                      LocalDateTime start,
                                                                      LocalDateTime end);


    //
    Page<Event> findAllByStateInAndEventDateIsBetween(Pageable pageable,
                                                      List<State> states,
                                                      LocalDateTime start,
                                                      LocalDateTime end);


    //TEXT PAID CAT
    Page<Event> findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndPaidIsAndCategoryInAndEventDateBetween(
            Pageable pageable, String annotation, String description, Boolean paid, Collection<Category> category,
            LocalDateTime eventDate, LocalDateTime eventDate2);

    // PAID CAT
    Page<Event> findAllByPaidIsAndCategoryInAndEventDateBetween(
            Pageable pageable, Boolean paid, Collection<Category> category,
            LocalDateTime eventDate, LocalDateTime eventDate2);

    //TEXT  CAT
    Page<Event> findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndCategoryInAndEventDateBetween(
            Pageable pageable, String annotation, String description, Collection<Category> category,
            LocalDateTime eventDate, LocalDateTime eventDate2);

    // CAT
    Page<Event> findAllByCategoryInAndEventDateBetween(
            Pageable pageable, Collection<Category> category,
            LocalDateTime eventDate, LocalDateTime eventDate2);

    //TEXT PAID
    Page<Event> findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndPaidIsAndEventDateBetween(
            Pageable pageable, String annotation, String description, Boolean paid,
            LocalDateTime eventDate, LocalDateTime eventDate2);

    //TEXT
    Page<Event> findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndEventDateBetween(
            Pageable pageable, String annotation, String description,
            LocalDateTime eventDate, LocalDateTime eventDate2);

    // PAID
    Page<Event> findAllByPaidIsAndEventDateBetween(
            Pageable pageable, Boolean paid,
            LocalDateTime eventDate, LocalDateTime eventDate2);

    //
    Page<Event> findAllByEventDateBetween(
            Pageable pageable,
            LocalDateTime eventDate, LocalDateTime eventDate2);
}
