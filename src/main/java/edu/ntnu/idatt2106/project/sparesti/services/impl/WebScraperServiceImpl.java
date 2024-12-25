package edu.ntnu.idatt2106.project.sparesti.services.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.NewsArticleDto;
import edu.ntnu.idatt2106.project.sparesti.services.WebScraperService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/** Service for fetching news articles from a given source. */
@Service
public class WebScraperServiceImpl implements WebScraperService {
  /**
   * Fetches news articles from a given source.
   *
   * @return a list of news articles
   * @throws IOException if an error occurs while fetching the articles
   */
  @Override
  public List<NewsArticleDto> fetchArticles() throws IOException {
    var doc = Jsoup.connect("https://www.dn.no/").get();
    return parseDocument(doc);
  }

  /**
   * Parses a document and returns a list of news articles.
   *
   * @param doc the document to parse
   * @return a list of news articles
   */
  public List<NewsArticleDto> parseDocument(Document doc) {
    ArrayList<NewsArticleDto> list = new ArrayList<NewsArticleDto>();
    Elements articles = doc.select("article");
    for (Element el : articles) {
      // Check if article is free
      String articleLink = el.select("a.dre-item__title").attr("href");

      NewsArticleDto article =
          NewsArticleDto.builder()
              .title(el.select("a.dre-item__title").text())
              .imageUrl(el.select("img").attr("src"))
              .url("https://www.dn.no" + articleLink)
              .source("Dagens Næringsliv")
              .build();
      list.add(article);
    }

    return list;
  }
}
