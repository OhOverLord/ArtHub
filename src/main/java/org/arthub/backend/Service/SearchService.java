package org.arthub.backend.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Repository.PostRepository;
import org.springframework.stereotype.Service;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing search operations.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    /**
     * Repository for managing Post entities in the database.
     */
    private final PostRepository postRepository;

    /**
     * Processes a search prompt and extracts keywords using an external Python service.
     *
     * @param prompt the search prompt to process
     * @return a list of keywords extracted from the prompt
     */
    public static List<String> processPrompt(final String prompt) {
        String url = System.getProperty("python.url") + "/api/search/";
        List<String> result = new ArrayList<>();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            Gson gson = new Gson();
            Map<String, String> map = new HashMap<>();
            map.put("data", prompt);
            String jsonPayload = gson.toJson(map);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String jsonResponse = EntityUtils.toString(responseEntity);
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    log.info("Prompt: " + prompt);
                    jsonObject.get("tokens").getAsJsonArray().forEach((JsonElement token) -> {
                        result.add(token.getAsString());
                    });
                    log.info("Tokens: " + result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieves search results based on the provided prompt.
     *
     * @param prompt the search prompt
     * @return a list of posts matching the search criteria
     * @throws Exception if an error occurs during the search process
     */
    public List<Post> getSearchResults(final String prompt) throws Exception {
        List<String> keywords;
        keywords = processPrompt(prompt.toLowerCase());

        return new ArrayList<>(postRepository.searchByKeywords(keywords));
    }
}
