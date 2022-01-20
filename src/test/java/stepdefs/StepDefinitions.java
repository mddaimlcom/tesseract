package stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import pageobject.FakeCaptchaPage;
import pageobject.GoogleTranslatePage;
import utils.GenericActions;
import utils.Ocr;
import utils.ScenarioContext;
import utils.WebDriverInstance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@ContextConfiguration(classes = CucumberSpringConfiguration.class)
@CucumberContextConfiguration
public class StepDefinitions {

    @Autowired
    WebDriverInstance webDriverInstance;
    @Autowired
    GoogleTranslatePage googleTranslatePage;
    @Autowired
    FakeCaptchaPage fakeCaptchaPage;
    @Autowired
    ScenarioContext scenarioContext;
    @Autowired
    GenericActions genericActions;
    @Autowired
    Ocr ocr;

    String language = null;
    File file = null;

    @Given("^user is on (.*) page$")
    public void userAccessesThePage(String page) {
        switch (page) {
            case "Google Translate":
                webDriverInstance.getWebDriver().get("https://translate.google.com/");
                assertThat(page + " page is present", genericActions.checkIfPresent(googleTranslatePage.getGoogleTranslateLogo()), is(TRUE));
                break;
            case "Fake Captcha":
                webDriverInstance.getWebDriver().get("https://fakecaptcha.com/");
                assertThat(page + " page is present", genericActions.checkIfPresent(fakeCaptchaPage.getFakeCaptchaLogo()), is(TRUE));
        }
    }

    @When("^user selects (.*) Language$")
    public void userSelectsEnglishLanguage(String language) {

        this.language = language;
        genericActions.click(googleTranslatePage.getLanguageDropdown());

        switch (language) {
            case "English":
                genericActions.sendKeys(googleTranslatePage.getLanguageSearch(), language);
                genericActions.click(googleTranslatePage.getLanguage());
                genericActions.click(googleTranslatePage.getEnglishKbd());
                break;
            case "Russian":
                genericActions.sendKeys(googleTranslatePage.getLanguageSearch(), language);
                genericActions.click(googleTranslatePage.getLanguage());
                genericActions.click(googleTranslatePage.getInputType());
                genericActions.click(googleTranslatePage.getRussianKbd());
        }
    }

    @And("^user types (.*) via ocr engine$")
    public void userTypesWordViaOcrEngine(String text) throws TesseractException, IOException {
        genericActions.click(googleTranslatePage.getShiftButton());
        ocr.clickOnElementsByText(text, googleTranslatePage.getKbdButtons(googleTranslatePage.getKbd()), language);
        genericActions.checkIfPresent(googleTranslatePage.getTextArea());
        this.file = googleTranslatePage.getTextArea().getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(file, new File("C:\\images\\" + RandomString.make(5) + ".png"));
        assertThat("Word is typed into translate field", genericActions.getText(googleTranslatePage.getTextArea()), is(text));
    }

    @Then("^(.*) is present in text area$")
    public void wordIsPresentInTextArea(String text) throws TesseractException {
        ocr.getTesseract().setOcrEngineMode(3);
        String word = ocr.getTesseract().doOCR(file);
        assertThat("Word is typed into translate field", word, containsString(text));
    }

    @When("^user creates fake captcha for '(.*)'$")
    public void userCreatesFakeCaptcha(String text) {
        genericActions.sendKeys(fakeCaptchaPage.getCaptchaTextField(), text);
        genericActions.click(fakeCaptchaPage.getCreateCaptchaButton());
        genericActions.click(fakeCaptchaPage.getProceedLink());
        genericActions.checkIfPresent(fakeCaptchaPage.getResultDisplay());
        scenarioContext.saveData("text", text);
    }

    @Then("the word from captcha corresponds to typed one")
    public void wordCorrespondsToTypedOne() throws TesseractException, IOException {
        this.file = fakeCaptchaPage.getResultDisplay().getScreenshotAs(OutputType.FILE);
        String text = (String) scenarioContext.getData("text");
        ocr.getTesseract().setLanguage("eng");
        ocr.getTesseract().setOcrEngineMode(3);
        String word = ocr.getTesseract().doOCR(file).trim();
        while (!word.contains(text)) {
            log.error("extracted word is: " + word);
            BufferedImage bufferedImage = ImageIO.read(file);
            ImageIO.write(bufferedImage, "png", new File("C:\\images\\fakeCaptchaRetries\\" + RandomString.make(5) + ".png"));
            genericActions.click(fakeCaptchaPage.getRegenerate());
            genericActions.click(fakeCaptchaPage.getProceedLink());
            genericActions.checkIfPresent(fakeCaptchaPage.getResultDisplay());
            file = fakeCaptchaPage.getResultDisplay().getScreenshotAs(OutputType.FILE);
            word = ocr.getTesseract().doOCR(file);
        }
        assertThat("Captcha word correspons to typed ine", word, containsString(text));
    }
}
