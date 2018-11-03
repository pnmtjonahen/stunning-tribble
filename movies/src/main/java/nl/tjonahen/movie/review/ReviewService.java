package nl.tjonahen.movie.review;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewClient reviewClient;
    
    public String getReview(int movieId) {
        return Optional.ofNullable(reviewClient.getReview(movieId)).orElse(new Review()).getReview();
    }

}
