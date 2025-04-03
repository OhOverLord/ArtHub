package org.arthub.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.entity.Image;
import org.arthub.backend.entity.Post;
import org.arthub.backend.exception.EmptyFile;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.messages.LogMessages;
import org.arthub.backend.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing images.
 * <p>
 * This service provides methods for creating, retrieving, associating, and deleting images.
 * It integrates with Cloudinary for image storage and the database for managing image entities.
 * </p>
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    /**
     * Repository for managing image entities in the database.
     */
    private final ImageRepository imageRepository;

    /**
     * Cloudinary client for uploading and managing images in Cloudinary.
     */
    private final Cloudinary cloudinary;

    /**
     * Retrieves all images from the database.
     *
     * @return a list of all images
     */
    public List<Image> readAll() {
        return imageRepository.findAll();
    }

    /**
     * Retrieves an image by its ID.
     *
     * @param imageId the ID of the image to retrieve
     * @return the image with the specified ID
     * @throws NotFound if the image is not found
     */
    public Image getImageById(final Long imageId) throws NotFound {
        log.info(LogMessages.IMAGE_RETRIEVE_ATTEMPT.getMessage(), imageId);

        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) {
            log.warn(LogMessages.IMAGE_NOT_FOUND.getMessage(), imageId);
            throw new NotFound();
        }

        log.info(LogMessages.IMAGE_RETRIEVED_SUCCESS.getMessage(), imageId);
        return image;
    }

    /**
     * Creates a new image by uploading it to Cloudinary.
     *
     * @param file the file to upload and create an image from
     * @return the created {@link Image} entity
     * @throws EmptyFile if an error occurs during file upload or the file is empty
     */
    @SuppressWarnings("unchecked")
    public Image createImage(final MultipartFile file) throws EmptyFile {
        if (file.isEmpty()) {
            log.error(LogMessages.FILE_EMPTY_ERROR.getMessage());
            throw new EmptyFile();
        }

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            log.info(LogMessages.IMAGE_CREATION_ATTEMPT.getMessage(), file.getOriginalFilename());
            Image image = new Image();
            image.setFile(uploadResult.get("url").toString());

            Image savedImage = imageRepository.save(image);
            log.info(LogMessages.IMAGE_CREATED_SUCCESS.getMessage(), savedImage.getId());

            return savedImage;
        } catch (IOException ex) {
            log.info(ex.getMessage());
        }

        return null;
    }

    /**
     * Deletes an image by its ID.
     *
     * @param imageId the ID of the image to delete
     * @throws NotFound if the image is not found
     */
    public void deleteById(final Long imageId) throws NotFound {
        log.info(LogMessages.IMAGE_DELETE_ATTEMPT.getMessage(), imageId);

        Image imageToDelete = imageRepository.findById(imageId).orElse(null);
        if (imageToDelete == null) {
            log.warn(LogMessages.IMAGE_NOT_FOUND.getMessage(), imageId);
            throw new NotFound();
        }

        imageRepository.delete(imageToDelete);
        log.info(LogMessages.IMAGE_DELETED_SUCCESS.getMessage(), imageId);
    }

    /**
     * Associates a post with an image.
     *
     * @param image   the image to associate the post with
     * @param newPost the post to associate with the image
     */
    public void addPostToImages(final Image image, final Post newPost) {
        log.info(LogMessages.POST_ADDED_TO_IMAGE.getMessage(), newPost);
        image.setPost(newPost);
        imageRepository.save(image);
    }

    /**
     * Removes the association of a post from an image.
     *
     * @param image       the image to disassociate the post from
     * @param updatedPost the post to disassociate from the image
     */
    public void deletePostFromImage(final Image image, final Post updatedPost) {
        log.info(LogMessages.POST_REMOVED_FROM_IMAGE.getMessage(), updatedPost);
        image.setPost(null);
        imageRepository.delete(image);
    }
}
