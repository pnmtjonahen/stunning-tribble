package nl.tjonahen.movie.watchlist;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testFlow() throws Exception {
        this.mockMvc.perform(get("/api/watchlist"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));

        this.mockMvc.perform(post("/api/watchlist")
                .contentType(APPLICATION_JSON)
                .content("{\"id\":\"1\", \"title\":\"test title1\", \"description\":\"Sample moview\", \"watched\":\"false\"}")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("add to watchlist", requestFields(
                        fieldWithPath("id").description("The moviedb id."),
                        fieldWithPath("title").description("The moviedb title."),
                        fieldWithPath("description").description("The description."),
                        fieldWithPath("watched").description("The true|false indicator if the movies is watched or not.")
                )));

        this.mockMvc.perform(get("/api/watchlist"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"id\":1,\"title\":\"test title1\",\"description\":\"Sample moview\",\"watched\":false}]")))
                .andDo(document("watchlist",
                        responseFields(
                                fieldWithPath("[]").description("The watchlist"),
                                fieldWithPath("[].id").description("The moviedb id."),
                                fieldWithPath("[].title").description("The moviedb title."),
                                fieldWithPath("[].description").description("The description."),
                                fieldWithPath("[].watched").description("The true|false indicator if the movies is watched or not.")
                        )));

        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/api/watchlist/{id}?watched=true", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update watched movie",
                        pathParameters(
                                parameterWithName("id").description("The movie id of the watched")
                        )));

    }

}
