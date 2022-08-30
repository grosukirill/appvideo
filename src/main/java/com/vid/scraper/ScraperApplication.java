package com.vid.scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
public class ScraperApplication {

    public static void main(String[] args) {
        File driverFile = new File("src/main/resources/chromedriver");
        System.setProperty("webdriver.chrome.driver", driverFile.getAbsolutePath());
        SpringApplication.run(ScraperApplication.class, args);
    }

}
