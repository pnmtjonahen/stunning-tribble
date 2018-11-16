package nl.tjonahen.messages;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MessagesFrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagesFrontApplication.class, args);
    }
}


@RestController
class ConfigurationJsController {
    @Value("${server.http}")
    private String httpServer;

    @GetMapping(value = "/configuration.js", produces = "application/javascript")
    public String getConfig() {
        return String.format("const config = {\n"
                + "    http_server: \"%s\"\n"
                + "};", httpServer);

    }
}

