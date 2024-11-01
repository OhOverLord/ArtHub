package org.arthub.backend.RepositoryTest;

import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Repository.PostRepository;
import org.arthub.backend.Repository.UserRepository;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TestEntityManager entityManager;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testFindAll() {
        Post post = new Post(null, "empty", "empty", null,  null, null, null);
        Post post1 = new Post(null, "full", "full", null,  null, null, null);
        entityManager.persist(post);
        entityManager.persist(post1);
        entityManager.flush();


        Page<Post> posts = postRepository.findAll(PageRequest.of(0, 5));
        assertNotNull(posts);
        List<Post> postList = posts.getContent();
        assert(postList.equals(List.of(post, post1)));
    }

    @Test
    public void testFindByTagsInPositive() {
        Tag tag1 = TestEntities.getDefaultTag1();
        Tag tag2 = TestEntities.getDefaultTag2();
        Post post = new Post(null, "empty", "empty", List.of(tag1, tag2),  null, null, null);
        Post post1 = new Post(null, "full", "full", List.of(tag1),  null, null, null);
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(post);
        entityManager.persist(post1);
        entityManager.flush();

        Page<Post> posts = postRepository.findByTagsIn(List.of(tag1),PageRequest.of(0, 5));
        assertNotNull(posts);
        List<Post> postList = posts.getContent();
        assert(postList.equals(List.of(post, post1)));
    }

    @Test
    public void testFindByTagsInEmpty() {
        Tag tag1 = TestEntities.getDefaultTag1();
        Tag tag2 = TestEntities.getDefaultTag2();
        Post post = new Post(null, "empty", "empty", List.of(tag1, tag2),  null, null, null);
        Post post1 = new Post(null, "full", "full", List.of(tag1),  null, null, null);
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(post);
        entityManager.persist(post1);
        entityManager.flush();

        Page<Post> posts = postRepository.findByTagsIn(List.of(),PageRequest.of(0, 5));
        assert(posts.getTotalElements() == 0);
//        List<Post> postList = posts.getContent();
//        assert(postList.equals(List.of(post, post1)));
    }

    @Test
    public void testFindByTagsInNegative() {
        Tag tag1 = TestEntities.getDefaultTag1();
        Tag tag2 = TestEntities.getDefaultTag2();
        Tag tag3 = TestEntities.getDefaultTag3();
        Post post = new Post(null, "empty", "empty", List.of(tag1, tag2),  null, null, null);
        Post post1 = new Post(null, "full", "full", List.of(tag1),  null, null, null);
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(tag3);
        entityManager.persist(post);
        entityManager.persist(post1);
        entityManager.flush();

        Page<Post> posts = postRepository.findByTagsIn(List.of(tag3),PageRequest.of(0, 5));
        assert(posts.getTotalElements() == 0);
//        List<Post> postList = posts.getContent();
//        assert(postList.equals(List.of(post, post1)));
    }

    @Test
    public void testSearchByKeywords() {
        Tag tag1 = TestEntities.getDefaultTag1();
        Tag tag2 = TestEntities.getDefaultTag2();
        Tag tag3 = TestEntities.getDefaultTag3();
        Post post = new Post(null, "empty", "empty", List.of(tag1, tag2),  null, null, null);
        Post post1 = new Post(null, "full", "full", List.of(tag1),  null, null, null);
        Post post2 = new Post(null, "somethingInBetween", "somethingInBetween", List.of(tag3),  null, null, null);
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(tag3);
        entityManager.persist(post);
        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.flush();

        List<Post> postList = postRepository.searchByKeywords(List.of("tag1"));
        assertNotNull(postList);
         assert(postList.equals(List.of(post, post1)));
    }




}
