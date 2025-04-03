package org.arthub.backend.repository;

import org.arthub.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing comments in the database.
 * Provides methods for CRUD operations and custom queries.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
