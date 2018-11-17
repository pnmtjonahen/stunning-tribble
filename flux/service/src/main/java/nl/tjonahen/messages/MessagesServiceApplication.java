package nl.tjonahen.messages;

import com.fasterxml.jackson.annotation.JsonView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@SpringBootApplication
@EnableAsync
public class MessagesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagesServiceApplication.class, args);
    }
}

@CrossOrigin
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
class MessagesController {

    private final MessageService service;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @JsonView(EventView.class)
    public Flux<Message> get() {
        return Flux.merge(service.getNewMessages(), service.getStoredMessages());
    }

    @GetMapping("/download")
    @JsonView(DownloadView.class)
    public List<Message> download() {
        return service.getAllMessages();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void add(@RequestBody Message msg) {
        service.add(msg);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addAll(@RequestBody List<Message> exportedMessages) {
        service.addAll(exportedMessages);
    }
    
    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteAll() {
        service.deleteAll();
    }

}

@Service
@RequiredArgsConstructor
class MessageService {

    private final MessageRepository repository;
    private final List<MessageSender> messageSenders = new ArrayList<>();

    public Flux<Message> getNewMessages() {
        return Flux.create((FluxSink<Message> fluxSink) -> {
            messageSenders.add(new MessageSender() {
                @Override
                public void send(Message m) {
                    fluxSink.next(m);
                }

                @Override
                public boolean isCancelled() {
                    return fluxSink.isCancelled();
                }
            });
        });
    }

    public Flux<Message> getStoredMessages() {
        return Flux.fromStream(repository.findAll().stream());
    }

    public List<Message> getAllMessages() {
        return repository.findAll();
    }

    @Async
    public void add(Message msg) {

        if ("/cat".equals(msg.getBody().trim())) {
            msg.setBody(fetchCat());
        }
        repository.save(msg);
        messageSenders.removeAll(messageSenders.stream().filter(MessageSender::isCancelled).collect(Collectors.toList()));
        messageSenders.forEach(ms -> ms.send(msg));

    }

    @Async
    public void addAll(List<Message> messages) {
        repository.saveAll(messages);
    }

    @Async
    public void deleteAll() {
        repository.deleteAll();
    }

    private String fetchCat() {
        try {
            File file = ResourceUtils.getFile("classpath:cat.txt");
            InputStream in = new FileInputStream(file);
            return StreamUtils.copyToString(in, Charset.defaultCharset());
        } catch (IOException ex) {
            return "no cats.";
        }
    }

}

interface MessageSender {
    void send(Message m);
    boolean isCancelled();
}

interface EventView {}
interface DownloadView {}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
class Message {

    @JsonView(EventView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonView({EventView.class, DownloadView.class})
    @Lob
    private String body;
}

interface MessageRepository extends JpaRepository<Message, Long> {
}
