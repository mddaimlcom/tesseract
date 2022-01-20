package utils;

import lombok.Getter;
import net.bytebuddy.utility.RandomString;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.time.Duration.ofMillis;

@Getter
@Service
public class Ocr {

    @Autowired
    Tesseract tesseract;

    @Autowired
    WebDriverInstance webDriverInstance;

    public Ocr(Tesseract tesseract) {
        this.tesseract = tesseract;
        this.tesseract.setDatapath("src/test/resources/ocr/tessdata");
    }

    public BufferedImage takeNodeScreenshots(WebElement element) throws IOException {
        Wait<WebDriver> wait = new FluentWait<>(webDriverInstance.getWebDriver()).
                withTimeout(ofMillis(10000L))
                .pollingEvery(ofMillis(20))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        File sc = element.getScreenshotAs(OutputType.FILE);
        BufferedImage bufferedImage = ImageIO.read(sc);
        return getScaledImage(bufferedImage, 100, 100).getSubimage(25,25,50,50);
    }

    public String getTextValue(WebElement element, String language) throws TesseractException, IOException {

        String character = null;
        String whitelist = null;
        tesseract.setOcrEngineMode(0);

        if (language.equals("English")) {
            whitelist = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            tesseract.setTessVariable("tessedit_char_whitelist", whitelist);
        } else {
            whitelist = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
            tesseract.setTessVariable("tessedit_char_whitelist", whitelist);
            tesseract.setLanguage("rus");
        }

        BufferedImage screenshot = takeNodeScreenshots(element);

        String scannedChar = tesseract.doOCR(screenshot);

        if (scannedChar.isEmpty()) {
            character = "I";
            // exception for 'I' character, couldn't be scanned correctly
            ImageIO.write(screenshot, "png", new File("C:\\images\\notScanned\\" + RandomString.make(5) + ".png"));
        } else {
            character = String.valueOf(scannedChar.charAt(0));
            ImageIO.write(screenshot, "png", new File("C:\\images\\scanned\\" + RandomString.make(5) + ".png"));
        }
        if (whitelist.contains(character)) {
            return character;
        } else {
            return null;
        }
    }

    public void clickOnElementsByText(String text, List<WebElement> elementList, String language) throws
            TesseractException, IOException {
        Map<String, WebElement> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        HashSet<String> keysSet = new HashSet<>();

        for (WebElement e : elementList) {
            String key = getTextValue(e, language);
            if (key != null) {
                if (!keysSet.contains(key)) {
                    keysSet.add(key);
                } else {
                    clickOnElementsByText(text, elementList, language);
                }
                map.put(key, e);
            }
        }

        for (char c : text.toCharArray()
        ) {
            map.get(String.valueOf(c)).click();
        }
    }

    public static BufferedImage getScaledImage(BufferedImage image, int width, int height) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
    }
}
