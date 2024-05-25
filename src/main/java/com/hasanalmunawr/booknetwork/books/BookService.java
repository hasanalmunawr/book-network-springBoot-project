package com.hasanalmunawr.booknetwork.books;


import com.hasanalmunawr.booknetwork.dto.PageResponse;
import com.hasanalmunawr.booknetwork.entity.BookEntity;
import com.hasanalmunawr.booknetwork.entity.UserEntity;
import com.hasanalmunawr.booknetwork.exception.BookNotFoundException;
import com.hasanalmunawr.booknetwork.history.BookTransactionHistory;
import com.hasanalmunawr.booknetwork.history.BookTransactionHistoryRepository;
import com.hasanalmunawr.booknetwork.history.BorrowedBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hasanalmunawr.booknetwork.books.BookSpecification.withOwnerId;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository historyRepository;

    public Integer saveBook(BookRequest request, Authentication currentUser) {
        UserEntity user = (UserEntity) currentUser.getPrincipal();
        BookEntity book = bookMapper.toBook(request);

        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException());
        return bookMapper.toBookResponse(bookEntity);
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication currentUser) {
        UserEntity user = (UserEntity) currentUser.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("CreatedAt").descending());
        Page<BookEntity> books = bookRepository.findAllDisplayBooks(pageable, user.getId());

        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication currentUser) {
        UserEntity user = (UserEntity) currentUser.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("CreatedAt").descending());
        Page<BookEntity> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);

        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowsBooks(int page, int size, Authentication currentUser) {
        UserEntity user = (UserEntity) currentUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookTransactionHistory> allBorrowsBooks = historyRepository.findAllBorrowsBooks(pageable, user.getId());

        List<BorrowedBookResponse> bookResponses = allBorrowsBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponses,
                allBorrowsBooks.getNumber(),
                allBorrowsBooks.getSize(),
                allBorrowsBooks.getTotalElements(),
                allBorrowsBooks.getTotalPages(),
                allBorrowsBooks.isFirst(),
                allBorrowsBooks.isLast()
        );
    }
}
