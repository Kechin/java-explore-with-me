package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface HitsRepository extends JpaRepository<Hit, Long> {

    @Query(value = "select new  ru.practicum.StatDto (e.app, e.uri, count(e.app)) " +
            "from Hit e where e.timeStamp > ?1 and  e.timeStamp <?2 and e.uri in ?3 group by e.app, e.uri " +
            "order by count (e.app) desc ")
    List<StatDto> getAllViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new  ru.practicum.StatDto (e.app, e.uri, count(e.app))  " +
            "from Hit e where e.timeStamp > ?1 and  e.timeStamp <?2 and e.uri in ?3 group by e.app, e.uri,e.ip " +
            "order by count (e.app) desc")
    List<StatDto> getUniqueViews(LocalDateTime start, LocalDateTime end, List<String> uris);
}

