package edu.generalpuzzle.main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PuzzleExceptionTest {

    @AfterEach
    void resetTraceSetting() {
        PuzzleException.setAddTrace(false);
    }

    @Test
    void traceSettingIsScopedToTheCurrentThread() throws InterruptedException {
        PuzzleException.setAddTrace(true);
        AtomicBoolean workerTraceSetting = new AtomicBoolean(true);
        Thread worker = new Thread(() -> workerTraceSetting.set(PuzzleException.isAddTrace()));

        worker.start();
        worker.join();

        assertTrue(PuzzleException.isAddTrace());
        assertFalse(workerTraceSetting.get());
    }
}
