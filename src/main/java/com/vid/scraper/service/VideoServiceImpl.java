package com.vid.scraper.service;

import com.sun.istack.NotNull;
import com.vid.scraper.exception.UserNotFoundException;
import com.vid.scraper.exception.VideoNotFoundException;
import com.vid.scraper.model.PaginationDto;
import com.vid.scraper.model.Response;
import com.vid.scraper.model.VideoDto;
import com.vid.scraper.model.VideoSummaryDto;
import com.vid.scraper.model.entity.*;
import com.vid.scraper.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@EnableAsync
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final TagRepository tagRepository;
    private final ViewRepository viewRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final RecommendationService recommendationService;
    private final RecommendationViewRepository recommendationViewRepository;
    private final List<String> urls = new ArrayList<>(Arrays.asList("https://wow.yaeby.info/pop-video/", "https://wow.yaeby.info/top-rated/", "https://wwv.sslkn.porn/"));


    @Scheduled(fixedRate = 84600000)
    @Async
    public void scrapeVideos() {
        for (String url : this.urls) {
            if (url.contains("yaeby")) {
                extractVideoFromYaeby(url);
            }
            if (url.contains("sslkn")) {
                extractVideoFromSSLKN(url);
            }
        }
    }

    @Scheduled(fixedRate = 84600000)
    @Async
    public void verifyAllVideos() {
        List<Video> videos = videoRepository.findAll();
        for (Video video : videos) {
            if (validateURL(video.getSdUrl()) || validateURL(video.getImageUrl()) || validateURL(video.getHdUrl()) || validateURL(video.getFullHdUrl())) {
                videoRepository.delete(video);
            }
        }
    }

    @Override
    @Transactional
    public Response getVideos(Integer page) {
        Pageable pageable = PageRequest.of(page, 30);
        Page<Video> rawVideos = videoRepository.findAll(pageable);
        List<VideoSummaryDto> videoSummaryDtos = new ArrayList<>();
        rawVideos.forEach(video -> videoSummaryDtos.add(VideoSummaryDto.from(video)));
        PaginationDto pagination = new PaginationDto(rawVideos.hasNext(), rawVideos.getNumber());
        return new Response(true, videoSummaryDtos, pagination);
    }

    @Override
    @Transactional
    public Response getVideoById(Long videoId, String rawToken) {
        Optional<Video> video = videoRepository.findById(videoId);
        if (!video.isPresent()) {
            throw new VideoNotFoundException(String.format("Video with ID [%s] not found!", videoId));
        }
        Video updatedVideo = video.get();
        View view = viewRepository.save(View.builder().createdAt(OffsetDateTime.now()).video(video.get()).build());
        List<View> views = new ArrayList<>(video.get().getViews());
        views.add(view);
        updatedVideo.setViews(views);
        Video savedVideo = videoRepository.save(updatedVideo);
        if (rawToken != null) {
            User user = getUserFromToken(rawToken);
            return new Response(true, VideoDto.fromWithIsLiked(savedVideo, user), null);
        }
        return new Response(true, VideoDto.fromWithTags(savedVideo), null);
    }

    @Override
    @Transactional
    public Response getPopularVideos(Integer page, Integer perPage, Integer days) {
        int from = page * perPage;
        List<Video> videos = videoRepository.findAllByOrderByViewsDesc(from, perPage, days);
        List<VideoDto> videoDtos = new ArrayList<>();
        videos.forEach(video -> videoDtos.add(VideoDto.from(video)));
        PaginationDto pagination = new PaginationDto(true, page + 1);
        if (videos.size() < perPage) {
            pagination.setHasNextPage(false);
            pagination.setNextPage(page);
        }
        return new Response(true, videoDtos, pagination);
    }

    @Override
    @Transactional
    public Response getRecommendations(String rawToken, Integer page, String videoIds) {
        if (videoIds == null && rawToken == null) {
            List<Video> videos = videoRepository.findRandomVideos();
            List<VideoSummaryDto> videoSummaryDtos = new ArrayList<>();
            videos.forEach(video -> videoSummaryDtos.add(VideoSummaryDto.from(video)));
            return new Response(true, videoSummaryDtos, new PaginationDto(true, page + 1));
        } else if (videoIds == null) {
            int offset = page * 20;
            User user = getUserFromToken(rawToken);
            List<Like> likes = likeRepository.findAll();
            List<RecommendationView> recommendationViews = recommendationViewRepository.findAll();
            List<VideoSummaryDto> videoSummaryDtos = new ArrayList<>();
            List<RecommendedItem> recommendedItems = recommendationService.findRecommendations(user, likes, recommendationViews, offset + 20);
            if (recommendedItems == null || recommendedItems.isEmpty()) {
                List<Video> videos = videoRepository.findRandomVideos();
                videos.forEach(video -> videoSummaryDtos.add(VideoSummaryDto.fromWithIsLiked(video, user)));
            } else {
                for (int i = 0; i < offset; i++) {
                    RecommendedItem toRemove = recommendedItems.get(0);
                    recommendedItems.remove(toRemove);
                }
                recommendedItems.forEach(recommendedItem -> {
                    Optional<Video> video = videoRepository.findById(recommendedItem.getItemID());
                    if (!video.isPresent()) {
                        throw new VideoNotFoundException(String.format("Video with ID [%s] not found!", recommendedItem.getItemID()));
                    }
                    videoSummaryDtos.add(VideoSummaryDto.from(video.get()));
                });
            }
            PaginationDto paginationDto = new PaginationDto(true, page + 1);
            if (videoSummaryDtos.size() < 20) {
                paginationDto.setNextPage(page);
                paginationDto.setHasNextPage(false);
            }
            return new Response(true, videoSummaryDtos, paginationDto);
        } else if (rawToken == null) {
            String[] stringIds = videoIds.split(",");
            List<VideoSummaryDto> videoSummaryDtos = new ArrayList<>();
            for (String stringId : stringIds) {
                List<Video> videos = videoRepository.findSimilar(Long.parseLong(stringId), page * 20, 4);
                videos.forEach(video -> videoSummaryDtos.add(VideoSummaryDto.from(video)));
            }
            PaginationDto paginationDto = new PaginationDto(true, page + 1);
            if (videoSummaryDtos.size() < 20) {
                paginationDto.setHasNextPage(false);
                paginationDto.setNextPage(page);
            }
            return new Response(true, videoSummaryDtos, paginationDto);
        }
        return null;
    }

    @Override
    @Transactional
    public Response getSimilar(Long videoId, Integer page) {
        int offset = page * 20;
        List<Video> videos = videoRepository.findSimilar(videoId, offset, 20);
        Optional<Video> duplicateVideo = videoRepository.findById(videoId);
        if (!duplicateVideo.isPresent()) {
            throw new VideoNotFoundException(String.format("Video with ID [%s] not found", videoId));
        }
        videos.remove(duplicateVideo.get());
        List<VideoSummaryDto> videoSummaryDtos = new ArrayList<>();
        videos.forEach(video -> videoSummaryDtos.add(VideoSummaryDto.from(video)));
        PaginationDto paginationDto = new PaginationDto(true, page + 1);
        if (videos.size() < 20) {
            paginationDto.setHasNextPage(false);
            paginationDto.setNextPage(page);
        }
        return new Response(true, videoSummaryDtos, paginationDto);
    }

    private void extractVideoFromYaeby(String url) {
        try {
            System.out.println("Started extracting from Yaeby.");
            ChromeDriverService chromeDriverService = new ChromeDriverService.Builder().build();
            ChromeOptions chromeDriverOptions = new ChromeOptions().addArguments("--no-sandbox").addArguments("--headless").addArguments("--disable-dev-shm-usage").addArguments("--disable-extensions").addArguments("disable-infobars").addArguments("--mute-audio").setLogLevel(ChromeDriverLogLevel.OFF);
            ChromeDriver driver = new ChromeDriver(chromeDriverService, chromeDriverOptions);
            WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofMillis(4000L));
            Actions actions = new Actions(driver);

            driver.get(url);
            driver.findElement(By.id("login")).click();
            driverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("login_username")));
            driver.findElement(By.id("login_username")).sendKeys("gro180");
            driver.findElement(By.id("login_pass")).sendKeys("12341234");
            driver.findElement(By.className("submit")).click();
            for (int i = 1; i <= 10; i++) {
                Document document = Jsoup.connect(url + i + "/").get();
                Elements elements = document.getElementById("list_videos_common_videos_list_items").getElementsByClass("item   ");
                for (Element e : elements) {
                    Elements aElement = e.getElementsByTag("a");
                    String videoUrl = aElement.first().attr("href");


                    //GET Image
                    driver.get(videoUrl);
                    String imageUrl = driver.findElement(By.className("fp-poster")).findElement(By.tagName("img")).getAttribute("src");


                    // GET 360p Video
                    driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"kt_player\"]/div[2]/div[1]")));
                    driver.findElement(By.xpath("//*[@id=\"kt_player\"]/div[2]/div[1]")).click();
                    Document videoPage = Jsoup.parse(driver.getPageSource());
                    String videoFilePath = videoPage.getElementsByTag("video").attr("src");
                    driver.get(videoFilePath);
                    String sdVideoSrc = driver.getCurrentUrl();

                    // GET 720p Video
                    driver.get(videoUrl);
                    driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"kt_player\"]/div[2]/div[1]")));
                    driver.findElement(By.xpath("//*[@id=\"kt_player\"]/div[2]/div[1]")).click();
                    driverWait.until(ExpectedConditions.elementToBeClickable(By.className("fp-settings")));
                    actions.moveToElement(driver.findElement(By.className("fp-settings"))).click().perform();
                    if (driver.findElements(By.xpath("//*[text()='720p']")).size() > 0) {
                        try {
                            actions.moveToElement(driver.findElement(By.xpath("//*[text()='720p']"))).click().perform();
                        } catch (ElementNotInteractableException ie) {
                            System.out.println("Could not find button on: " + videoUrl);
                        }
                    }
                    videoPage = Jsoup.parse(driver.getPageSource());
                    videoFilePath = videoPage.getElementsByTag("video").attr("src");
                    driver.get(videoFilePath);
                    String hdVideoSrc = driver.getCurrentUrl();

                    //GET 1080p Video
                    driver.get(videoUrl);
                    driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"kt_player\"]/div[2]/div[1]")));
                    driver.findElement(By.xpath("//*[@id=\"kt_player\"]/div[2]/div[1]")).click();
                    driverWait.until(ExpectedConditions.elementToBeClickable(By.className("fp-settings")));
                    actions.moveToElement(driver.findElement(By.className("fp-settings"))).click().perform();
                    if (driver.findElements(By.xpath("//*[text()='1080p']")).size() > 0) {
                        try {
                            actions.moveToElement(driver.findElement(By.xpath("//*[text()='1080p']"))).click().perform();
                        } catch (ElementNotInteractableException ie) {
                            System.out.println("Could not find button on: " + videoUrl);
                        }
                    }
                    videoPage = Jsoup.parse(driver.getPageSource());
                    videoFilePath = videoPage.getElementsByTag("video").attr("src");
                    driver.get(videoFilePath);
                    String fullHdVideoSrc = driver.getCurrentUrl();


                    // GET description
                    String description = "";
                    try {
                        Element descriptionElement = videoPage.getElementById("tab_video_info");
                        description = descriptionElement.getElementsByTag("em").get(3).text();
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        System.out.println(indexOutOfBoundsException.getMessage());
                    }

                    // GET tags
                    Elements tagElements = videoPage.getElementsByClass("info-content").first().select("a:not([class])");
                    List<Tag> tags = new ArrayList<>();
                    for (Element tagElement : tagElements) {
                        Tag tag = Tag.builder().tagName(tagElement.text()).build();
                        if (!tagRepository.existsByTagName(tag.getTagName())) {
                            tags.add(tagRepository.save(tag));
                        } else {
                            tags.add(tagRepository.getByTagName(tag.getTagName()));
                        }
                    }

                    String videoTitle = aElement.first().attr("title");
                    if (!videoRepository.existsByTitle(videoTitle)) {
                        System.out.println("Added video with title: " + videoTitle);
                        videoRepository.save(Video.builder().sdUrl(sdVideoSrc).hdUrl(hdVideoSrc).fullHdUrl(fullHdVideoSrc).imageUrl(imageUrl).description(description).title(videoTitle).tags(tags).views(new ArrayList<>()).createdAt(OffsetDateTime.now()).build());
                    }
                }
            }
            driver.quit();
        } catch (IOException | TimeoutException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Finished extracting from Yaeby.");
        }
    }

    private void extractVideoFromSSLKN(String url) {
        try {
            System.out.println("Started extracting from SSLKN.");
            for (int i = 1; i <= 10; i++) {
                ChromeDriverService chromeDriverService = new ChromeDriverService.Builder().build();
                ChromeOptions chromeDriverOptions = new ChromeOptions().addArguments("--no-sandbox").addArguments("--headless").addArguments("--disable-dev-shm-usage").addArguments("--disable-extensions").addArguments("disable-infobars").addArguments("--mute-audio").setLogLevel(ChromeDriverLogLevel.OFF);
                ChromeDriver driver = new ChromeDriver(chromeDriverService, chromeDriverOptions);
                driver.get(url + i + "/");
                Document document = Jsoup.parse(driver.getPageSource());
                Elements elements = document.getElementById("list_videos_most_recent_videos").getElementsByClass("video_list").first().getElementById("list_videos_most_recent_videos_items").getElementsByClass("item");
                elements.removeIf(element -> element.getElementsByClass("premium-icons").hasText());
                for (Element e : elements) {
                    String videoUrl = e.getElementsByTag("a").first().attr("href");

                    //GET Image
                    driver.get(videoUrl);
                    String imageUrl = driver.findElement(By.className("fp-poster")).findElement(By.tagName("img")).getAttribute("src");

                    //GET Title
                    String videoTitle = driver.findElement(By.className("title-video")).findElement(By.tagName("h1")).getText();


                    // GET 360p Video
                    Document videoPage = Jsoup.parse(driver.getPageSource());
                    String videoFilePath = videoPage.getElementsByTag("video").attr("src");
                    driver.get(videoFilePath);
                    String sdVideoSrc = driver.getCurrentUrl();

                    // GET 720p Video
                    String hdVideoSrc = sdVideoSrc.replace("360", "720");

                    //GET 1080p Video
                    String fullHdVideoSrc = sdVideoSrc.replace("_360p", "");


                    // GET description
                    String description = "";
                    try {
                        description = videoPage.getElementsByClass("description").first().text();
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        System.out.println(indexOutOfBoundsException.getMessage());
                    }

                    // GET tags
                    Elements tagElements = videoPage.getElementsByClass("categor-holder").get(1).getElementsByClass("row").first().getElementsByClass("row-item");
                    List<Tag> tags = new ArrayList<>();
                    for (Element tagElement : tagElements) {
                        Tag tag = Tag.builder().tagName(tagElement.text()).build();
                        if (!tagRepository.existsByTagName(tag.getTagName())) {
                            tags.add(tagRepository.save(tag));
                        } else {
                            tags.add(tagRepository.getByTagName(tag.getTagName()));
                        }
                    }

                    if (!videoRepository.existsByTitle(videoTitle)) {
                        System.out.println("Added video with title: " + videoTitle);
                        videoRepository.save(Video.builder().sdUrl(sdVideoSrc).hdUrl(hdVideoSrc).fullHdUrl(fullHdVideoSrc).imageUrl(imageUrl).description(description).title(videoTitle).tags(tags).views(new ArrayList<>()).createdAt(OffsetDateTime.now()).build());
                    }
                }
                driver.quit();
            }
        } catch (TimeoutException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Finished extracting from SSLKN.");
        }
    }

    private boolean validateURL(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            return !(connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void verifyVideos(Pageable pageable) {
        Page<Video> videos = videoRepository.findAll(pageable);
        videos.getContent().forEach(video -> {
            if (validateURL(video.getSdUrl()) || validateURL(video.getHdUrl()) || validateURL(video.getImageUrl())) {
                videoRepository.delete(video);
            }
        });
    }

    private User getUserFromToken(String rawToken) {
        Map<String, String> userData = tokenService.getUserDataFromToken(rawToken);
        Optional<User> user = userRepository.findByEmail(userData.get("email"));
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("User with token [%s] not found", rawToken));
        }
        return user.get();
    }
}
