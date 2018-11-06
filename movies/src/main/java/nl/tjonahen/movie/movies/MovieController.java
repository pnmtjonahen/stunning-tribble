package nl.tjonahen.movie.movies;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nl.tjonahen.movie.genre.GenreRepository;
import nl.tjonahen.movie.review.ReviewService;
import nl.tjonahen.movie.themoviedb.MovieSearchService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    
    private final MovieSearchService movieSearchService;
    private final GenreRepository genreRepository;
    private final ReviewService reviewService;
    
    @CrossOrigin
    @GetMapping
    public List<Movie> search(@RequestParam("query") String query) {
        return movieSearchService.search(query).stream().map(m -> {
            return new Movie.MovieBuilder()
                    .review(reviewService.getReview(m.getId()))
                    .genre(genreRepository.findAllById(m.getGenre_ids()).stream().map(g -> g.getName()).collect(Collectors.toList()))
                    .poster(m.getPoster_path() == null ? "/img/na.jpg" : "https://image.tmdb.org/t/p/w200" + m.getPoster_path() )
                    .backdrop(m.getBackdrop_path() == null ? "/img/na-backdrop.png" : "https://image.tmdb.org/t/p/w500" + m.getBackdrop_path() )
                    .title(m.getOriginal_title())
                    .description(m.getOverview())
                    .id(m.getId())
                    .build();
        }).collect(Collectors.toList());
    }
}
