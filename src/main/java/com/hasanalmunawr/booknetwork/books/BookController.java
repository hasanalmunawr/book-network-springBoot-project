package com.hasanalmunawr.booknetwork.books;

import com.hasanalmunawr.booknetwork.dto.PageResponse;
import com.hasanalmunawr.booknetwork.history.BorrowedBookResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "books")
@Tag(name = "Book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody BookRequest request,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.saveBook(request, currentUser));
    }

    @GetMapping("/{book-id}")
    public ResponseEntity<BookResponse> findById(
            @RequestParam("book-id") Integer bookId
    ) {
        return ResponseEntity.ok(bookService.findById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooks(page, size, currentUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, currentUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, currentUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, currentUser));
    }



    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<?> updateShareableStatus(
            @RequestParam(name = "book-id") Integer bookId,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, currentUser));
    }


    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<?> updateArchivedStatus(
            @RequestParam(name = "book-id") Integer bookId,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, currentUser));
    }


    @PatchMapping("borrow/return/{book-id}")
    public ResponseEntity<?> returnBorrowedBook(
            @RequestParam(name = "book-id") Integer bookId,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, currentUser));
    }


    @PatchMapping("borrow/return/approve/{book-id}")
    public ResponseEntity<?> approveReturnBorrowedBook(
            @RequestParam(name = "book-id") Integer bookId,
            Authentication currentUser
    ) {
        return ResponseEntity.ok(bookService.approveReturnBorrowedBook(bookId, currentUser));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ) {
        bookService.uploadBookCoverPicture(file, connectedUser, bookId);
        return ResponseEntity.accepted().build();
    }


}
