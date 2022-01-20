package utils;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.time.Duration.ofMillis;

@Service
public class GenericActions {

    @Autowired
    private WebDriverInstance webDriverInstance;

    private WebDriver webDriver;


    public boolean checkIfPresent(WebElement element) {
        this.webDriver = webDriverInstance.getWebDriver();
        Wait<WebDriver> wait = new FluentWait<>(this.webDriver).
                withTimeout(ofMillis(10000L))
                .pollingEvery(ofMillis(20))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
    }

    public void click(WebElement element) {
        checkIfPresent(element);
        element.click();
    }

    public void sendKeys(WebElement element, String text) {
        checkIfPresent(element);
        element.sendKeys(text);
    }

    public String getText(WebElement element){
        checkIfPresent(element);
        return element.getText();
    }
}
