package nl.tjonahen.movie.watchlist;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class WatchlistController {
    private final WatchlistRepository repository;
    
    @GetMapping
    public List<WatchlistMovie> get() {
        return repository.findAll();
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@RequestBody WatchlistMovie newMovie) {
        repository.save(newMovie);
    }
    
    @PutMapping("{id}")
    public void watched(@PathVariable("id") Long id, @RequestParam("watched") boolean watched) {
        WatchlistMovie wm = repository.findById(id).get();
        wm.setWatched(watched);
        repository.save(wm);
    }
}
