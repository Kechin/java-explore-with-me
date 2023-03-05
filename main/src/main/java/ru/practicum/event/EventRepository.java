package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.category.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    Event findFirstByCategory(Category category);
    Page<Event> findAll(Pageable pageRequest);
    List<Event> findAllByIdIn(List<Long>ids);
    Page<Event> findAllByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween(Pageable pageable,
                                                                                       List<Long> users,
                                                                                       List<State> states,
                                                                                       List<Long> cats,
                                                                                       LocalDateTime start,
                                                                                       LocalDateTime end);



    Page<Event> findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndCategoryInAndEventDateBetween
            (Pageable pageable, String annotation, String description, Collection<Category> category, LocalDateTime eventDate, LocalDateTime eventDate2);


//    @Query("select event from Event event where event.initiator.id in ?1 and event.category.id in ?2 and event.state in ?3 " +
//            "and event.eventDate > ?4 and event.eventDate < ?5 and event.participantLimit=0 " +
//            "or event.confirmedRequests <= event.participantLimit ")
//    List<Event> findWithParam(ArrayList<Long> ids, ArrayList<Long> catIds, State[] states,
//                              LocalDateTime rangeStart, LocalDateTime rangeEnd);
//
//    @Query(" select event from Event  event  where event.annotation in ?1 or event.description  in ?1  and event.category in ?2 "+
//            "and event.eventDate > ?3 and event.eventDate < ?4 and event.paid in ?5 " )
//    List<Event> findWithFiltration(String text1,  List<Category> cats, LocalDateTime start,
//                                   LocalDateTime end, Boolean paid );




}
