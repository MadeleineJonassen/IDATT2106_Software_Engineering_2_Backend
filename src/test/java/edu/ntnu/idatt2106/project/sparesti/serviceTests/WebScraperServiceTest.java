package edu.ntnu.idatt2106.project.sparesti.serviceTests;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.NewsArticleDto;
import edu.ntnu.idatt2106.project.sparesti.services.impl.WebScraperServiceImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/** Unit tests for {@link WebScraperServiceImpl}. */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class WebScraperServiceTest {
  @InjectMocks private WebScraperServiceImpl webScraperService;

  /** Test that the web scraper can fetch articles from a given source */
  @Test
  void canFetchArticles() {
    // We should mock the response from dn.no to make the tests deterministic
    Document doc =
        Jsoup.parse(
            "<html><body><article><a class=\"dre-item__title\" href=\"/article1\">Article 1</a><img src=\"image1\"/></article><article><a class=\"dre-item__title\" href=\"/article2\">Article 2</a><img src=\"image2\"/></article></body></html>");
    List<NewsArticleDto> articles = webScraperService.parseDocument(doc);
    assert articles.get(0).getTitle().equals("Article 1");
    assert articles.get(1).getTitle().equals("Article 2");
    assert articles.size() == 2;
  }
}
