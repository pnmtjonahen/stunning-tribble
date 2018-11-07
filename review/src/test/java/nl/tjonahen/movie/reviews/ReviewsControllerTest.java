package nl.tjonahen.movie.reviews;

import static org.hamcrest.CoreMatchers.containsString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
    public void testCrossOrigin() throws Exception {
        this.mockMvc.perform(options("/api/reviews")
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "http://www.tjonahen.nl"))
                .andDo(print())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET"));

    }
    

    @Test
    public void testFlow() throws Exception {
        this.mockMvc.perform(get("/api/reviews"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));

        this.mockMvc.perform(post("/api/reviews")
                .contentType(APPLICATION_JSON)
                .content("{\"movieId\":\"1\", \"title\":\"test title1\", \"review\":\"Just a test review\"}")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("add review", requestFields(
                        fieldWithPath("movieId").description("The moviedb id."),
                        fieldWithPath("title").description("The moviedb title."),
                        fieldWithPath("review").description("The review.")
                )));

        this.mockMvc.perform(get("/api/reviews"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"id\":1,\"movieId\":1,\"title\":\"test title1\",\"review\":\"Just a test review\"}]")))
                .andDo(document("reviews",
                        responseFields(
                                fieldWithPath("[]").description("A list of reviews"),
                                fieldWithPath("[].id").description("The review id."),
                                fieldWithPath("[].movieId").description("The moviedb id."),
                                fieldWithPath("[].title").description("The moviedb title."),
                                fieldWithPath("[].review").description("The review.")
                        )));

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/movie/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"movieId\":1,\"title\":\"test title1\",\"review\":\"Just a test review\"}]")))
                .andDo(document("get reviews",
                        pathParameters(
                                parameterWithName("id").description("The movieId of the reviews to get")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("A list of reviews for the requested movieId"),
                                fieldWithPath("[].movieId").description("The moviedb id."),
                                fieldWithPath("[].title").description("The moviedb title."),
                                fieldWithPath("[].review").description("The review.")
                        )));

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"movieId\":1,\"title\":\"test title1\",\"review\":\"Just a test review\"}")))
                .andDo(document("get review",
                        pathParameters(
                                parameterWithName("id").description("The review id of the review to get")
                        ),
                        responseFields(
                                fieldWithPath("movieId").description("The moviedb id."),
                                fieldWithPath("title").description("The moviedb title."),
                                fieldWithPath("review").description("The review.")
                        )));

    }
}
