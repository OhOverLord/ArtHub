package org.arthub.backend.repository;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.PostElasticsearch;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostElasticsearchRepositoryTest {
    @Autowired
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    private static final ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.6");
    @BeforeAll
    public static void startContainer() {
        elasticsearchContainer.start();
    }

    @DynamicPropertySource
    static void setElasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @Test
     void findByTitleOrDescriptionContainingSuccessfulSubstringCaseTest(){
        PostElasticsearch postElasticsearch = TestEntities.ELASTICLONGDUMMY;
        PostElasticsearch postElasticsearch2 = TestEntities.ELASTICSHORTDUMMY;
        postElasticsearchRepository.save(postElasticsearch);
        postElasticsearchRepository.save(postElasticsearch2);

        Page<PostElasticsearch> foundPosts = postElasticsearchRepository.findByTitleOrDescriptionContaining("Life is either a daring adventure or nothing at all.", PageRequest.of(0, 5));
        assertNotNull(foundPosts);
        List<PostElasticsearch> asList = foundPosts.getContent();
        assertEquals(1, asList.size());
        assertEquals("1", asList.get(0).getId());
    }
    @Test
     void findByTitleContainingSuccessfulMatchCaseTest(){
        PostElasticsearch postElasticsearch = TestEntities.ELASTICLONGDUMMY;
        PostElasticsearch postElasticsearch2 = TestEntities.ELASTICSHORTDUMMY;
        postElasticsearchRepository.save(postElasticsearch);
        postElasticsearchRepository.save(postElasticsearch2);

        Page<PostElasticsearch> foundPosts = postElasticsearchRepository.findByTitleOrDescriptionContaining("Life is either routine", PageRequest.of(0, 5));
        List<PostElasticsearch> asList = foundPosts.getContent();
        assertEquals(1, asList.size());
        assertEquals("1", asList.get(0).getId());
    }
    @Test
     void findByTitleOrDescriptionContainingUnsuccessfulLongCaseTest(){
        PostElasticsearch postElasticsearch = TestEntities.ELASTICLONGDUMMY;
        PostElasticsearch postElasticsearch2 = TestEntities.ELASTICSHORTDUMMY;
        postElasticsearchRepository.save(postElasticsearch);
        postElasticsearchRepository.save(postElasticsearch2);

        Page<PostElasticsearch> foundPosts = postElasticsearchRepository.findByTitleOrDescriptionContaining("Life incredible experience morning routine", PageRequest.of(0, 5));
        List<PostElasticsearch> asList = foundPosts.getContent();
        assertEquals(0, asList.size());
    }
    @Test
     void findByTitleOrDescriptionContainingUnsuccessfulCaseTest(){
        PostElasticsearch postElasticsearch = TestEntities.ELASTICLONGDUMMY;
        PostElasticsearch postElasticsearch2 = TestEntities.ELASTICSHORTDUMMY;
        postElasticsearchRepository.save(postElasticsearch);
        postElasticsearchRepository.save(postElasticsearch2);

        Page<PostElasticsearch> foundPosts = postElasticsearchRepository.findByTitleOrDescriptionContaining("Death", PageRequest.of(0, 5));
        List<PostElasticsearch> asList = foundPosts.getContent();
        assertEquals(0, asList.size());
    }
    @Test
     void findByPostIdSuccessfulTest(){
        PostElasticsearch postElasticsearch = TestEntities.ELASTICLONGDUMMY;
        postElasticsearchRepository.save(postElasticsearch);

        Optional<PostElasticsearch> found = postElasticsearchRepository.findByPostId(1L);
        assertTrue(found.isPresent());
        assertEquals("1", found.get().getId());
    }
    @Test
     void findByPostIdFailedTest(){
        PostElasticsearch postElasticsearch = TestEntities.ELASTICLONGDUMMY;
        postElasticsearchRepository.save(postElasticsearch);

        Optional<PostElasticsearch> found = postElasticsearchRepository.findByPostId(123L);
        assertTrue(found.isEmpty());
    }
}
