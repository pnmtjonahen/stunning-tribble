package nl.tjonahen.movie.themoviedb;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Setter
public class MovieSearchResult {
    private int page;
    private int total_results;
    private int total_pages;
    private List<MovieResult> results;

}
