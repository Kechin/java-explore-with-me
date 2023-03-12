package ru.practicum.main.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.Status;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getAllByRequester_Id(Long requesterId);

    List<Request> getAllByEvent_IdAndAndRequester_Id(Long eventId, Long requesterId);

    List<Request> getAllByEvent_Id(Long eventId);

    List<Request> getAllByEvent_IdAndStatus(Long eventId, Status status);

    List<Request> getAllByIdIn(List<Long> requestId);

    @Query("select   count(req) " +
            " from Request req where req.event.id = ?1  and req.status = 'CONFIRMED'" +
            " group by req.event.id")
    Integer getConfirmedRequestCount(Long ids);

    default Integer getConfirmedRequest(Long id) {
        Integer count = getConfirmedRequestCount(id);
        return count == null ? 0 : count;
    }
}










