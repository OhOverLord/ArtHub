/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses related to folder operations.
 * </p>
 */
package org.arthub.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.dto.FolderDTO;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.facade.FolderFacade;
import org.arthub.backend.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing folder-related operations.
 * <p>
 * Provides endpoints for retrieving, creating, updating, and deleting folders.
 * </p>
 *
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/folders")
public class FolderController {

    /**
     * Service for managing folder-related operations.
     */
    private final FolderService service;

    /**
     * Facade for performing complex folder-related operations.
     */
    private final FolderFacade folderFacade;

    /**
     * Retrieves all folders.
     *
     * @return a {@link List} of all {@link Folder} entities
     */
    @GetMapping
    public List<Folder> getAllFolders() {
        return service.readAll();
    }

    /**
     * Retrieves a folder by its ID.
     *
     * @param folderId the ID of the folder to retrieve
     * @return {@link ResponseEntity} containing the requested {@link Folder}
     * @throws NotFound if an error occurs during retrieval
     */
    @GetMapping("/{folderId}")
    public ResponseEntity<Folder> getFolderById(@PathVariable final Long folderId) throws NotFound {
        Folder folder = service.getFolderById(folderId);
        return ResponseEntity.ok(folder);
    }

    /**
     * Retrieves folders for the authenticated user.
     *
     * @return {@link ResponseEntity} containing a list of {@link Folder} entities for the user
     * @throws NotFound if the folders for the user are not found
     */
    @GetMapping("/user")
    public ResponseEntity<List<Folder>> getFoldersByUser() throws NotFound {
        List<Folder> folders = service.getFoldersByPatron();
        return ResponseEntity.ok(folders);
    }

    /**
     * Creates a new folder.
     *
     * @param folderDTO the {@link FolderDTO} object containing folder details
     * @return {@link ResponseEntity} containing the newly created {@link Folder}
     * @throws NotFound if an error occurs during creation
     */
    @PostMapping
    public ResponseEntity<Folder> createFolder(
            @Validated(FolderDTO.class) @RequestBody final FolderDTO folderDTO
    ) throws NotFound {
        Folder newFolder = folderFacade.createFolder(folderDTO);
        return ResponseEntity.ok(newFolder);
    }

    /**
     * Updates an existing folder.
     *
     * @param folderId  the ID of the folder to update
     * @param folderDTO the {@link FolderDTO} object containing updated folder details
     * @return {@link ResponseEntity} containing the updated {@link Folder}
     * @throws NotFound if an error occurs during update
     */
    @PutMapping("/{folderId}")
    public ResponseEntity<Folder> updateFolder(
            @PathVariable final Long folderId,
            @Validated(FolderDTO.class) @RequestBody final FolderDTO folderDTO
    ) throws NotFound {
        Folder updatedFolder = folderFacade.updateFolder(folderId, folderDTO);
        return ResponseEntity.ok(updatedFolder);
    }

    /**
     * Deletes a folder by its ID.
     *
     * @param folderId the ID of the folder to delete
     * @return {@link ResponseEntity} indicating the result of the deletion
     * @throws NotFound if an error occurs during deletion
     */
    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable final Long folderId) throws NotFound {
        folderFacade.deleteById(folderId);
        return ResponseEntity.ok().build();
    }
}
