package nl.tjonahen.movie.watchlist;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
public interface WatchlistRepository extends JpaRepository<WatchlistMovie, Long> {

}
