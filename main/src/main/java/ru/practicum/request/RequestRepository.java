package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.util.List;

@Repository

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> getAllByRequester_Id(Long requesterId);

    List<Request> getAllByEvent_IdAndAndRequester_Id(Long eventId, Long requesterId);

    List<Request> getAllByEvent_Id(Long eventId);

    List<Request> getAllByEvent_IdAndStatus(Long eventId, Status status);

   // Request getByRequester_IdAndEvent_IdAndAndId(Long requesterId, Long eventId, Long requestId);
}
