package nl.tjonahen.movie.reviews;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewsController {

    private final ReviewMovieRepository repository;
    @GetMapping
    public List<ReviewMovie> get() {
        return repository.findAll();
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@RequestBody ReviewMovie newMovie) {
        repository.save(newMovie);
    }
    
    @GetMapping("/movie/{movieid}")
    @JsonView(PublicView.class)    
    public List<ReviewMovie> get(@PathVariable("movieid") Long movieId) {
        return repository.findByMovieId(movieId);
    }
    
    @GetMapping("/{id}")
    @JsonView(PublicView.class)    
    public ResponseEntity<ReviewMovie> getReview(@PathVariable("id") Long id) {
        final Optional<ReviewMovie> review = repository.findById(id);
        if (review.isPresent()) {
            return ResponseEntity.ok(review.get());
        }
        return ResponseEntity.notFound().build();
    }
}
