/**
 * This package contains utility classes for the Arthub backend application.
 * <p>
 * These classes provide auxiliary functionality, such as application event listeners,
 * synchronization utilities, and other supporting components.
 * </p>
 */
package org.arthub.backend.util;

import lombok.AllArgsConstructor;
import org.arthub.backend.service.ElasticsearchSyncService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener class for handling database initialization tasks upon application startup.
 * <p>
 * This listener triggers the synchronization of posts from the database to Elasticsearch
 * when the application is fully initialized.
 * </p>
 *
 * @since 1.0
 */
@Component
@AllArgsConstructor
public class DatabaseInitializationListener {

    /**
     * Service for synchronizing database posts with Elasticsearch.
     */
    private final ElasticsearchSyncService syncService;

    /**
     * Event listener method triggered when the application is ready.
     * <p>
     * This method calls {@link ElasticsearchSyncService#syncPosts()} to ensure that
     * all posts are synchronized with Elasticsearch upon application startup.
     * </p>
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        syncService.syncPosts();
    }
}
