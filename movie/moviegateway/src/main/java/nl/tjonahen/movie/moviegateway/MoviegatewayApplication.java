package nl.tjonahen.movie.moviegateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

@Configuration
@SpringBootApplication
public class MoviegatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviegatewayApplication.class, args);
    }

    @Bean
    public RouteLocator movieRoutes(RouteLocatorBuilder builder,
            @Value("${front}") String front,
            @Value("${movie}") String movie,
            @Value("${review}") String review,
            @Value("${watchlist}") String watchlist) {
        return builder.routes()
                .route(p -> p.path("/").uri(front))
                .route(p -> p.path("/css/*.*").uri(front))
                .route(p -> p.path("/img/*.*").uri(front))
                .route(p -> p.path("/js/*.js").uri(front))
                .route(p -> p.path("/api/movies").uri(movie))
                .route(p -> p.path("/api/review")
                                    .filters(f -> f.hystrix(c -> c.setName("review").setFallbackUri("forward:/fallbackreview")))
                            .uri(review))
                .route(p -> p.path("/api/watchlist").uri(watchlist))
                .build();
    }

    @RequestMapping(path = "/fallbackreview", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> fallback() {
        return Flux.empty();
    }
}
