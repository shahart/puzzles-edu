package edu.generalpuzzle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

/**
 * http://localhost:8080/graphiql
 */

@GraphQlTest(PuzzlesController.class)
public class PuzzlesControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    void solve() {
        this.graphQlTester
                .documentName("MyQuery")
                .variable("id", "20_3")
                .execute()
                .path("puzzlesById")
                .matchesJson("""
                    {
                        "id": "20_3"
                    }
                """);
    }

    @Test
    void solveWithTotal() {
        this.graphQlTester
                .documentName("MyQuery_total")
                .variable("id", "20_3")
                .execute()
                .path("puzzlesById")
                .matchesJson("""
                    {
                        "id": "20_3",
                        "solutions": {
                            "total": 2
                        }
                    }
                """);
    }
}
