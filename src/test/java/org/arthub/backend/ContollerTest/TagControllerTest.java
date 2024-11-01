package org.arthub.backend.ContollerTest;

import org.arthub.backend.Controller.TagController;
import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Facade.TagFacade;
import org.arthub.backend.Service.TagService;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
@AutoConfigureMockMvc
public class TagControllerTest {
    @Autowired
    private TagController tagController;
    @MockBean
    private TagService tagService;
    @MockBean
    private TagFacade tagFacade;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testGetTagById() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        tag.setId(1L);
        when(tagService.getTagById(1L)).thenReturn(tag);
        ResponseEntity<Tag> response = tagController.getTagById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tag, response.getBody());
        verify(tagService, times(1)).getTagById(1L);
    }
    @Test
    public void testCreateTags() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        when(tagService.createTags(List.of(tag))).thenReturn(List.of(tag));
        ResponseEntity<?> response = tagController.createTags(List.of(tag));
        assertEquals(200, response.getStatusCodeValue());
        verify(tagService, times(1)).createTags(List.of(tag));
    }
    @Test
    public void testDeleteTagById() throws Exception {
        doNothing().when(tagFacade).deleteById(1L);
        ResponseEntity<?> response = tagController.deleteTag(1L);
        assertEquals(200, response.getStatusCodeValue());
        verify(tagFacade, times(1)).deleteById(1L);
    }
}
