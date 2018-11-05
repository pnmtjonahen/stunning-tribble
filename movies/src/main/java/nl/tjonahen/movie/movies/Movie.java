package nl.tjonahen.movie.movies;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private int id;
    private String title;
    private String description;
    private String poster;
    private String backdrop;
    private List<String> review;
    private List<String> genre;
}
