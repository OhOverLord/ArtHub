package org.arthub.backend.repository;

import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing folder-related data in the database.
 *
 * @since 1.0
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    /**
     * Finds a folder by its patron and title.
     *
     * @param patron the user who owns the folder
     * @param title the title of the folder
     * @return an Optional containing the folder if found, empty otherwise
     */
    Optional<Object> findByPatronAndTitle(User patron, String title);

    /**
     * Finds all folders belonging to a specific user.
     *
     * @param patron the user who owns the folders
     * @return a list of folders belonging to the user
     */
    List<Folder> findAllByPatron(User patron);
}
