package ru.practicum.main.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getAllByRequester_Id(Long requesterId);

    List<Request> getAllByEvent_IdAndAndRequester_Id(Long eventId, Long requesterId);

    List<Request> getAllByEvent_Id(Long eventId);

    List<Request> getAllByEvent_IdAndStatus(Long eventId, Status status);

    List<Request> getAllByIdIn(List<Long> requestId);

    @Query("select new map (req.event.id , count(req.id))  " +
            " from Request req where req.event.id in (?1)  and req.status = 'CONFIRMED'" +
            " group by req.event.id")
    List<Map<Integer, Map<Long, Integer>>> getConfirmedRequestCount(List ids);

    default Map<Long, Integer> getConfirmedRequest(List ids) {
        var count = getConfirmedRequestCount(ids);
        Map<Long, Integer> result = new HashMap<>();
        for (Map e : count) {
            long id = (long) e.get("0");
            long confReq = (long) e.get("1");
            result.put(id, (int) confReq);
        }
        return result;
    }
}










