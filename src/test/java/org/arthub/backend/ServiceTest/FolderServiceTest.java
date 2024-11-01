package org.arthub.backend.ServiceTest;

import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.FolderRepository;
import org.arthub.backend.Service.FolderService;
import org.arthub.backend.Service.JwtService;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class FolderServiceTest {
    @Autowired
    private FolderService folderService;
    @MockBean
    private FolderRepository folderRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testGetFolderById() throws Exception {
        Folder folder = new Folder(1L, "shrek_photos", "", null, null);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));

        Folder foundFolder = folderService.getFolderById(1L);
        assert(folder.equals(foundFolder));
        verify(folderRepository, times(1)).findById(1L);
    }
    @Test
    public void testDeleteById() throws Exception {
        Folder folder = new Folder(1L, "shrek_photos", "", null, null);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));
        doNothing().when(folderRepository).delete(any(Folder.class));

        folderService.deleteById(1L);
        verify(folderRepository, times(1)).findById(1L);
        verify(folderRepository, times(1)).delete(any(Folder.class));
    }
    @Test
    public void testAddPostsToFolder(){
        Folder folder = new Folder(1L, "shrek_photos", "", null, new ArrayList<>());
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null,  null, new ArrayList<>(List.of(folder)), null),
                new Post(2L, "user2", "user2@example.com",  null,  null, new ArrayList<>(List.of(folder)), null)
        );
        folderService.addPostsToFolder(folder, mockPosts);
        assert(folder.getPosts().equals(mockPosts));
    }
    @Test
    public void testDeletePostFromFolders(){
        Post post = new Post(1L, "user1", "user1@example.com", null,  null, null, null);
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
    public void testGetFoldersByPatron() throws NotFound {
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
