package org.arthub.backend.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Exception.AlreadyExists;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.FolderRepository;
import org.arthub.backend.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing folders.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FolderService {
    /**
     * The repository for managing folder entities.
     */
    private final FolderRepository repository;

    /**
     * The repository for managing user entities.
     */
    private final UserRepository userRepository;

    /**
     * Service responsible for handling JWT tokens and user authentication.
     */
    private final JwtService jwtService;


    /**
     * Retrieves all folders from the database.
     *
     * @return a list of all folders
     */
    public List<Folder> readAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a folder by its ID.
     *
     * @param folderId the ID of the folder to retrieve
     * @return the folder with the specified ID
     * @throws NotFound if the folder is not found
     */
    public Folder getFolderById(final Long folderId) throws Exception {
        log.info("Retrieving folder with ID: {}", folderId);

        Folder folder = repository.findById(folderId).orElse(null);
        if (folder == null) {
            log.warn("Folder not found for ID: {}", folderId);
            throw new NotFound();
        }

        log.info("Successfully retrieved folder with ID: {}", folderId);
        return folder;
    }


    /**
     * Creates a new folder.
     *
     * @param folder the folder to create
     * @return the newly created folder
     * @throws AlreadyExists if a folder with the same title and patron already exists
     */
    public Folder createFolder(final Folder folder) throws Exception {
        if (repository.findByPatronAndTitle(folder.getPatron(), folder.getTitle()).isPresent()) {
            log.error("Folder already exists");
            throw new AlreadyExists();
        }
        log.info("Folder saved to database: {}", folder);
        return repository.save(folder);
    }

    /**
     * Updates a folder.
     *
     * @param folderId the ID of the folder to update
     * @param folder   the updated folder
     * @return the updated folder
     * @throws NotFound if the folder to update is not found
     */
    public Folder update(final Long folderId, final Folder folder) throws Exception {
        log.info("Attempting to update folder with ID: {}", folderId);

        Folder folderToUpdate = repository.findById(folderId).orElse(null);
        if (folderToUpdate == null) {
            log.warn("Folder not found for ID: {}", folderId);
            throw new NotFound();
        }

        Folder updatedFolder = repository.save(folderToUpdate);

        log.info("Successfully updated folder with ID: {}", folderId);
        return updatedFolder;
    }


    /**
     * Deletes a folder by its ID.
     *
     * @param folderId the ID of the folder to delete
     * @throws NotFound if the folder is not found
     */
    public void deleteById(final Long folderId) throws Exception {
        log.info("Attempting to delete folder with ID: {}", folderId);

        Folder folderToDelete = repository.findById(folderId).orElse(null);
        if (folderToDelete == null) {
            log.warn("Folder not found for ID: {}", folderId);
            throw new NotFound();
        }

        repository.delete(folderToDelete);
        log.info("Successfully deleted folder with ID: {}", folderId);
    }


    /**
     * Adds posts to a folder.
     *
     * @param folder   the folder to add posts to
     * @param newPosts the posts to add to the folder
     */
    public void addPostsToFolder(final Folder folder, final List<Post> newPosts) {
        folder.getPosts().addAll(newPosts);
    }

    /**
     * Retrieves folders by the ID of the patron.
     *
     * @param userId the ID of the patron
     * @return a list of folders belonging to the patron
     * @throws NotFound if the user is not found
     */
    public List<Folder> getFoldersByPatronId(final Long userId) throws NotFound {
        log.info("Attempting to retrieve folders for patron with ID: {}", userId);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("User not found for ID: {}", userId);
            throw new NotFound();
        }

        List<Folder> folders = repository.findAllByPatron(user);
        log.info("Retrieved {} folders for patron with ID: {}", folders.size(), userId);

        return folders;
    }


    /**
     * Deletes a post from multiple folders.
     *
     * @param folders the folders to delete the post from
     * @param post    the post to delete
     */
    public void deletePostFromFolders(final List<Folder> folders, final Post post) {
        folders.forEach(folder -> folder.getPosts().remove(post));
        log.info("Post deleted from folders: {}", post);
        repository.saveAll(folders);
    }

    /**
     * Retrieves folders by the ID of the patron from the JWT token.
     *
     * @return a list of folders belonging to the patron from the JWT token
     * @throws NotFound if the user is not found
     */
    public List<Folder> getFoldersByPatron() throws NotFound {
        log.info("Retrieving folders for the patron using JWT token.");

        User user = jwtService.getUserByToken();
        if (user == null) {
            log.warn("User not found from JWT token.");
            throw new NotFound();
        }

        List<Folder> folders = repository.findAllByPatron(user);
        log.info("Retrieved {} folders for patron with ID: {}", folders.size(), user.getId());

        return folders;
    }

}
