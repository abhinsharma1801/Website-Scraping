**Description:**
This is a demo Java web scraping application designed to extract information from a website. The application is built using the Jsoup library for HTML parsing and CompletableFuture for asynchronous execution of tasks.

**Features:**

Asynchronously scrape web pages, traverse links, and download resources.
Download HTML content, images, stylesheets, and other elements.
Show progress on console.

**How to Use:**

**Installation:**
Clone the repository: git clone https://github.com/abhinsharma1801/Website-Scraping.git
Open the project in your preferred Java IDE (IntelliJ, Eclipse etc).

**Run the Application:**
Build maven project.
Execute the main method in the WebScrapingApplication class.
It will download 3 folder(static, media & catalogue) and 1 index.html(homepage file).
Files and folders will be downloaded to the D drive by default. If you encounter access denied issue or 
don't have a D drive, please modify the drive name in the code to an available drive and run the application.
Run the application by double-clicking on index.html. It will open the website in local.
Browse the website from the local.

**Logs:**
Logs are generated using SLF4J and can be viewed in console.

**Notes:**
Ensure you have Java (8 or higher) installed on your machine.
