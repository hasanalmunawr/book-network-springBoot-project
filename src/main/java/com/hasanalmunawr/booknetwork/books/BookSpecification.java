package com.hasanalmunawr.booknetwork.books;

import com.hasanalmunawr.booknetwork.entity.BookEntity;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {


    public static Specification<BookEntity> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
