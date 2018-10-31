package nl.tjonahen.movies.moviegateway.movies;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nl.tjonahen.movies.moviegateway.genre.GenreRepository;
import nl.tjonahen.movies.moviegateway.themoviedb.MovieSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieResource {
    
    private final MovieSearchService movieSearchService;
    private final GenreRepository genreRepository;
    
    @GetMapping
    public List<Movie> search(@RequestParam("query") String query) {
        return movieSearchService.search(query).stream().map(m -> {
            return new Movie.MovieBuilder().genre(genreRepository.findAllById(m.getGenre_ids()).stream().map(g -> g.getName()).collect(Collectors.toList())).title(m.getOriginal_title()).description(m.getOverview()).id(m.getId()).build();
        }).collect(Collectors.toList());
    }
}
