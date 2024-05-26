package com.hasanalmunawr.booknetwork.repository;

import com.hasanalmunawr.booknetwork.feedbacks.FeedbackEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedbackEntity, Integer> {


    Page<FeedbackEntity> findAllByBookId(Integer bookId, Pageable pageable);
}
