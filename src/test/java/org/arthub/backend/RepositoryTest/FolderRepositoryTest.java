package org.arthub.backend.RepositoryTest;

import jakarta.persistence.EntityManager;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Repository.FolderRepository;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
@Rollback
@DataJpaTest
public class FolderRepositoryTest {
    @Autowired
    private  FolderRepository folderRepository;
    @Autowired
    private EntityManager entityManager;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testFindByPatronAndTitleExisting() {
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
    public void testFindByPatronAndTitleNotExisting() {
        User user1 = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user1, null);
        entityManager.persist(user1);
        entityManager.persist(folder);
        entityManager.flush();

        Folder foundFolder = (Folder)folderRepository.findByPatronAndTitle(user1, "kek_photos").orElse(null);
        assertNull(foundFolder);
    }

    @Test
    public void testFindAllByPatronExisting() {
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
    public void testFindAllByPatronNonExisting() {
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
        assert(folders.isEmpty());
    }
}
