package org.arthub.backend.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.Entity.Image;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service class for managing images.
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
    public Image getImageById(final Long imageId) throws Exception {
        log.info("Retrieving image with ID: {}", imageId);

        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) {
            log.warn("Image not found for ID: {}", imageId);
            throw new NotFound();
        }

        log.info("Successfully retrieved image with ID: {}", imageId);
        return image;
    }


    /**
     * Creates a new image from the provided file.
     *
     * @param file the multipart file representing the image
     * @return the newly created image
     * @throws NotFound if the file is empty or null
     * @throws Exception if an I/O error occurs
     */
    public Image createImage(final MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            log.error("File is empty or null");
            throw new NotFound();
        }

        log.info("Creating a new image from the provided file: {}", file.getOriginalFilename());

        Image image = new Image();
        image.setData(file.getBytes());

        Image savedImage = imageRepository.save(image);
        log.info("Successfully created image with ID: {}", savedImage.getId());

        return savedImage;
    }


    /**
     * Deletes an image by its ID.
     *
     * @param imageId the ID of the image to delete
     * @throws NotFound if the image is not found
     */
    public void deleteById(final Long imageId) throws Exception {
        log.info("Attempting to delete image with ID: {}", imageId);

        Image imageToDelete = imageRepository.findById(imageId).orElse(null);
        if (imageToDelete == null) {
            log.warn("Image not found for ID: {}", imageId);
            throw new NotFound();
        }

        imageRepository.delete(imageToDelete);
        log.info("Successfully deleted image with ID: {}", imageId);
    }


    /**
     * Associates a post with an image.
     *
     * @param image   the image to associate the post with
     * @param newPost the post to associate with the image
     */
    public void addPostToImages(final Image image, final Post newPost) {
        log.info("Post added to images: {}", newPost);
        image.setPost(newPost);
        imageRepository.save(image);
    }

    /**
     * Removes the association of a post from an image.
     *
     * @param image        the image to disassociate the post from
     * @param updatedPost the post to disassociate from the image
     */
    public void deletePostFromImage(final Image image, final Post updatedPost) {
        log.info("Post deleted from images: {}", updatedPost);
        image.setPost(null);
        imageRepository.delete(image);
    }
}
