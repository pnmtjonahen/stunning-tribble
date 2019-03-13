package nl.tjonahen.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.springframework.web.util.HtmlUtils;
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
    public Flux<JsonMessage> get() {
        return Flux.merge(service.getNewMessages(), service.getStoredMessages());
    }

    @GetMapping("/download")
    public List<JsonMessage> download() {
        return service.getAllMessages();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void add(@RequestBody JsonMessage msg) {
        service.add(msg);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addAll(@RequestBody List<JsonMessage> exportedMessages) {
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

    /*
     * Creates a message sink for new incomming messages. 
     */
    public Flux<JsonMessage> getNewMessages() {
        return Flux.create((FluxSink<JsonMessage> fluxSink) -> {
            messageSenders.add(new MessageSender() {
                @Override
                public void send(JsonMessage m) {
                    fluxSink.next(m);
                }

                @Override
                public boolean isCancelled() {
                    return fluxSink.isCancelled();
                }
            });
        });
    }

    /*
    * Get all stored messages as a flux
    */
    public Flux<JsonMessage> getStoredMessages() {
        return Flux.fromStream(repository.findAll().stream().map(JsonMessage::fromMessage));
    }

    /*
    * Get all messages as one single list
    */
    public List<JsonMessage> getAllMessages() {
        return repository.findAll().stream().map(JsonMessage::fromMessage).collect(Collectors.toList());
    }

    @Async
    public void add(JsonMessage msg) {

        if ("/cat".equals(msg.getBody().trim())) {
            msg.setBody(fetchCat());
        }
        repository.save(Message.fromJsonMessage(msg));
        messageSenders.removeAll(messageSenders.stream().filter(MessageSender::isCancelled).collect(Collectors.toList()));
        messageSenders.forEach(ms -> ms.send(msg.sanitize()));

    }

    @Async
    public void addAll(List<JsonMessage> messages) {
        repository.saveAll(messages.stream().map(Message::fromJsonMessage).collect(Collectors.toList()));
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
    void send(JsonMessage m);
    boolean isCancelled();
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class JsonMessage {
    private String body;
    
    public static JsonMessage fromMessage(Message m) {
        return new JsonMessage(HtmlUtils.htmlEscape(m.getBody()));
    }
    
    public JsonMessage sanitize() {
        this.body = HtmlUtils.htmlEscape(body);
        return this;
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
class Message {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String body;
    
    public static Message fromJsonMessage(JsonMessage m) {
        final Message msg = new Message();
        msg.setBody(m.getBody());
        return msg;
    }
}

interface MessageRepository extends JpaRepository<Message, Long> {
}
