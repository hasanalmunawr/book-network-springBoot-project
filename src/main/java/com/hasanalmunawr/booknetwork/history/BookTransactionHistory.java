package com.hasanalmunawr.booknetwork.history;

import com.hasanalmunawr.booknetwork.common.BaseEntity;
import com.hasanalmunawr.booknetwork.entity.BookEntity;
import com.hasanalmunawr.booknetwork.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
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
public class BookTransactionHistory extends BaseEntity {

    private boolean returned;
    private boolean returnedApproved;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;
}
