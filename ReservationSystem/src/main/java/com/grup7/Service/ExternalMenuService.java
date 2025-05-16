package com.grup7.Service;

import com.grup7.Entity.Category;
import com.grup7.Entity.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalMenuService {
  private final RestTemplate restTemplate = new RestTemplate();
  @Value("https://www.themealdb.com/api/json/v1/1/categories.php")
  private String apiUrl;
  public List<Category> getCategories() {
      CategoryResponse categoryResponse = restTemplate.getForObject(apiUrl, CategoryResponse.class);
      return categoryResponse != null ? categoryResponse.getCategories() : new ArrayList<>();
  }
}
