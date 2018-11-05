package nl.tjonahen.movie.moviegateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class MoviegatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviegatewayApplication.class, args);
    }

    @Bean
    public RouteLocator movieRoutes(RouteLocatorBuilder builder,
            @Value("${movies}") String movies,
            @Value("${reviews}") String reviews,
            @Value("${watchlist}") String watchlist) {
        return builder.routes()
                .route(p -> p
                .path("/api/movies")
                .uri(movies))
                .route(p -> p
                .path("/api/reviews")
                .uri(reviews))
                .route(p -> p
                .path("/api/watchlist")
                .uri(watchlist))
                .build();
    }

}

