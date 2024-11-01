package org.arthub.backend.Repository;

import org.arthub.backend.Entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing tag-related data in the database.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Finds a tag by its name.
     *
     * @param name the name of the tag to find
     * @return an Optional containing the tag if found, empty otherwise
     */
    Optional<Object> findByName(String name);
}
