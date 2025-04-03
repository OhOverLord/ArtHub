package org.arthub.backend.controller;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Tag;
import org.arthub.backend.facade.TagFacade;
import org.arthub.backend.service.TagService;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class TagControllerTest {
    @Autowired
    private TagController tagController;
    @MockBean
    private TagService tagService;
    @MockBean
    private TagFacade tagFacade;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testGetTagById() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        tag.setId(1L);
        when(tagService.getTagById(1L)).thenReturn(tag);
        ResponseEntity<Tag> response = tagController.getTagById(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(tag, response.getBody());
        verify(tagService, times(1)).getTagById(1L);
    }
    @Test
    void testCreateTags() {
        Tag tag = TestEntities.getDefaultTag1();
        when(tagService.createTags(List.of(tag))).thenReturn(List.of(tag));
        ResponseEntity<?> response = tagController.createTags(List.of(tag));
        assertEquals(200, response.getStatusCode().value());
        verify(tagService, times(1)).createTags(List.of(tag));
    }
    @Test
    void testDeleteTagById() throws Exception {
        doNothing().when(tagFacade).deleteById(1L);
        ResponseEntity<?> response = tagController.deleteTag(1L);
        assertEquals(200, response.getStatusCode().value());
        verify(tagFacade, times(1)).deleteById(1L);
    }
}
