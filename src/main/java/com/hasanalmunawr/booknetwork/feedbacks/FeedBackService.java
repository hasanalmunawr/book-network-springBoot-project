package com.hasanalmunawr.booknetwork.feedbacks;

import com.hasanalmunawr.booknetwork.dto.PageResponse;
import com.hasanalmunawr.booknetwork.entity.BookEntity;
import com.hasanalmunawr.booknetwork.entity.UserEntity;
import com.hasanalmunawr.booknetwork.exception.EntityNotFoundException;
import com.hasanalmunawr.booknetwork.exception.OperationNotPermittedException;
import com.hasanalmunawr.booknetwork.repository.BookRepository;
import com.hasanalmunawr.booknetwork.repository.FeedBackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final FeedBackRepository feedBackRepository;
    private final BookRepository bookRepository;
    private final FeedBackMapper feedBackMapper;


    public Integer save(FeedBackRequest request, Authentication connectedUser) {
        BookEntity book = (BookEntity) bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable book");
        }
        UserEntity user = ((UserEntity) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }
        FeedbackEntity feedback = feedBackMapper.toFeedback(request);
        return feedBackRepository.save(feedback).getId();
    }

    @Transactional
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        UserEntity user = ((UserEntity) connectedUser.getPrincipal());
        Page<FeedbackEntity> feedbacks = feedBackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedBackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );

    }


}
