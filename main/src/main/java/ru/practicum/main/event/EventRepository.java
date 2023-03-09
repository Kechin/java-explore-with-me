package ru.practicum.main.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.category.Category;
import ru.practicum.main.event.model.State;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findFirstByCategory(Category category);

    Page<Event> findAll(Pageable pageRequest);

    Set<Event> findAllByIdIn(List<Long> ids);

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
