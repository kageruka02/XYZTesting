package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures browser screenshots and attaches them to both the file system
 * and the Allure report for easy failure diagnosis.
 */
public class ScreenshotUtils {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final ConfigReader config = ConfigReader.getInstance();

    private ScreenshotUtils() {}

    /**
     * Takes a screenshot, saves it to target/screenshots/, and attaches it to Allure.
     *
     * @param driver   the active WebDriver instance
     * @param testName used as part of the file name for easy identification
     * @return absolute path to the saved screenshot file, or null if capture failed
     */
    public static String captureAndAttach(WebDriver driver, String testName) {
        if (driver == null) {
            log.warn("Screenshot skipped: driver is null");
            return null;
        }
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            // Attach to Allure report
            Allure.addAttachment("Screenshot – " + testName, "image/png",
                    new ByteArrayInputStream(screenshot), ".png");

            // Save to disk for local inspection
            String fileName = sanitize(testName) + "_" + LocalDateTime.now().format(TIMESTAMP) + ".png";
            Path dir = Paths.get(config.getScreenshotDir());
            Files.createDirectories(dir);
            Path dest = dir.resolve(fileName);
            Files.write(dest, screenshot);
            log.info("Screenshot saved: {}", dest.toAbsolutePath());
            return dest.toAbsolutePath().toString();

        } catch (Exception e) {
            log.error("Failed to capture screenshot for '{}': {}", testName, e.getMessage());
            return null;
        }
    }

    /** Replaces characters that are invalid in file names with underscores. */
    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
