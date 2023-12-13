package com.project.scraping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@Slf4j
public class WebScrapingApplication {

    private static final String BASE_URL = "https://books.toscrape.com/";
    private static final String DESTINATION_FOLDER = "D:\\";

    public static void main(String[] args) {
        SpringApplication.run(WebScrapingApplication.class, args);

        CompletableFuture<Void> future = WebScrapper.scrape(BASE_URL, Path.of(DESTINATION_FOLDER));
        log.info("Web scraping started...");

        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.info("Web scraping failed: ");
            } else {
                log.info("Web scraping completed successfully!");
            }
        });
        future.join();

        log.info("Program completed.");
    }
}
