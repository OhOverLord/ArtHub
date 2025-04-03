package org.arthub.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.messages.LogMessages;
import org.arthub.backend.repository.FolderRepository;
import org.arthub.backend.repository.UserRepository;
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
    public Folder getFolderById(final Long folderId) throws NotFound {
        log.info(LogMessages.FOLDER_RETRIEVE_ATTEMPT.getMessage(), folderId);

        Folder folder = repository.findById(folderId).orElse(null);
        if (folder == null) {
            log.warn(LogMessages.FOLDER_NOT_FOUND.getMessage(), folderId);
            throw new NotFound();
        }

        log.info(LogMessages.FOLDER_RETRIEVED_SUCCESS.getMessage(), folderId);
        return folder;
    }


    /**
     * Creates a new folder.
     *
     * @param folder the folder to create
     * @return the newly created folder
     * @throws AlreadyExists if a folder with the same title and patron already exists
     */
    public Folder createFolder(final Folder folder) throws AlreadyExists {
        if (repository.findByPatronAndTitle(folder.getPatron(), folder.getTitle()).isPresent()) {
            log.error(LogMessages.FOLDER_ALREADY_EXISTS.getMessage());
            throw new AlreadyExists();
        }
        log.info(LogMessages.FOLDER_SAVED.getMessage(), folder);
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
    public Folder update(final Long folderId, final Folder folder) throws NotFound {
        log.info(LogMessages.FOLDER_UPDATE_ATTEMPT.getMessage(), folderId);

        Folder folderToUpdate = repository.findById(folderId).orElse(null);
        if (folderToUpdate == null) {
            log.warn(LogMessages.FOLDER_NOT_FOUND.getMessage(), folderId);
            throw new NotFound();
        }

        Folder updatedFolder = repository.save(folderToUpdate);

        log.info(LogMessages.FOLDER_UPDATE_SUCCESS.getMessage(), folderId);
        return updatedFolder;
    }


    /**
     * Deletes a folder by its ID.
     *
     * @param folderId the ID of the folder to delete
     * @throws NotFound if the folder is not found
     */
    public void deleteById(final Long folderId) throws NotFound {
        log.info(LogMessages.FOLDER_DELETE_ATTEMPT.getMessage(), folderId);

        Folder folderToDelete = repository.findById(folderId).orElse(null);
        if (folderToDelete == null) {
            log.warn(LogMessages.FOLDER_NOT_FOUND.getMessage(), folderId);
            throw new NotFound();
        }

        repository.delete(folderToDelete);
        log.info(LogMessages.FOLDER_DELETE_SUCCESS.getMessage(), folderId);
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
        log.info(LogMessages.FOLDERS_RETRIEVE_ATTEMPT.getMessage(), userId);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn(LogMessages.USER_NOT_FOUND.getMessage(), userId);
            throw new NotFound();
        }

        List<Folder> folders = repository.findAllByPatron(user);
        log.info(LogMessages.FOLDERS_RETRIEVED.getMessage(), folders.size(), userId);

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
        log.info(LogMessages.POST_DELETED_FROM_FOLDERS.getMessage(), post);
        repository.saveAll(folders);
    }

    /**
     * Retrieves folders by the ID of the patron from the JWT token.
     *
     * @return a list of folders belonging to the patron from the JWT token
     * @throws NotFound if the user is not found
     */
    public List<Folder> getFoldersByPatron() throws NotFound {
        log.info(LogMessages.FOLDERS_RETRIEVE_BY_TOKEN.getMessage());

        User user = jwtService.getUserByToken();
        if (user == null) {
            log.warn(LogMessages.USER_NOT_FOUND_BY_TOKEN.getMessage());
            throw new NotFound();
        }

        List<Folder> folders = repository.findAllByPatron(user);
        log.info(LogMessages.FOLDERS_RETRIEVED.getMessage(), folders.size(), user.getId());

        return folders;
    }

}
