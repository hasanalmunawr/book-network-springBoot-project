package com.hasanalmunawr.booknetwork.books;

import com.hasanalmunawr.booknetwork.entity.BookEntity;
import com.hasanalmunawr.booknetwork.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer>, JpaSpecificationExecutor<BookEntity> {

    @Query("""
            SELECT book FROM books book
            WHERE book.archived = false
                        AND book.shareable = true
                        AND book.owner.id != :userId
            """
    )
    Page<BookEntity> findAllDisplayBooks(Pageable pageable, Integer userId);

}
