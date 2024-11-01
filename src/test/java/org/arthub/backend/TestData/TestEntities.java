package org.arthub.backend.TestData;

import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Entity.User;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TestEntities {
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
    public static MultipartFile createMockMultipartFile() throws IOException {
        String fileName = "test_image.jpg";
        String contentType = "image/jpeg";
        String content = "Mock image content";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return new MockMultipartFile(fileName, fileName, contentType, bytes);
    }
   // public final static Folder Folder;
}
