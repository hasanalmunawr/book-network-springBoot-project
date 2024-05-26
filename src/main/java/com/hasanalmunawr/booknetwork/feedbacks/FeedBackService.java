package com.hasanalmunawr.booknetwork.feedbacks;

import com.hasanalmunawr.booknetwork.repository.BookRepository;
import com.hasanalmunawr.booknetwork.repository.FeedBackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final FeedBackRepository feedBackRepository;
    private final BookRepository bookRepository;

}
