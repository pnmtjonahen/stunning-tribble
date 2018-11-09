package nl.tjonahen.movie.front;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class FrontApplication extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(FrontApplication.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/actuator/**").permitAll();
    }
}

@RestController
@RequestMapping("/")
class ConfigController {

    @Value("${server.http}")
    private String httpServer;

    @CrossOrigin
    @GetMapping(value = "/configuration.js", produces = "application/javascript")
    public String getConfig() {
        return String.format("const config = {\n"
                + "    http_server: \"%s\"\n"
                + "};", httpServer);

    }

}
