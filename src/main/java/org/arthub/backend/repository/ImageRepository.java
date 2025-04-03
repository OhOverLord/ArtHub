package org.arthub.backend.repository;

import org.arthub.backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing image-related data in the database.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
