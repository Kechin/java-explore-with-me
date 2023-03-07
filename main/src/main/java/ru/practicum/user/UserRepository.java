package ru.practicum.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.User;

import java.util.ArrayList;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page findAllByIdIn(Pageable pageable, ArrayList<Long> ids);
    Page findAll (Pageable pageable);

}
