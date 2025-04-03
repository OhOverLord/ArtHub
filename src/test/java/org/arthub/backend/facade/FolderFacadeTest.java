package org.arthub.backend.facade;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.dto.FolderDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.User;
import org.arthub.backend.repository.FolderRepository;
import org.arthub.backend.service.FolderService;
import org.arthub.backend.service.JwtService;
import org.arthub.backend.service.PostService;
import org.arthub.backend.service.UserService;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class FolderFacadeTest {
    @Autowired
    private FolderFacade folderFacade;
    @MockBean
    private FolderService folderService;
    @MockBean
    private PostService postService;
    @MockBean
    private UserService userService;
    @MockBean
    private FolderRepository folderRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    //includes test for private method FolderFromDTO()
    void testCreateFolder() throws Exception {
        User user = TestEntities.getDefaultUser1();
        FolderDTO folderDTO = new FolderDTO("shrek_photos", " ", new ArrayList<>());
        Folder folder = new Folder();
        folder.setTitle(folderDTO.getTitle());
        folder.setDescription(folderDTO.getDescription());
        folder.setPatron(user);
        folder.setPosts(List.of());

        when(jwtService.getUserByToken()).thenReturn(user);
        when(folderRepository.save(any(Folder.class))).thenReturn(folder);
        doNothing().when(userService).addFolderToUser(any(User.class), any(Folder.class));
        doNothing().when(postService).addFolderToPosts(List.of(), folder);
        when(postService.getPostsById(List.of())).thenReturn(List.of());

        Folder createdFolder = folderFacade.createFolder(folderDTO);
        assert(createdFolder.getTitle().equals("shrek_photos"));
        assert(createdFolder.getPatron().equals(user));
        verify(userService, times(1)).addFolderToUser(any(User.class), any(Folder.class));
        verify(postService, times(1)).addFolderToPosts(List.of(), folder);
    }
    @Test
    void testUpdateFolderNonExistingNewPosts() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        FolderDTO folderDTO = new FolderDTO("shrek_photos", " ", new ArrayList<>(List.of(2L)));
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        Folder folderToUpdate = new Folder();
        folderToUpdate.setId(1L);
        folderToUpdate.setPatron(null);
        folderToUpdate.setPosts(new ArrayList<>(List.of(post)));

        // mocking inside updateFolder()
        when(folderService.getFolderById(1L)).thenReturn(folderToUpdate);
        when(folderService.update(1L, folderToUpdate)).thenReturn(folderToUpdate);
        // mocking inside updatePostsInFolder()
        when(postService.getPostsById(folderDTO.getPostIds())).thenReturn(List.of());
        // mocking inside deleteAllPostsInFolder()
        doNothing().when(postService).deleteFolderFromPosts(folderToUpdate, List.of(post));

        folderToUpdate = folderFacade.updateFolder(1L, folderDTO);

        assert(folder.getTitle().equals(folderToUpdate.getTitle()));
        assert(folder.getDescription().equals(folderToUpdate.getDescription()));
        assert(folderToUpdate.getPosts() == null);
        verify(postService, times(1)).deleteFolderFromPosts(folderToUpdate, List.of(post));
    }
    @Test
    void testUpdateFolderExistingNewPosts() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        Post newPost = new Post(2L, "full", "empty", null,  null, null, null, null);
        FolderDTO folderDTO = new FolderDTO("shrek_photos", " ", new ArrayList<>(List.of(1L, 2L)));
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>(List.of(post, newPost)));
        Folder folderToUpdate = new Folder();
        folderToUpdate.setId(1L);
        folderToUpdate.setPatron(null);
        folderToUpdate.setPosts(new ArrayList<>());

        // mocking inside updateFolder()
        when(folderService.getFolderById(1L)).thenReturn(folderToUpdate);
        when(folderService.update(1L, folderToUpdate)).thenReturn(folderToUpdate);
        // mocking inside updatePostsInFolder()
        when(postService.getPostsById(folderDTO.getPostIds())).thenReturn(List.of(post, newPost));
        doNothing().when(postService).deleteFolderFromPost(any(Folder.class), any(Post.class));
        doAnswer(invocation ->{
            Folder tmpFolder = invocation.getArgument(0);
            tmpFolder.setPosts(invocation.getArgument(1));
            return null;
        }).when(folderService).addPostsToFolder(folderToUpdate, List.of(post, newPost));
        doNothing().when(postService).addFolderToPosts(List.of(post, newPost), folderToUpdate);

        folderToUpdate = folderFacade.updateFolder(1L, folderDTO);

        assert(folder.getTitle().equals(folderToUpdate.getTitle()));
        assert(folder.getDescription().equals(folderToUpdate.getDescription()));
        assert(folderToUpdate.getPosts().equals(List.of(post, newPost)));
        verify(postService, times(0)).deleteFolderFromPost(any(Folder.class), any(Post.class));
        verify(folderService, times(1)).addPostsToFolder(folderToUpdate, List.of(post, newPost));
        verify(postService, times(1)).addFolderToPosts(List.of(post, newPost), folderToUpdate);
    }
    @Test
    void testDeleteById() throws Exception {
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderService.getFolderById(1L)).thenReturn(folder);
        doNothing().when(postService).deleteFolderFromPosts(folder, folder.getPosts());
        doNothing().when(userService).deleteFolderFromUser(folder.getPatron(), folder);
        doNothing().when(folderService).deleteById(1L);

        folderFacade.deleteById(1L);
        verify(folderService, times(1)).getFolderById(1L);
        verify(folderService, times(1)).deleteById(1L);
        verify(postService, times(1)).deleteFolderFromPosts(folder, folder.getPosts());
        verify(userService, times(1)).deleteFolderFromUser(folder.getPatron(), folder);
    }
}
