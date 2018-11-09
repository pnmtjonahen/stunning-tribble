package nl.tjonahen.movie.review;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@FeignClient(name = "reviews", decode404 = true)
public interface ReviewClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/review/movie/{id}")
    List<Review> getReview(@PathVariable("id") int movieId);
}
