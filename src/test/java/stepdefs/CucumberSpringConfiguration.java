package stepdefs;


import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan({"utils", "stepdefs", "pageobject"})
public class CucumberSpringConfiguration {

    @Bean
    public Tesseract tesseract() {
        return new Tesseract();
    }
}
