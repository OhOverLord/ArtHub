package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.repository.FolderRepository;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class FolderServiceTest {
    @Autowired
    private FolderService folderService;
    @MockBean
    private FolderRepository folderRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testReadAll(){
        List<Folder> folders = new ArrayList<>(List.of(new Folder(1L, "shrek_photos", "", null, null)));
        when(folderRepository.findAll()).thenReturn(folders);

        List<Folder> folders1 = folderService.readAll();
        assertEquals(1, folders1.size());
        assertEquals(folders.get(0), folders1.get(0));
    }
    @Test
    void testCreateFolderSuccessful() throws AlreadyExists {
        User user = TestEntities.getDefaultUser1();
        Folder folder = new Folder(1L, "shrek_photos", "", null, null);
        when(folderRepository.findByPatronAndTitle(user, folder.getTitle())).thenReturn(Optional.empty());
        when(folderRepository.save(folder)).thenReturn(folder);

        Folder folder1 = folderService.createFolder(folder);
        assertEquals(1L, folder1.getId());
    }
    @Test
    void testCreateFolderFailed() {
        User user = TestEntities.getDefaultUser1();
        Folder folder = new Folder(1L, "shrek_photos", "", user, null);
        when(folderRepository.findByPatronAndTitle(user, folder.getTitle())).thenReturn(Optional.of(folder));

        assertThrows(AlreadyExists.class, () -> folderService.createFolder(folder));
    }
    @Test
    void testUpdateSuccessful() throws NotFound {
        Folder folder = new Folder(1L, "shrek_photos", "", null, null);
        when(folderRepository.findById(folder.getId())).thenReturn(Optional.of(folder));
        when(folderRepository.save(folder)).thenReturn(folder);

        Folder folder1 = folderService.update(folder.getId(), folder);
        assertEquals(1L, folder1.getId());
    }
    @Test
    void testUpdateFailed() {
        Folder folder = new Folder(1L, "shrek_photos", "", null, null);
        when(folderRepository.findById(folder.getId())).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> folderService.update(folder.getId(), folder));
    }
    @Test
    void testGetFoldersByPatronIdSuccessful() throws NotFound {
        List<Folder> folders = new ArrayList<>(List.of(new Folder(1L, "shrek_photos", "", null, null)));
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(folderRepository.findAllByPatron(user)).thenReturn(folders);

        List<Folder> folder1 = folderService.getFoldersByPatronId(user.getId());
        assertEquals(1, folder1.size());
    }
    @Test
    void testGetFoldersByPatronIdFailed() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> folderService.getFoldersByPatronId(1L));
    }

    @Test
    void testGetFolderById() throws Exception {
        Folder folder = new Folder(1L, "shrek_photos", "", null, null);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));

        Folder foundFolder = folderService.getFolderById(1L);
        assert(folder.equals(foundFolder));
        verify(folderRepository, times(1)).findById(1L);
    }
    @Test
    void testDeleteById() throws Exception {
        Folder folder = new Folder(1L, "shrek_photos", "", null, null);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));
        doNothing().when(folderRepository).delete(any(Folder.class));

        folderService.deleteById(1L);
        verify(folderRepository, times(1)).findById(1L);
        verify(folderRepository, times(1)).delete(any(Folder.class));
    }
    @Test
     void testAddPostsToFolder(){
        Folder folder = new Folder(1L, "shrek_photos", "", null, new ArrayList<>());
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null,null,null, new ArrayList<>(List.of(folder)), null),
                new Post(2L, "user2", "user2@example.com",  null, null,  null, new ArrayList<>(List.of(folder)), null)
        );
        folderService.addPostsToFolder(folder, mockPosts);
        assertEquals(mockPosts, folder.getPosts());
    }
    @Test
    void testDeletePostFromFolders(){
        Post post = new Post(1L, "user1", "user1@example.com", null,  null, null, null, null);
        List<Folder> mockFolders = List.of(
                new Folder(1L, "shrek_photos", "", null, new ArrayList<>(List.of(post))),
                new Folder(2L, "kek_photos", "", null, new ArrayList<>(List.of(post)))
        );
        when(folderRepository.saveAll(mockFolders)).thenReturn(null);

        folderService.deletePostFromFolders(mockFolders, post);
        assert(mockFolders.stream().allMatch(folder -> folder.getPosts().isEmpty()));
        verify(folderRepository, times(1)).saveAll(mockFolders);
    }
    @Test
    void testGetFoldersByPatron() throws NotFound {
        User user = TestEntities.getDefaultUser1();
        List<Folder> mockFolders = List.of(
                new Folder(1L, "shrek_photos", "", user, null),
                new Folder(2L, "kek_photos", "", user, null)
        );
        when(jwtService.getUserByToken()).thenReturn(user);
        when(folderRepository.findAllByPatron(user)).thenReturn(mockFolders);

        List<Folder> foundFolders = folderService.getFoldersByPatron();
        assert(mockFolders.equals(foundFolders));
        verify(folderRepository, times(1)).findAllByPatron(user);
    }
}
