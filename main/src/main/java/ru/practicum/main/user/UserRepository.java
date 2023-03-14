package ru.practicum.main.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.user.model.User;

import java.util.ArrayList;

public interface UserRepository extends JpaRepository<User, Long> {
    Page findAllByIdIn(Pageable pageable, ArrayList<Long> ids);

    Page findAll(Pageable pageable);

}
