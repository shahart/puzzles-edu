package edu.generalpuzzle

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class ApplicationTests {

    @Test
    fun contextLoads(applicationContext: ApplicationContext) {
        Assertions.assertNotNull(applicationContext)
        Assertions.assertTrue(applicationContext.beanDefinitionCount > 0)
    }
}
