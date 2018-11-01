package nl.tjonahen.movie.reviews;

import static org.hamcrest.CoreMatchers.containsString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Philippe Tjon - A - Hen philippe@tjonahen.nl
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReviewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFlow() throws Exception {
        this.mockMvc.perform(get("/api/reviews")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("[]")));
        
        this.mockMvc.perform(post("/api/reviews")
                .contentType(APPLICATION_JSON)
                .content("{\"movieId\":\"1\", \"title\":\"test title1\", \"review\":\"Just a test review\"}")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/api/reviews")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("[{\"id\":1,\"movieId\":1,\"title\":\"test title1\",\"review\":\"Just a test review\"}]")));
        
    }
}
