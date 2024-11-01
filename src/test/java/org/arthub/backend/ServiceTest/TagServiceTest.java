package org.arthub.backend.ServiceTest;

import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.TagRepository;
import org.arthub.backend.Repository.UserRepository;
import org.arthub.backend.Service.TagService;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class TagServiceTest {
    @Autowired
    private TagService tagService;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testGetTagById() throws NotFound {
        Tag tag = TestEntities.getDefaultTag1();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag foundTag = tagService.getTagById(1L);
        assert(foundTag.equals(tag));
        verify(tagRepository, times(1)).findById(1L);
    }
    @Test
    public void testDeleteById() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        tagService.deleteById(1L);
        verify(tagRepository, times(1)).delete(tag);
    }
    @Test
    public void testAddPostToTags() throws Exception {
        Tag tag1 = TestEntities.getDefaultTag1();
        tag1.setPosts(new ArrayList<>());
        Tag tag2 = TestEntities.getDefaultTag2();
        tag2.setPosts(new ArrayList<>());
        Post post = new Post(null, "full", "full", null,  null, null, null);
        when(tagRepository.saveAll(List.of(tag1, tag2))).thenReturn(null);

        tagService.addPostToTags(new ArrayList<>(List.of(tag1, tag2)), post);
        assert(tag1.getPosts().size() == 1);
        assert(tag2.getPosts().size() == 1);
        verify(tagRepository, times(1)).saveAll(List.of(tag1, tag2));
    }
    @Test
    public void testDeletePostFromTags(){
        Post post = new Post(null, "full", "full", null,  null, null, null);
        Tag tag1 = TestEntities.getDefaultTag1();
        tag1.setPosts(new ArrayList<>(List.of(post)));
        Tag tag2 = TestEntities.getDefaultTag2();
        tag2.setPosts(new ArrayList<>(List.of(post)));
        when(tagRepository.saveAll(List.of(tag1, tag2))).thenReturn(null);

        tagService.deletePostFromTags(List.of(tag1, tag2), post);
        assert(tag1.getPosts().isEmpty());
        assert(tag2.getPosts().isEmpty());
        verify(tagRepository, times(1)).saveAll(List.of(tag1, tag2));
    }
    @Test
    public void testDeletePostFromTag(){
        Post post = new Post(null, "full", "full", null,  null, null, null);
        Tag tag1 = TestEntities.getDefaultTag1();
        tag1.setPosts(new ArrayList<>(List.of(post)));
        when(tagRepository.save(any(Tag.class))).thenReturn(null);

        tagService.deletePostFromTag(tag1, post);
        assert(tag1.getPosts().isEmpty());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }
}
