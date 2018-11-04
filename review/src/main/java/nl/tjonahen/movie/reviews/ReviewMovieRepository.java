package nl.tjonahen.movie.reviews;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
public interface ReviewMovieRepository extends JpaRepository<ReviewMovie, Long>{

    public List<ReviewMovie> findByMovieId(Long movieId);

}
