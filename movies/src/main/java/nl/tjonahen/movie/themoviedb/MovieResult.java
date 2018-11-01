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
public class MovieResult {
    private int vote_count;
    private int id;
    private boolean video;
    private int vote_average;
    private String title;
//popularity: 13.443,
    private String poster_path;
    private String original_language;
    private String original_title;
    private List<Long> genre_ids;
//private int genre_ids: [
//16,
//10751,
//35
//],
    private String backdrop_path;
//    private boolean adult;
    private String overview;
    private String release_date;
}
