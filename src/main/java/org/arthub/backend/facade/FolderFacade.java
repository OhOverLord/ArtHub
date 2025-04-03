package org.arthub.backend.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.dto.FolderDTO;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.repository.FolderRepository;
import org.arthub.backend.service.FolderService;
import org.arthub.backend.service.JwtService;
import org.arthub.backend.service.PostService;
import org.arthub.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade for folder operations such as creation, update, and deletion.
 * This class acts as a bridge between the services and the controllers.
 * It handles business logic related to folders, posts, and users.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FolderFacade {
    /**
     * Service for handling folder-related business logic.
     */
    private final FolderService folderService;

    /**
     * Service for handling post-related business logic.
     */
    private final PostService postService;

    /**
     * Service for handling user-related business logic.
     */
    private final UserService userService;

    /**
     * Repository for performing CRUD operations on folders.
     */
    private final FolderRepository folderRepository;

    /**
     * Service for handling JWT authentication
     * and user retrieval based on tokens.
     */
    private final JwtService jwtService;


    /**
     * Creates a new folder and associates it with the user and posts.
     *
     * @param folderDTO the data transfer object containing the folder's details
     * @return the newly created folder
     * @throws NotFound if there is an issue during folder creation
     */
    @Transactional
    public Folder createFolder(final FolderDTO folderDTO) throws NotFound {
        User user = jwtService.getUserByToken();

        Folder newFolder = folderFromDTO(folderDTO, user);

        log.info("Folder created: {}", newFolder);
        userService.addFolderToUser(newFolder.getPatron(), newFolder);
        postService.addFolderToPosts(newFolder.getPosts(), newFolder);
        return newFolder;
    }

    /**
     * Converts a FolderDTO object into
     * a Folder entity and saves it in the repository.
     *
     * @param folderDTO the folder DTO
     * @param user      the user who created the folder
     * @return the saved Folder entity
     */
    private Folder folderFromDTO(
            final FolderDTO folderDTO, final User user) {
        Folder folder = new Folder();
        folder.setTitle(folderDTO.getTitle());
        folder.setDescription(folderDTO.getDescription());
        folder.setPatron(user);
        folder.setPosts(postService.getPostsById(folderDTO.getPostIds()));
        log.info("Folder created from DTO: {}", folder);
        return folderRepository.save(folder);
    }

    /**
     * Updates an existing folder with new details.
     *
     * @param folderId  the ID of the folder to be updated
     * @param folderDTO the updated folder details
     * @return the updated folder entity
     * @throws NotFound if the folder is not found
     * or if there are any issues during the update
     */
    @Transactional
    public Folder updateFolder(
            final Long folderId, final FolderDTO folderDTO) throws NotFound {
        Folder oldFolder = folderService.getFolderById(folderId);
        oldFolder.setTitle(folderDTO.getTitle());
        oldFolder.setDescription(folderDTO.getDescription());
        updatePostsInFolder(folderDTO.getPostIds(), oldFolder);
        log.info("Folder updated: {}", oldFolder);
        return folderService.update(folderId, oldFolder);
    }

    /**
     * Updates the posts associated with
     * a folder by adding new posts and removing old ones.
     *
     * @param postIds the list of new post IDs
     * @param folder  the folder to update
     */
    private void updatePostsInFolder(
            final List<Long> postIds, final Folder folder) {
        List<Post> newPosts = postService.getPostsById(postIds);
        if (newPosts.isEmpty()) {
            deleteAllPostsInFolder(folder);
            return;
        }

        List<Post> oldPosts = new ArrayList<>(folder.getPosts());
        for (Post post : oldPosts) {
            if (!newPosts.contains(post)) {
                postService.deleteFolderFromPost(folder, post);
                folder.getPosts().remove(post);
            } else {
                newPosts.remove(post);
            }
        }

        folderService.addPostsToFolder(folder, newPosts);
        postService.addFolderToPosts(newPosts, folder);
    }

    /**
     * Removes all posts associated with a folder.
     *
     * @param folder the folder from which to remove all posts
     */
    private void deleteAllPostsInFolder(final Folder folder) {
        List<Post> posts = folder.getPosts();
        postService.deleteFolderFromPosts(folder, posts);
        folder.setPosts(null);
    }

    /**
     * Deletes a folder by its ID and
     * removes all associations with posts and users.
     *
     * @param folderId the ID of the folder to delete
     * @throws NotFound if the folder is not found
     * or there is an issue during deletion
     */
    public void deleteById(final Long folderId) throws NotFound {
        Folder folderToDelete = folderService.getFolderById(folderId);

        postService.deleteFolderFromPosts(
                folderToDelete,
                folderToDelete.getPosts()
        );

        userService.deleteFolderFromUser(
                folderToDelete.getPatron(),
                folderToDelete
        );

        folderService.deleteById(folderId);
    }

}
