package pageobject;

import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import utils.WebDriverInstance;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@Scope("prototype")
public class GoogleTranslatePage {

    @FindBy(css = "#sdgBod")
    private WebElement googleTranslateLogo;
    @FindBy(css = "[jsname=BvKnce]")
    private WebElement textArea;
    @FindBy(css = "#yDmH0d .Llmcnf > span")
    private WebElement language;
    @FindBy(css = "#yDmH0d .aCQag .OoYv6d > div > div.fMHXgc.qkH7ie > input")
    private WebElement languageSearch;
    @FindBy(css = "a.ita-kd-icon-button.ita-kd-dropdown.ita-kd-right")
    private WebElement inputType;
    @FindBy(css = "#yDmH0d div.ccvoYb  c-wiz > div.zXU7Rb > c-wiz > div:nth-child(2) > button")
    private WebElement languageDropdown;
    @FindBy(css = "#K16 > span")
    private WebElement shiftButton;
    @FindBy(css = "a.ita-kd-icon-button.ita-kd-inputtool-icon.ita-kd-small.ita-kd-left")
    private WebElement englishKbd;
    @FindBy(css = "#yDmH0d > ul > li:nth-child(2)")
    private WebElement russianKbd;
    @FindBy(css = "[class*=\"vk-btn\"]")
    private List<WebElement> kbd;

    @Autowired
    private WebDriverInstance webDriverInstance;

    @Autowired
    public GoogleTranslatePage(WebDriverInstance webDriverInstance) {
        PageFactory.initElements(webDriverInstance.getWebDriver(), this);
    }

    public List<WebElement> getKbdButtons(List<WebElement> list) {
        List<WebElement> elementList = new ArrayList<>();

        for (WebElement e : list) {
            if (e.getText().matches("^[A-Z]+$") || e.getText().matches("[ЁА-Я]")) {
                elementList.add(e);
            }
        }
        return elementList;
    }
}

