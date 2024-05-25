package com.hasanalmunawr.booknetwork.books;

import com.hasanalmunawr.booknetwork.entity.BookEntity;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public BookEntity toBook(BookRequest request) {
        return BookEntity.builder()
                .id(request.id())
                .title(request.title())
                .isbn(request.isbn())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public BookResponse toBookResponse(BookEntity book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().getFullName())
//                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }
}
