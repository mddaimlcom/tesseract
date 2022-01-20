package pageobject;

import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import utils.WebDriverInstance;


@Getter
@Component
@Scope("prototype")
public class FakeCaptchaPage {

    @FindBy(css = "#header > div > div > div.col-lg-8 > div > h1")
    private WebElement fakeCaptchaLogo;
    @FindBy(css = "#words")
    private WebElement captchaTextField;
    @FindBy(css = "#captcha-create button")
    private WebElement createCaptchaButton;
    @FindBy(css = "#proceed_link")
    private WebElement proceedLink;
    @FindBy(css = "[alt=\"WORDS ONLY\"]")
    private WebElement resultDisplay;
    @FindBy(css = "#resultdisplay > div > div > div:nth-child(3) > div > ul > li > a")
    private WebElement regenerate;

    @Autowired
    WebDriverInstance webDriverInstance;

    @Autowired
    public FakeCaptchaPage(WebDriverInstance webDriverInstance) {
        PageFactory.initElements(webDriverInstance.getWebDriver(), this);
    }
}
