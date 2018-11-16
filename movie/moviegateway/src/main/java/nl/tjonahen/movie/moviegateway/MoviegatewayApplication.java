package nl.tjonahen.movie.moviegateway;

import java.util.Arrays;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@SpringBootApplication
@RestController
public class MoviegatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviegatewayApplication.class, args);
    }

    @Bean
    public CorsConfiguration corsConfiguration(RoutePredicateHandlerMapping routePredicateHandlerMapping) {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        Arrays.asList(HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST)
                .forEach(m -> corsConfiguration.addAllowedMethod(m));
        corsConfiguration.addAllowedOrigin("*");
        routePredicateHandlerMapping.setCorsConfigurations(
                new HashMap<String, CorsConfiguration>() {
            {
                put("/**", corsConfiguration);
            }
        }
        );
        return corsConfiguration;
    }

    @Bean
    public RouteLocator movieRoutes(RouteLocatorBuilder builder,
            @Value("${movie}") String movie,
            @Value("${review}") String review,
            @Value("${watchlist}") String watchlist) {
        return builder.routes()
                .route(p -> p.path("/api/movies").uri(movie))
                .route(p -> p
                .path("/api/review")
                .filters(f -> f.hystrix(c -> c
                .setName("review")
                .setFallbackUri("forward:/fallbackreview")))
                .uri(review))
                .route(p -> p.path("/api/watchlist").uri(watchlist))
                .build();
    }

    @RequestMapping(path = "/fallbackreview", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> fallback() {
        return Flux.empty();
    }
}
