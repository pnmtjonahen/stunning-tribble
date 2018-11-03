package nl.tjonahen.movie.reviews;

import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView(PublicView.class)
    private Long movieId;
    @JsonView(PublicView.class)
    private String title;
    @JsonView(PublicView.class)
    private String review;
}
