package nl.tjonahen.movie.watchlist;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Entity
@Getter
@Setter        
public class WatchlistMovie {
    @Id
    private Long id;
    private String title;
    private String description;
    private boolean watched;
}
