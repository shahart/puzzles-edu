package edu.generalpuzzle;

import edu.generalpuzzle.solver.PuzzleSolverService;
import edu.generalpuzzle.solver.SolverRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Controller.class)
@Import({PuzzleSolverService.class, ControllerWebIntegrationTest.SolverConfiguration.class})
class ControllerWebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void servesStructuredResultThroughTheHttpMapping() throws Exception {
        mockMvc.perform(get("/solve/2d_ascii/4_5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value("2d_ascii"))
                .andExpect(jsonPath("$.totalSolutions").value(42));
    }

    @Test
    void mapsLegacySolverErrorsToBadRequest() throws Exception {
        mockMvc.perform(get("/solve/broken"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unable to solve puzzle 'broken': invalid puzzle"));
    }

    @TestConfiguration
    static class SolverConfiguration {

        @Bean
        SolverRunner solverRunner() {
            return arguments -> "broken".equals(arguments[0]) ? "invalid puzzle" : "42";
        }
    }
}
