package edu.generalpuzzle

import edu.generalpuzzle.core.Puzzle2D
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.mockito.BDDMockito.given

@WebMvcTest(PuzzlesController::class)
class ControllerTest {

    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @MockitoBean
    private lateinit var svc: Puzzle2D

    @Test
    fun solve() {
        given(svc.solve()).willReturn(1)

        mockMvcTester.get().uri("/solve/5_12")
            .exchange()
            .assertThat()
            .hasStatusOk()
            .hasContentType(MediaType.APPLICATION_JSON)
            .bodyJson()
            .hasPathSatisfying("$") { size -> size.assertThat().isEqualTo(1) }
    }

    @Test
    fun countSolutions() {
        given(svc.solve(countSolutions = true)).willReturn(2)

        mockMvcTester.get().uri("/solve/20_3?count=true")
            .exchange()
            .assertThat()
            .hasStatusOk()
            .hasContentType(MediaType.APPLICATION_JSON)
            .bodyJson()
            .hasPathSatisfying("$") { size -> size.assertThat().isEqualTo(2) }
    }

    @Test
    fun solve3d() {
        mockMvcTester.get().uri("/solve3d/3_4_5")
            .exchange()
            .assertThat()
            .hasStatusOk()
            .hasContentType(MediaType.APPLICATION_JSON)
            .bodyJson()
            .hasPathSatisfying("$") { size -> size.assertThat().isEqualTo(1) }
    }
}
