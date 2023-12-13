package com.project.scraping;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WebScrapper {

    /*
     * Asynchronous method to perform web scraping on a given URL.
     * It will connect to website, traverse links and download resources
     */
    public static CompletableFuture<Void> scrape(String url, Path destinationFolder) {
        return null;
    }
}
