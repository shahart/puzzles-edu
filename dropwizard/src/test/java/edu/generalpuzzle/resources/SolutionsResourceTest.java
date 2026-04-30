package edu.generalpuzzle.resources;

import edu.generalpuzzle.api.Solutions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SolutionsResourceTest {

    private SolutionsResource resource = new SolutionsResource();

    @Test
    void test12_5() {
        Solutions result = resource.solve("12_5");
        assertEquals(1010, result.getRes());
    }
}
