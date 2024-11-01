package org.arthub.backend.FacadeTest;

import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Facade.TagFacade;
import org.arthub.backend.Service.PostService;
import org.arthub.backend.Service.TagService;
import org.arthub.backend.Service.UserService;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class TagFacadeTest {
    @Autowired
    private TagFacade tagFacade;
    @MockBean
    private TagService tagService;
    @MockBean
    private PostService postService;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testDeleteByIdExisting() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        tag.setPosts(new ArrayList<>());
        when(tagService.getTagById(tag.getId())).thenReturn(tag);
        doNothing().when(tagService).deleteById(tag.getId());
        doNothing().when(postService).deleteTagsFromPost(tag, List.of());

        tagFacade.deleteById(tag.getId());
        verify(postService, times(1)).deleteTagsFromPost(tag, List.of());
        verify(tagService, atLeastOnce()).deleteById(tag.getId());
    }
    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        when(tagService.getTagById(1L)).thenReturn(null);
        assertThrows(NotFound.class, () -> tagFacade.deleteById(1L) );
    }
}
