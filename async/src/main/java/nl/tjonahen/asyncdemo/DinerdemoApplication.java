package nl.tjonahen.asyncdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAsync
public class DinerdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DinerdemoApplication.class, args);
    }
}

@Slf4j
@RestController
@RequiredArgsConstructor
class DinerController {

    private final BarMicroService bms;
    private final KitchenMicroService kms;

    @GetMapping("/menu")
    public MenuKaart getMenuKaart() throws InterruptedException, ExecutionException {
        log.info("Getting menu...");

        CompletableFuture<List<Drankje>> drankjesCompleteFuture = bms.getMenu();
        CompletableFuture<List<Snack>> snaksCompletableFuture = kms.getMenu();
        CompletableFuture.allOf(drankjesCompleteFuture, snaksCompletableFuture).join();

        log.info("Menu items received...");

        return new MenuKaart(drankjesCompleteFuture.get(), snaksCompletableFuture.get());
    }
}

/*
{ 
drankjes : [{omschrijving:"cola"}],
snaks :[{}]
 */
@AllArgsConstructor
@Getter
@Setter
class MenuKaart {

    private List<Drankje> drankjes;
    private List<Snack> snaks;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Drankje {

    private String omschrijving;

}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Snack {

    private String omschrijving;
}

@Slf4j
@Service
class BarMicroService {

    @Async
    CompletableFuture<List<Drankje>> getMenu() throws InterruptedException {
        log.info("Get Drankjes");
        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(2000L);
        log.info("Return Drankjes menu");
        return CompletableFuture.completedFuture(new ArrayList<Drankje>() {
            {
                add(new Drankje("cola"));
            }
        });
    }
}

@Slf4j
@Service
class KitchenMicroService {

    @Async
    CompletableFuture<List<Snack>> getMenu() throws InterruptedException {
        log.info("Get Snack");
        // Artificial delay of 2s for demonstration purposes
        Thread.sleep(1000L);
        log.info("Return Snack menu");
        return CompletableFuture.completedFuture(new ArrayList<Snack>() {
            {
                add(new Snack("shanghai nootjes"));
            }
        });
    }

}
