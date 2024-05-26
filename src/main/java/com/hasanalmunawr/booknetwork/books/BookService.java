package com.hasanalmunawr.booknetwork.books;


import com.hasanalmunawr.booknetwork.dto.PageResponse;
import com.hasanalmunawr.booknetwork.entity.BookEntity;
import com.hasanalmunawr.booknetwork.entity.UserEntity;
import com.hasanalmunawr.booknetwork.exception.EntityNotFoundException;
import com.hasanalmunawr.booknetwork.exception.OperationNotPermittedException;
import com.hasanalmunawr.booknetwork.file.FileStoreService;
import com.hasanalmunawr.booknetwork.history.BookTransactionHistory;
import com.hasanalmunawr.booknetwork.repository.BookTransactionHistoryRepository;
import com.hasanalmunawr.booknetwork.history.BorrowedBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.hasanalmunawr.booknetwork.books.BookSpecification.withOwnerId;


@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository historyRepository;
    private final FileStoreService fileStoreService;

    public Integer saveBook(BookRequest request, Authentication currentUser) {
        UserEntity user = (UserEntity) currentUser.getPrincipal();
        BookEntity book = bookMapper.toBook(request);

        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(EntityNotFoundException::new);
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

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication currentUser) {
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

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication currentUser) {
        UserEntity user = (UserEntity) currentUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<BookTransactionHistory> allReturnedBooks = historyRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allReturnedBooks.getNumber(),
                allReturnedBooks.getSize(),
                allReturnedBooks.getNumberOfElements(),
                allReturnedBooks.getTotalPages(),
                allReturnedBooks.isFirst(),
                allReturnedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication currentUser) {
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book Not Found"));

        UserEntity user = (UserEntity) currentUser.getPrincipal();

        if (!Objects.equals(bookEntity.getOwner(), user)) {
            throw new OperationNotPermittedException("You Can not Update others books shareable status");
        }
        bookEntity.setShareable(!bookEntity.isShareable());
        bookRepository.save(bookEntity);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication currentUser) {
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book Not Found"));

        UserEntity user = (UserEntity) currentUser.getPrincipal();

        if (!Objects.equals(bookEntity.getOwner(), user)) {
            throw new OperationNotPermittedException("You Can not Update others books archived status");
        }
        bookEntity.setShareable(!bookEntity.isArchived());
        bookRepository.save(bookEntity);
        return bookId;
    }

    public Integer borrowedBook(Integer bookId, Authentication currentUser) {
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book Not Found"));

        UserEntity user = (UserEntity) currentUser.getPrincipal();

        if (bookEntity.isArchived() || !bookEntity.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or " +
                    "not shareable");
        }

        if (Objects.equals(bookEntity.getOwner(), user)) {
            throw new OperationNotPermittedException("You Can Not Borrow Your Own Book");
        }

        final boolean isAlreadyBorrow = historyRepository.isAlreadyBorrowedByUser(bookId, user.getId());

        if (isAlreadyBorrow) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(bookEntity)
                .returned(false)
                .returnedApproved(false)
                .build();

        return historyRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication currentUser) {
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No Book Found By Id : " + bookId));

        if (bookEntity.isArchived() || !bookEntity.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }

        UserEntity user = (UserEntity) currentUser.getPrincipal();

        if (Objects.equals(bookEntity.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }

        BookTransactionHistory bookTransactionHistory = historyRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

        bookTransactionHistory.setReturned(true);
        return historyRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        UserEntity user = ((UserEntity) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }

        BookTransactionHistory bookTransactionHistory = historyRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));

        bookTransactionHistory.setReturnedApproved(true);
        return historyRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        UserEntity user = ((UserEntity) connectedUser.getPrincipal());
        String profilePicture = fileStoreService.saveFile(file, bookId, user.getId());
        book.setBookCover(profilePicture);
        bookRepository.save(book);
    }


}
