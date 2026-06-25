package edu.generalpuzzle

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PuzzlesApplication

fun main(args: Array<String>) {
    SpringApplication.run(PuzzlesApplication::class.java, *args)
}
