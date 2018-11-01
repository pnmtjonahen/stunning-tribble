package nl.tjonahen.movie;

import nl.tjonahen.movie.themoviedb.GenreClient;
import nl.tjonahen.movie.genre.GenreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
public class MoviegatewayApplication extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(MoviegatewayApplication.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/actuator/**").permitAll();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner loadData(GenreRepository repository, GenreClient genreClient) {
        return (args) -> {
            genreClient.getGenres().getGenres().stream().forEach(repository::save);
        };
    }

}
