package nl.tjonahen.movie.themoviedb;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Component
@RequiredArgsConstructor
public class MovieSearchService {

    @Value("${themoviedb.apikey}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    
    @HystrixCommand(fallbackMethod = "onFallback", commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")})    
    public List<MovieResult> search(String query) {
        return restTemplate.getForObject(
                "https://api.themoviedb.org/3/search/movie?api_key={apiKey}&language=en-US&query={query}&page=1&include_adult=false",
                 MovieSearchResult.class, apiKey, query).getResults();
    }
    
    public List<MovieResult> onFallback(String query) {
        
        return new ArrayList<>();
    }
}
