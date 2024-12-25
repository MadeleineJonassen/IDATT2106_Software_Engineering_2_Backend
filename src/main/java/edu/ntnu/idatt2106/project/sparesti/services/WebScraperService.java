package edu.ntnu.idatt2106.project.sparesti.services;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.NewsArticleDto;
import java.io.IOException;
import java.util.List;

/** Service for fetching news articles from a given source. */
public interface WebScraperService {
  public List<NewsArticleDto> fetchArticles() throws IOException;
}
