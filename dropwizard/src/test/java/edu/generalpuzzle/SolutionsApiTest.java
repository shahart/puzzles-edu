package edu.generalpuzzle;

import edu.generalpuzzle.api.Solutions;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.client.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class SolutionsApiTest {

    private static DropwizardAppExtension<PuzzlesConfiguration> EXT = new DropwizardAppExtension<>(PuzzlesApplication.class);

    @Test
    void solveGet() {
        Client client = EXT.client();

        Solutions solutions = client.target(String.format("http://localhost:%d/solve", EXT.getLocalPort()))
                .queryParam("id", "12_5")
                .request()
                .get(Solutions.class);

        assertEquals(1010, solutions.getRes());
    }
}
