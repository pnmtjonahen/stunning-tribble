package nl.tjonahen.movies.moviegateway.themoviedb;

import nl.tjonahen.movies.moviegateway.genre.Genres;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@FeignClient(name = "genres", url = "https://api.themoviedb.org/3/genre/movie/list?api_key=${themoviedb.apikey}&language=en-US")
public interface GenreClient {
    @RequestMapping(method = RequestMethod.GET)
    Genres getGenres();
}
