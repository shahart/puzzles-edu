package edu.generalpuzzle

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.ApplicationContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads(applicationContext: ApplicationContext) {
        Assertions.assertNotNull(applicationContext)
        Assertions.assertTrue(applicationContext.beanDefinitionCount > 0)
    }

    @Test
    fun solvesMultiplePuzzlesConcurrently() {
        val problemIds = listOf("12_5", "10_6", "15_4", "20_3")
        val ready = CountDownLatch(problemIds.size)
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(problemIds.size)

        try {
            val results = problemIds.associateWith { problemId ->
                executor.submit<Int> {
                    ready.countDown()
                    start.await()
                    mockMvc.get("/solve/{problemId}", problemId)
                        .andExpect {
                            status { isOk() }
                        }
                        .andReturn()
                        .response
                        .contentAsString
                        .toInt()
                }
            }

            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS), "Caller threads did not become ready")
            start.countDown()

            results.forEach { (problemId, result) ->
                Assertions.assertEquals(1, result.get(10, TimeUnit.SECONDS), "Unexpected result for $problemId")
            }
        } finally {
            start.countDown()
            executor.shutdownNow()
        }
    }
}
