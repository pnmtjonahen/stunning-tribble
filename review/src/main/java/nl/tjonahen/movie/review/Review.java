package nl.tjonahen.movie.review;

import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDateTime;
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
public class Review {

    @Id
    @GeneratedValue
    private Long id;
    @JsonView(PublicView.class)
    private Long movieId;
    @JsonView(PublicView.class)
    private String title;
    @JsonView(PublicView.class)
    private String review;
    
    private LocalDateTime createdon;
}
