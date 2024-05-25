package com.hasanalmunawr.booknetwork.entity;

import com.hasanalmunawr.booknetwork.feedbacks.FeedbackEntity;
import com.hasanalmunawr.booknetwork.common.BaseEntity;
import com.hasanalmunawr.booknetwork.history.BookTransactionHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "books")
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

    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }

        var rate = this.feedbacks.stream()
                .mapToDouble(FeedbackEntity::getNote)
                .average()
                .orElse(0.0);

         double roundedRate = Math.round(rate * 10.0) / 10.0;

//         return 4.0 if the roundedRate is lase 4.5, otherwise return 4.5
        return roundedRate;
    }
}
