package com.hasanalmunawr.booknetwork.feedbacks;

import com.hasanalmunawr.booknetwork.entity.BookEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedBackMapper {

    public FeedbackEntity toFeedback(FeedBackRequest request) {
        return FeedbackEntity.builder()
                .note(request.note())
                .comment(request.comment())
                .book(BookEntity.builder()
                        .id(request.bookId())
                        .shareable(false) // Not required and has no impact :: just to satisfy lombok
                        .archived(false) // Not required and has no impact :: just to satisfy lombok
                        .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(FeedbackEntity feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}
