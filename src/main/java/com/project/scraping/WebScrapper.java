package com.project.scraping;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WebScrapper {

    private static final String BASE_URL = "https://books.toscrape.com/";

    private WebScrapper() {
    }

    private static final Set<String> processedUrls = new HashSet<>();
    private static final Set<String> processedFileUrls = new HashSet<>();
    private static final Set<String> processedHtmlContentUrls = new HashSet<>();

    /*
     * Asynchronous method to perform web scraping on a given URL.
     * It will connect to website, traverse links and download resources
     */
    public static CompletableFuture<Void> scrape(String url, Path destinationFolder) {
        return CompletableFuture.runAsync(() -> {
            try {
                Document document = Jsoup.connect(url).get();
                traverseLinks(document, destinationFolder);
                downloadResources(document, destinationFolder);
            } catch (IOException e) {
                log.error("Error while scraping:", e);
            }
        });
    }

    /*
     * Traverse and process all links on the current page.
     */
    private static void traverseLinks(Document document, Path destinationFolder) {
        Elements links = document.select("a[href]");
        CompletableFuture.runAsync(() ->
            links.forEach(link -> {
                String linkUrl = link.absUrl("href");
                if (!processedUrls.contains(linkUrl) && linkUrl.startsWith(BASE_URL)) {
                    log.info("Processing link: {}", linkUrl);
                    scrape(linkUrl, destinationFolder.resolve(getRelativePath(document.baseUri())));
                    processedUrls.add(linkUrl);
                }
            }));
    }

    /*
     * Download resources (HTML, CSS, Images, Icons) for the current page.
     */
    private static void downloadResources(Document document, Path destinationFolder) {
        CompletableFuture.runAsync(() -> saveHtmlContent(document, destinationFolder.resolve(getRelativePath(document.baseUri()))));
        downloadElements(document.select("img[src]"), "src", destinationFolder);
        downloadElements(document.select("link[rel=stylesheet]"), "href", destinationFolder);
    }

    /*
     * Download resources (HTML, CSS, Images, Icons) for the current page.
     */
    private static void downloadElements(Elements elements, String absUrl, Path destinationFolder) {
        elements.forEach(element -> {
            String elementUrl = element.absUrl(absUrl);
            if (elementUrl.startsWith(BASE_URL)) {
                CompletableFuture.runAsync(() -> {
                    try {
                        downloadFile(new URL(elementUrl), destinationFolder.resolve(getRelativePath(elementUrl)));
                    } catch (MalformedURLException e) {
                        log.error("Invalid URL: {}", elementUrl, e);
                    }
                });
            }
        });
    }

    /*
     * Download a file (HTML, CSS, Images, Icons) from a given URL.
     */
    private static void downloadFile(URL url, Path destinationPath) {
        try {
            if (processedFileUrls.contains(url.toString())) {
                return;
            }
            Files.createDirectories(destinationPath.getParent());
            log.info("Downloading: {}", url);
            try (InputStream inputStream = url.openStream()) {
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Downloaded: {}", destinationPath);
            processedFileUrls.add(url.toString());
        } catch (IOException e) {
            log.error("Error while downloading {}: {}", url, e.getMessage());
        }
    }

    /*
     * Save the HTML content of the current page to a file.
     */
    private static void saveHtmlContent(Document document, Path filePath) {
        try {
            if (processedHtmlContentUrls.contains(filePath.toString())) {
                return;
            }
            log.info("Saving HTML content: {}", filePath);
            Files.createDirectories(filePath.getParent());
            String htmlContent = document.outerHtml();
            Files.writeString(filePath, htmlContent);
            processedHtmlContentUrls.add(filePath.toString());
            log.info("Saved HTML content: {}", filePath);
        } catch (IOException e) {
            log.error("Error while saving HTML content: {}", e.getMessage());
        }
    }

    /*
     * Extract the relative path from the absolute URL.
     */
    private static String getRelativePath(String absoluteUrl) {
        try {
            URL absolute = new URL(absoluteUrl);
            return Paths.get(absolute.getPath()).toString();
        } catch (MalformedURLException e) {
            log.error("Invalid URL: {}", absoluteUrl, e);
            return "";
        }
    }
}
