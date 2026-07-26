package edu.generalpuzzle

import edu.generalpuzzle.core.Puzzle2D
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope

@SpringBootApplication
class PuzzlesApplication {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun puzzle2D() = Puzzle2D()
}

fun main(args: Array<String>) {
    SpringApplication.run(PuzzlesApplication::class.java, *args)
}
