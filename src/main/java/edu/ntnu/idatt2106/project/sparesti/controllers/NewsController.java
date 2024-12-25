package edu.ntnu.idatt2106.project.sparesti.controllers;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.NewsArticleDto;
import edu.ntnu.idatt2106.project.sparesti.services.WebScraperService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for fetching news articles. */
@CrossOrigin("http://localhost:5173")
@RequestMapping("api/secure/news")
@RestController
public class NewsController {
  private final WebScraperService webScraperService;

  public NewsController(WebScraperService webScraperService) {
    this.webScraperService = webScraperService;
  }

  /**
   * Fetches news articles from a given source.
   *
   * @return a list of news articles
   */
  @GetMapping
  public ResponseEntity<List<NewsArticleDto>> getNews() {
    try {
      List<NewsArticleDto> news = webScraperService.fetchArticles();
      return ResponseEntity.ok(news);
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }
}
