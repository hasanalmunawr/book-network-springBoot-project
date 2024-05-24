package com.hasanalmunawr.booknetwork.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Repository
public interface BookRepository extends JpaRepository<BookRepository, Integer> {
}
