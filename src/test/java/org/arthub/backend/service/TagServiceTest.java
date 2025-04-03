package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.Tag;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.repository.TagRepository;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TagServiceTest {
    @Autowired
    private TagService tagService;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;


    @Test
    void testReadAll(){
        List<Tag> tags = new ArrayList<>(List.of(
                TestEntities.getDefaultTag1(),
                TestEntities.getDefaultTag2()
        ));
        when(tagRepository.findAll()).thenReturn(tags);
        List<Tag> tags2 = tagService.readAll();
        assertEquals( 2, tags2.size());
        assertEquals(tags, tags2);
    }
    @Test
    void testCreateTagSuccessful() throws AlreadyExists {
        Tag tag = TestEntities.getDefaultTag1();
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag tag1 = tagService.createTag(tag);
        assertEquals(tag1, tag);
    }
    @Test
    void testCreateTagFailed() {
        Tag tag = TestEntities.getDefaultTag1();
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));

        assertThrows(AlreadyExists.class, () -> tagService.createTag(tag));
    }
    @Test
    void testGetTagsById() {
        List<Tag> tags = new ArrayList<>(List.of(
                TestEntities.getDefaultTag1(),
                TestEntities.getDefaultTag2()
        ));
        when(tagRepository.findAllById(List.of(1L, 2L))).thenReturn(tags);

        List<Tag> tags1 = tagService.getTagsByIds(List.of(1L, 2L));
        assertEquals(tags1.size(), tags.size());
        assertEquals(tags1, tags);
    }
    @Test
    void testCreateTags(){
        List<Tag> tags = new ArrayList<>(List.of(
                TestEntities.getDefaultTag1(),
                TestEntities.getDefaultTag2()
        ));
        when(tagRepository.saveAll(tags)).thenReturn(tags);

        List<Tag> tags1 = tagService.createTags(tags);
        assertEquals(tags1.size(), tags.size());
        assertEquals(tags1, tags);
    }
    @Test
    void testDeleteUserFromTagsSuccessful(){
        User user = TestEntities.getDefaultUser1();
        List<Tag> tags = new ArrayList<>(List.of(
                TestEntities.getDefaultTag1(),
                TestEntities.getDefaultTag2()
        ));
        tags.get(0).setUsers(new ArrayList<>(List.of(user)));
        tags.get(1).setUsers(new ArrayList<>(List.of(user)));
        when(tagRepository.saveAll(tags)).thenReturn(tags);

        tagService.deleteUserFromTags(tags, user);
        assertTrue(tags.get(0).getUsers().isEmpty());
        assertTrue(tags.get(1).getUsers().isEmpty());
    }
    @Test
    void testDeleteUserFromTagsEmptyTags(){
        User user = TestEntities.getDefaultUser1();
        List<Tag> tags = new ArrayList<>(List.of());
        when(tagRepository.saveAll(tags)).thenReturn(tags);
        tagService.deleteUserFromTags(tags, user);
        verify(tagRepository, times(0)).saveAll(tags);
    }
    @Test
    void testAddUserToTags(){
        User user = TestEntities.getDefaultUser1();
        List<Tag> tags = new ArrayList<>(List.of(
                TestEntities.getDefaultTag1(),
                TestEntities.getDefaultTag2()
        ));
        when(tagRepository.saveAll(tags)).thenReturn(tags);
        tags.get(0).setUsers(new ArrayList<>());
        tags.get(1).setUsers(new ArrayList<>());

        tagService.addUserToTags(tags, user);
        assertEquals(user, tags.get(0).getUsers().get(0));
        assertEquals(user, tags.get(1).getUsers().get(0));
    }
    @Test
    void testGetTagById() throws NotFound {
        Tag tag = TestEntities.getDefaultTag1();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag foundTag = tagService.getTagById(1L);
        assert(foundTag.equals(tag));
        verify(tagRepository, times(1)).findById(1L);
    }
    @Test
    void testDeleteById() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        tagService.deleteById(1L);
        verify(tagRepository, times(1)).delete(tag);
    }
    @Test
    void testAddPostToTags() {
        Tag tag1 = TestEntities.getDefaultTag1();
        tag1.setPosts(new ArrayList<>());
        Tag tag2 = TestEntities.getDefaultTag2();
        tag2.setPosts(new ArrayList<>());
        Post post = new Post(null, "full", "full", null,  null, null, null, null);
        when(tagRepository.saveAll(List.of(tag1, tag2))).thenReturn(null);

        tagService.addPostToTags(new ArrayList<>(List.of(tag1, tag2)), post);
        assert(tag1.getPosts().size() == 1);
        assert(tag2.getPosts().size() == 1);
        verify(tagRepository, times(1)).saveAll(List.of(tag1, tag2));
    }
    @Test
    void testDeletePostFromTags(){
        Post post = new Post(null, "full", "full", null,  null, null, null, null);
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
    void testDeletePostFromTag(){
        Post post = new Post(null, "full", "full", null,  null, null, null, null);
        Tag tag1 = TestEntities.getDefaultTag1();
        tag1.setPosts(new ArrayList<>(List.of(post)));
        when(tagRepository.save(any(Tag.class))).thenReturn(null);

        tagService.deletePostFromTag(tag1, post);
        assert(tag1.getPosts().isEmpty());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }
}
