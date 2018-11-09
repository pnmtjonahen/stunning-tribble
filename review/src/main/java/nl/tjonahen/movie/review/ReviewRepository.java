package nl.tjonahen.movie.review;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
public interface ReviewRepository extends JpaRepository<Review, Long>{

    public List<Review> findByMovieId(Long movieId);
    
    @Query("SELECT r FROM Review r ORDER BY r.createdon DESC")
    public List<Review> findTop5OrderByCreatedon();

}
