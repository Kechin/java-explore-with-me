package ru.practicum.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findFirstByNameIs(String name);

    Page<Category> findAll(Pageable pageRequest);

}
