package com.smartshelf.repositories;

import com.smartshelf.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
    Optional<Category> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
    List<Category> findByActiveTrueOrderByNameAsc();
}
