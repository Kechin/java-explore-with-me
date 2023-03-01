package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface HitsRepository extends JpaRepository<Hit, Long> {
    //    e.app.appName,e.ip, e.uri,
    @Query(value = "select new  ru.practicum.Stat (e.app.appName,e.ip, e.uri, count(e.ip)) " +
            "from Hit e  where e.timeStamp > ?1 and  e.timeStamp <?2 and e.uri in ?3 " +
            " group by e.app, e.uri, e.ip order by count (e.uri) desc ")
    List<Stat> getAllViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new  ru.practicum.Stat (e.app.appName,e.ip, e.uri, count(distinct e.ip))  " +
            "from Hit e where e.timeStamp > ?1 and  e.timeStamp <?2 and e.uri in ?3 group by e.app, e.uri,e.ip " +
            "order by count (e.uri) desc")
    List<Stat> getUniqueViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new  ru.practicum.Stat (e.app.appName,e.ip, e.uri, count(e.app)) " +
            "from Hit e where e.timeStamp > ?1 and  e.timeStamp <?2  group by e.app, e.uri,e.ip " +
            "order by count (e.app) desc ")
    List<Stat> getAllViewsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query(value = "select new  ru.practicum.Stat ( e.app.appName,e.ip, e.uri, count(distinct e.ip))  " +
            "from Hit e where e.timeStamp > ?1 and  e.timeStamp <?2  group by e.app, e.uri,e.ip " +
            "order by count (e.app) desc")
    List<Stat> getUniqueViewsWithoutUris(LocalDateTime start, LocalDateTime end);


}




