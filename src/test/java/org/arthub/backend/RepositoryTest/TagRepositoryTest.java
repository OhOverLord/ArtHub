package org.arthub.backend.RepositoryTest;

import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Repository.TagRepository;
import org.arthub.backend.Repository.UserRepository;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
public class TagRepositoryTest {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TestEntityManager entityManager;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testFindByName() {
        Tag tag = TestEntities.getDefaultTag1();
        entityManager.persist(tag);
        entityManager.flush();

        Tag findExisting = (Tag)tagRepository.findByName("tag1").orElse(null);
        assertNotNull(findExisting);
        assert(findExisting.getName().equals(tag.getName()));

        Tag notFound = (Tag)tagRepository.findByName("Not Found").orElse(null);
        assertNull(notFound);

    }
}
