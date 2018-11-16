package nl.tjonahen.movie.review;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    
    @HystrixCommand(fallbackMethod = "onFallback")     
    public List<String> getReview(int movieId) {
        return Optional.ofNullable(reviewClient.getReview(movieId)).orElse(Arrays.asList(new Review()))
                .stream()
                .map(r -> r.getReview())
                .collect(Collectors.toList());
    }
    
    public List<String> onFallback(int movieId) {
        return Arrays.asList("");
    }

}
