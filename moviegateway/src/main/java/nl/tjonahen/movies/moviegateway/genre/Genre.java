package nl.tjonahen.movies.moviegateway.genre;

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
public class Genre {
    @Id
    private Long id;
    private String name;
}
