package stepdefs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import utils.WebDriverInstance;

@ContextConfiguration(classes = CucumberSpringConfiguration.class)
public class Hooks {
    @Autowired
    private WebDriverInstance webDriverInstance;


    @Before
    public void before() {
       WebDriver driver =  webDriverInstance.getWebDriver();
       driver.manage().window().maximize();
    }

    @After
    public void after() {
        webDriverInstance.getWebDriver().quit();
        webDriverInstance.setWebDriver(null);
    }
}
