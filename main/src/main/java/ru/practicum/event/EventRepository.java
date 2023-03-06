package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    Event findFirstByCategory(Category category);

    Page<Event> findAll(Pageable pageRequest);

    List<Event> findAllByIdIn(List<Long> ids);

    Page<Event> findAllByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween(Pageable pageable,
                                                                                       List<Long> users,
                                                                                       List<State> states,
                                                                                       List<Long> cats,
                                                                                       LocalDateTime start,
                                                                                       LocalDateTime end);


    Page<Event> findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndCategoryInAndEventDateBetween(
            Pageable pageable, String annotation, String description, Collection<Category> category,
            LocalDateTime eventDate, LocalDateTime eventDate2);


}
