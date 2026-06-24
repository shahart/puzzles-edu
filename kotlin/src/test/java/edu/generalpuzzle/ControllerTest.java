package edu.generalpuzzle;

import edu.generalpuzzle.core.Puzzle2D;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;

@WebMvcTest(PuzzlesController.class)
class ControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private Puzzle2D svc;

    @Test
    void solve() {
        given(svc.solve()).willReturn(1);

        mockMvcTester.get().uri("/solve/5_12")
                .exchange()
                .assertThat()
                .hasStatusOk()
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson()
                .hasPathSatisfying("$", size -> size.assertThat().isEqualTo(1));
        // .hasPathSatisfying("$[0]", duration -> duration.assertThat().isEqualTo(LocalTime.parse("07:00"))); // TODO
    }

}