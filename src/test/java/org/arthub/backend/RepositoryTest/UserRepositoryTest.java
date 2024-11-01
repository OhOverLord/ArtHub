package org.arthub.backend.RepositoryTest;

import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Repository.UserRepository;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testFindByUsername() {
        User user = TestEntities.getDefaultUser1();
        entityManager.persist(user);
        entityManager.flush();

        User foundExisting = userRepository.findByUsername("john_doe").orElse(null);
        assertNotNull(foundExisting);
        assertEquals("john_doe", foundExisting.getUsername());
        assertEquals("john.doe@example.com", foundExisting.getEmail());

        User notFound = userRepository.findByUsername("doradura").orElse(null);
        assertNull(notFound);
    }
    @Test
    public void testFindByEmail() {
        User user = TestEntities.getDefaultUser1();
        entityManager.persist(user);
        entityManager.flush();

        User foundExisting = userRepository.findByEmail("john.doe@example.com").orElse(null);
        assertNotNull(foundExisting);
        assertEquals("john_doe", foundExisting.getUsername());
        assertEquals("john.doe@example.com", foundExisting.getEmail());

        User notFound = userRepository.findByEmail("unknown@example.com").orElse(null);
        assertNull(notFound);
    }
}
