import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class LoggerTest {

    private static final String TEST_DIRECTORY = "testOutput";
    private static final String TEST_FILE = "temp_log.txt";
    private Logger logger;

    @Before
    public void setUp() {
        // Ensure the test directory is created before tests
        File outputDir = new File(TEST_DIRECTORY);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        // Initialize the Logger
        logger = new Logger(TEST_DIRECTORY, TEST_FILE,true);
    }

    @After
    public void tearDown() {
        // Clean up the test file and directory after each test
        File testFile = new File(TEST_DIRECTORY, TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }

        File testDirectory = new File(TEST_DIRECTORY);
        if (testDirectory.exists()) {
            testDirectory.delete();
        }
    }

    @Test
    public void testCreateOutput() {
        File testFile = new File(TEST_DIRECTORY, TEST_FILE);

        // Check if the file exists after Logger initialization
        assertTrue("Log file should be created after Logger initialization.", testFile.exists());
    }

    @Test
    public void testWriteToFile() throws IOException {
        String testMessage = "This is a test log message.";

        // Write a message to the file
        logger.log(testMessage);

        // Verify the message is written
        File testFile = new File(TEST_DIRECTORY, TEST_FILE);
        List<String> lines = Files.readAllLines(Paths.get(testFile.getAbsolutePath()));

        assertTrue("Log file should contain the test message.", lines.contains(testMessage));
    }
}
