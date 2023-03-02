package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<App, String> {

}
