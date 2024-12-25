package edu.ntnu.idatt2106.project.sparesti.domain.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data transfer object for news articles. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsArticleDto {
  private String title;
  private String description;
  private String url;
  private String imageUrl;
  private String source;
  private Date publishedAt;
  private boolean isFree;
}
