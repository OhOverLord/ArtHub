package org.arthub.backend.repository;

import jakarta.persistence.EntityManager;
import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.User;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@Rollback
@DataJpaTest
class FolderRepositoryTest {
    @Autowired
    private  FolderRepository folderRepository;
    @Autowired
    private EntityManager entityManager;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testFindByPatronAndTitleExisting() {
        User user1 = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user1, null);
        entityManager.persist(user1);
        entityManager.persist(folder);
        entityManager.flush();

        Folder foundFolder = (Folder)folderRepository.findByPatronAndTitle(user1, "shrek_photos").orElse(null);
        assertNotNull(foundFolder);
        assert(foundFolder.getPatron().getUsername().equals(TestEntities.getDefaultUser1().getUsername()));
        assert(foundFolder.getPatron().getEmail().equals(TestEntities.getDefaultUser1().getEmail()));
        assert(foundFolder.getTitle().equals("shrek_photos"));
    }

    @Test
    void testFindByPatronAndTitleNotExisting() {
        User user1 = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user1, null);
        entityManager.persist(user1);
        entityManager.persist(folder);
        entityManager.flush();

        Folder foundFolder = (Folder)folderRepository.findByPatronAndTitle(user1, "kek_photos").orElse(null);
        assertNull(foundFolder);
    }

    @Test
    void testFindAllByPatronExisting() {
        User user1 = TestEntities.getDefaultUser1();
        User user2 = TestEntities.getDefaultUser2();
        Folder folder = new Folder(null, "shrek_photos", "", user1, null);
        Folder folder1 = new Folder(null, "shrek_photos", "", user2, null);
        Folder folder2 = new Folder(null, "kek_photos", "", user1, null);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(folder);
        entityManager.persist(folder1);
        entityManager.persist(folder2);
        entityManager.flush();

        List<Folder> folders = folderRepository.findAllByPatron(user1);
        assertNotNull(folders);
        assert(folders.equals(List.of(folder, folder2)));
    }

    @Test
    void testFindAllByPatronNonExisting() {
        User user1 = TestEntities.getDefaultUser1();
        User user2 = TestEntities.getDefaultUser2();
        Folder folder = new Folder(null, "shrek_photos", "", user1, null);
        Folder folder2 = new Folder(null, "kek_photos", "", user1, null);
        entityManager.persist(user2);
        entityManager.persist(user1);
        entityManager.persist(folder);
        entityManager.persist(folder2);
        entityManager.flush();

        List<Folder> folders = folderRepository.findAllByPatron(user2);
        assertTrue(folders.isEmpty());
    }
}
