package nl.tjonahen.movie.watchlist;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistRepository repository;
    private final WatchlistMovieEventProducer watchlistMovieProducer;

    @CrossOrigin
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<WatchlistMovie> get() {
        return Flux.concat(watchlistMovieProducer, Flux.fromIterable(repository.findAll()));
    }

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@RequestBody WatchlistMovie newMovie) {
        watchlistMovieProducer.send(newMovie);
        repository.save(newMovie);
    }

    @PutMapping("/{id}")
    public void watched(@PathVariable("id") Long id, @RequestParam("watched") boolean watched) {
        WatchlistMovie wm = repository.findById(id).get();
        wm.setWatched(watched);
        repository.save(wm);
    }
}

@Service
class WatchlistMovieEventProducer implements Publisher<WatchlistMovie> {

    private final List<WatchlistMovieWventSubscription> subscribtions = new ArrayList<>();

    @Override
    public void subscribe(Subscriber<? super WatchlistMovie> s) {
        final WatchlistMovieWventSubscription messageSubscription = new WatchlistMovieWventSubscription(s);
        s.onSubscribe(messageSubscription);
        subscribtions.add(messageSubscription);
    }

    public void send(WatchlistMovie m) {

        subscribtions.forEach(s -> {
            s.onNext(m);
        });
    }

}

@Slf4j
@RequiredArgsConstructor
class WatchlistMovieWventSubscription implements Subscription {

    private final Subscriber<? super WatchlistMovie> subscriber;
    private long count = 0;

    @Override
    public void request(long l) {
        this.count = l;
    }

    @Override
    public void cancel() {
        this.count = 0;
    }

    public void onNext(WatchlistMovie m) {
        if (this.count-- > 0) {
            subscriber.onNext(m);
        }
    }

}
