package org.arthub.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Image;
import org.arthub.backend.entity.Post;
import org.arthub.backend.exception.EmptyFile;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ImageServiceTest {
    @Autowired
    private ImageService imageService;
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private Cloudinary cloudinary;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testGetImageByIdSuccessful() throws NotFound{
        MultipartFile multipartFile = TestEntities.createMockMultipartFile();
        Image image = new Image();
        image.setId(1L);
        image.setFile(multipartFile.toString());
        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        Image foundImage = imageService.getImageById(1L);
        assertEquals(foundImage.getFile(), multipartFile.toString());
        verify(imageRepository, times(1)).findById(1L);
    }
    @Test
    void testGetImageByIdFailed(){
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());
        NotFound exception = assertThrows(NotFound.class, () -> {
            imageService.getImageById(1L);
        });
        assertEquals("Not Found", exception.getMessage());
    }
    @Test
     void testCreateImageSuccessful() throws Exception {
        MultipartFile multipartFile = TestEntities.createMockMultipartFile();
        Uploader mockUploader = mock(Uploader.class);
        Image image = new Image();
        image.setId(1L);
        image.setFile(multipartFile.toString());
        when(cloudinary.uploader()).thenReturn(mockUploader);
        when(cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.emptyMap())).thenReturn(Map.of("url", multipartFile));
        when(imageRepository.save(any(Image.class))).thenReturn(image);
        Image createdImage = imageService.createImage(multipartFile);

        assertFalse(createdImage.getFile().isEmpty());
        verify(imageRepository, times(1)).save(any(Image.class));
    }
    @Test
     void testCreateImageFailed() throws Exception {
        MultipartFile multipartFile = TestEntities.createMockMultipartFile();
        Uploader mockUploader = mock(Uploader.class);
        Image image = new Image();
        image.setId(1L);
        image.setFile(multipartFile.toString());
        when(cloudinary.uploader()).thenReturn(mockUploader);
        when(cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.emptyMap())).thenThrow(IOException.class);
        Image createdImage = imageService.createImage(multipartFile);

        assertNull(createdImage);
    }
    @Test
     void testCreateImageEmptyFile() {
        EmptyFile thrown = assertThrows(EmptyFile.class, () -> {
            imageService.createImage(new MockMultipartFile(
                    "file","",  "text/plain", new byte[0]));
        });
        assertEquals("Empty file", thrown.getMessage());
    }
    @Test
     void testDeleteByIdImage() throws Exception {
        Image image = new Image();
        image.setId(1L);
        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        doNothing().when(imageRepository).delete(image);
        imageService.deleteById(1L);
        verify(imageRepository, times(1)).delete(image);
    }
    @Test
     void testDeleteByIdImageNotFound() {
        Image image = new Image();
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(imageRepository).delete(image);
        assertThrows(NotFound.class, () -> imageService.deleteById(1L));
    }
    @Test
     void testAddPostToImages(){
        Image image = new Image();
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        when(imageRepository.save(any(Image.class))).thenReturn(null);
        imageService.addPostToImages(image, post);
        verify(imageRepository, times(1)).save(any(Image.class));
    }
    @Test
     void testDeletePostFromImage(){
        Image image = new Image();
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        image.setPost(post);
        doNothing().when(imageRepository).delete(any(Image.class));
        imageService.deletePostFromImage(image, post);
        assertNull(image.getPost());
        verify(imageRepository, times(1)).delete(any(Image.class));
    }
}
