package nl.tjonahen.movie.reviews;

import static org.hamcrest.CoreMatchers.containsString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class ReviewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFlow() throws Exception {
        this.mockMvc.perform(get("/api/reviews")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("[]")))
                .andDo(MockMvcRestDocumentation.document("reviews"));

        this.mockMvc.perform(post("/api/reviews")
                .contentType(APPLICATION_JSON)
                .content("{\"movieId\":\"1\", \"title\":\"test title1\", \"review\":\"Just a test review\"}")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentation.document("add review", requestFields(
                        fieldWithPath("movieId").description("The moviedb id."),
                        fieldWithPath("title").description("The moviedb title."),
                        fieldWithPath("review").description("The review.")
                )));

        this.mockMvc.perform(get("/api/reviews")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("[{\"id\":1,\"movieId\":1,\"title\":\"test title1\",\"review\":\"Just a test review\"}]")))
                .andDo(MockMvcRestDocumentation.document("reviews"));

        this.mockMvc.perform(get("/api/reviews/movie/1")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("{\"movieId\":1,\"title\":\"test title1\",\"review\":\"Just a test review\"}")))
                .andDo(MockMvcRestDocumentation.document("get review", responseFields(
                        fieldWithPath("movieId").description("The moviedb id."),
                        fieldWithPath("title").description("The moviedb title."),
                        fieldWithPath("review").description("The review.")
                )));
        
        

    }
}
