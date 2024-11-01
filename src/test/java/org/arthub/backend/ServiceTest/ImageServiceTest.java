package org.arthub.backend.ServiceTest;

import jakarta.persistence.EntityNotFoundException;
import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Image;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.ImageRepository;
import org.arthub.backend.Service.ImageService;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class ImageServiceTest {
    @Autowired
    private ImageService imageService;
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testCreateImage() throws Exception {
        Image image = new Image();
        image.setId(1L);
        image.setData(TestEntities.createMockMultipartFile().getBytes());
        when(imageRepository.save(any(Image.class))).thenReturn(image);
        Image createdImage = imageService.createImage(TestEntities.createMockMultipartFile());

        assert(createdImage.getData().length != 0);
        verify(imageRepository, times(1)).save(any(Image.class));
    }
    @Test
    public void testDeleteByIdImage() throws Exception {
        Image image = new Image();
        image.setId(1L);
        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        doNothing().when(imageRepository).delete(image);
        imageService.deleteById(1L);
        verify(imageRepository, times(1)).delete(image);
    }
    @Test
    public void testDeleteByIdImageNotFound() throws Exception {
        Image image = new Image();
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(imageRepository).delete(image);
        assertThrows(NotFound.class, () -> imageService.deleteById(1L));
    }
    @Test
    public void testAddPostToImages(){
        Image image = new Image();
        Post post = new Post(1L, "full", "full", null,  null, null, null);
        when(imageRepository.save(any(Image.class))).thenReturn(null);
        imageService.addPostToImages(image, post);
        verify(imageRepository, times(1)).save(any(Image.class));
    }
    @Test
    public void testDeletePostFromImage(){
        Image image = new Image();
        Post post = new Post(1L, "full", "full", null,  null, null, null);
        image.setPost(post);
        doNothing().when(imageRepository).delete(any(Image.class));
        imageService.deletePostFromImage(image, post);
        assert(image.getPost() == null);
        verify(imageRepository, times(1)).delete(any(Image.class));
    }
}
