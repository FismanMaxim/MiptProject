import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {
    @Test
    void successfulLaunch () {
        assertDoesNotThrow(() -> Main.main(null));
    }
}