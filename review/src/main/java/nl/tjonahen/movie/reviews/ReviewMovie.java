package nl.tjonahen.movie.reviews;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
public class ReviewMovie {

    @Id
    @GeneratedValue
    private Long id;

    private Long movieId;
    private String title;
    private String review;
}
