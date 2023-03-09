package ru.practicum.main.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.Status;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> getAllByRequester_Id(Long requesterId);

    List<Request> getAllByEvent_IdAndAndRequester_Id(Long eventId, Long requesterId);

    List<Request> getAllByEvent_Id(Long eventId);

    List<Request> getAllByEvent_IdAndStatus(Long eventId, Status status);

}
