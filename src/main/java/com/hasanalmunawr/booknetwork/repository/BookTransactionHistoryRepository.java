package com.hasanalmunawr.booknetwork.repository;

import com.hasanalmunawr.booknetwork.history.BookTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {


    @Query("""
            SELECT history FROM BookTransactionHistory history
            WHERE history.user.id =:userId
            """)
    Page<BookTransactionHistory> findAllBorrowsBooks(Pageable pageable, Integer userId);

    @Query("""
             SELECT history FROM BookTransactionHistory history
             WHERE history.book.owner.id =:userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    boolean isAlreadyBorrowedByUser(Integer bookId, Integer id);
}
