package com.hasanalmunawr.booknetwork.feedbacks;

import com.hasanalmunawr.booknetwork.common.BaseEntity;
import com.hasanalmunawr.booknetwork.entity.BookEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class FeedbackEntity extends BaseEntity {

    private Double note;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;
}
