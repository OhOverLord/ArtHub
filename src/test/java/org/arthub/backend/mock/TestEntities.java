package org.arthub.backend.mock;

import org.arthub.backend.entity.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestEntities {
    public static final PostElasticsearch ELASTICLONGDUMMY = new PostElasticsearch("1", "long_description", "Life is either a daring adventure or nothing at all. " +
                                                                                                "Security is mostly a superstition. It does not exist in nature.", 1L, new ArrayList<>());
    public static final PostElasticsearch ELASTICSHORTDUMMY = new PostElasticsearch("2", "short_description", "When in Rome, do as the Romans do.", 2L, new ArrayList<>());
    public static final Pageable PAGEABLE = PageRequest.of(0, 5);
    public static final Comment COMMENT1 = new Comment(1L, "Stop posting shrek photos", null, "john_doe", LocalDateTime.MAX);
    public static final Comment COMMENT2 = new Comment(2L, "I need photos of spider man", null, "john_doe", LocalDateTime.MIN);

    public static PasswordResetToken getResetToken() {
        return new PasswordResetToken(1L, "token", null, LocalDateTime.now(), false);
    }
    public static User getDefaultUser1() {
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
        user.setEmail("john.doe@example.com");
        user.setFolders(new ArrayList<>());
        user.setPreferredTags(new ArrayList<>());
        user.setPosts(new ArrayList<>());
        return user;
    }
    public static User getDefaultUser2 (){
        User user = new User();
        user.setUsername("homer_simpson");
        user.setPassword("password123");
        user.setEmail("homer.simpson@example.com");
        user.setFolders(new ArrayList<>());
        user.setPreferredTags(new ArrayList<>());
        user.setPosts(new ArrayList<>());
        return user;
    }
    public  static Tag getDefaultTag1(){
        Tag tag = new Tag();
        tag.setName("tag1");
        tag.setPosts(new ArrayList<>());
        return tag;
    }
    public  static Tag getDefaultTag2(){
        Tag tag = new Tag();
        tag.setName("Tech");
        tag.setPosts(new ArrayList<>());
        return tag;
    }
    public  static Tag getDefaultTag3(){
        Tag tag = new Tag();
        tag.setName("Medicine");
        tag.setPosts(new ArrayList<>());
        return tag;
    }
    public static MultipartFile createMockMultipartFile() {
        String fileName = "test_image.jpg";
        String contentType = "image/jpeg";
        String content = "Mock image content";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return new MockMultipartFile(fileName, fileName, contentType, bytes);
    }
    public static Post getDefaultPost() {
        Post post = new Post();
        post.setTitle("test post");
        post.setDescription("test description");
        post.setTags(new ArrayList<>());
        return post;
    }
    public static ArrayList<Post> getListWithDefaultPosts(Integer numberOfElements){
        ArrayList<Post> posts = new ArrayList<>();
        for (int i = 0; i < numberOfElements; i++) {
            posts.add(getDefaultPost());
        }
        return posts;
    }
}
