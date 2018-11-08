package nl.tjonahen.movie.reviews;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewsController {

    private final ReviewMovieRepository repository;
    private final ReviewEventProducer reviewEventProducer;
    
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ReviewMovie> get() {
        return Flux.merge(reviewEventProducer, Flux.fromIterable(repository.findAll()));
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@RequestBody ReviewMovie newMovie) {
        reviewEventProducer.send(newMovie);
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

@Service
class ReviewEventProducer implements Publisher<ReviewMovie> {

    private final List<ReviewEventSubscription> subscribtions = new ArrayList<>();

    @Override
    public void subscribe(Subscriber<? super ReviewMovie> s) {
        final ReviewEventSubscription messageSubscription = new ReviewEventSubscription(s);
        s.onSubscribe(messageSubscription);
        subscribtions.add(messageSubscription);
    }

    public void send(ReviewMovie m) {

        subscribtions.forEach(s -> {
            s.onNext(m);
        });
    }

}

@Slf4j
@RequiredArgsConstructor
class ReviewEventSubscription implements Subscription {

    private final Subscriber<? super ReviewMovie> subscriber;
    private long count = 0;

    @Override
    public void request(long l) {
        this.count = l;
    }

    @Override
    public void cancel() {
        this.count = 0;
    }

    public void onNext(ReviewMovie m) {
        if (this.count-- > 0) {
            subscriber.onNext(m);
        }
    }

}