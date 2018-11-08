package nl.tjonahen.movie.moviegateway;

import java.util.Arrays;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurerComposite;

@Configuration
@SpringBootApplication
@EnableDiscoveryClient
public class MoviegatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviegatewayApplication.class, args);
    }

//    @Bean
//    public WebFluxConfigurer corsConfigurer() {
//        return new WebFluxConfigurerComposite() {
//
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")
//                        .allowedMethods("*");
//            }
//        };
//    }

    @Bean
    public CorsConfiguration corsConfiguration(RoutePredicateHandlerMapping routePredicateHandlerMapping) {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        Arrays.asList(HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST)
                .forEach(m -> corsConfiguration.addAllowedMethod(m));
        corsConfiguration.addAllowedOrigin("*");
        routePredicateHandlerMapping.setCorsConfigurations(new HashMap<String, CorsConfiguration>() 
        {{ put("/**", corsConfiguration); }}
        );
        return corsConfiguration;
    }

    @Bean
    public RouteLocator movieRoutes(RouteLocatorBuilder builder,
            @Value("${movies}") String movies,
            @Value("${reviews}") String reviews,
            @Value("${watchlist}") String watchlist) {
        return builder.routes()
                .route(p -> p.path("/api/movies").uri(movies))
                .route(p -> p.path("/api/reviews").uri(reviews))
                .route(p -> p.path("/api/watchlist").uri(watchlist))
                .build();
    }

}
