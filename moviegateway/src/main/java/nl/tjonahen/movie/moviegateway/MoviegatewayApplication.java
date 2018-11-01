package nl.tjonahen.movie.moviegateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(UriConfiguration.class)
//@RestController
public class MoviegatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviegatewayApplication.class, args);
    }
    
    @Bean
    public RouteLocator movieRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
        return builder.routes()
            .route(p -> p
                .path("/api/movies")
                .uri(uriConfiguration.getMovies()))
            .route(p -> p
                .path("/api/reviews")
                .uri(uriConfiguration.getReviews()))
            .route(p -> p
                .path("/api/watchlist")
                .uri(uriConfiguration.getWatchlist()))
//            .route(p -> p
//                .host("*.hystrix.com")
//                .filters(f -> f
//                    .hystrix(config -> config
//                        .setName("mycmd")
//                        .setFallbackUri("forward:/fallback")))
//                .uri(moviesUri))
            .build();
    }

//    @RequestMapping("/fallback")
//    public Mono<String> fallback() {
//        return Mono.just("fallback");
//    }    
}
@ConfigurationProperties
@Getter
@Setter        
class UriConfiguration {
    private String movies = "http://localhost:8081";
    private String reviews = "http://localhost:8082";
    private String watchlist = "http://localhost:8083";

}