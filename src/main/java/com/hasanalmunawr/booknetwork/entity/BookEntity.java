package com.hasanalmunawr.booknetwork.entity;

import com.hasanalmunawr.booknetwork.books.FeedbackEntity;
import com.hasanalmunawr.booknetwork.common.BaseEntity;
import com.hasanalmunawr.booknetwork.history.BookTransactionHistory;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class BookEntity extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @OneToMany(mappedBy = "book")
    private List<FeedbackEntity> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> bookTransactionHistories;
}
