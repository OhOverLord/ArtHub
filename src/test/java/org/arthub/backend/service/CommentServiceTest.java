package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Comment;
import org.arthub.backend.entity.Post;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.repository.CommentRepository;
import org.arthub.backend.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testCreateSuccessful() throws NotFound {
        Post post = new Post(1L, "full", "full", null,  new ArrayList<>(), null, null, null);
        String postCommentText = "Stop posting shrek photos";
        String authorName = "john_doe";
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(null);
        when(postRepository.save(post)).thenReturn(null);

        Comment createdComment = commentService.create(1L, postCommentText, authorName);
        assertEquals(postCommentText, createdComment.getText());
        assertEquals(authorName, createdComment.getAuthorUsername());
    }

    @Test
    void testCreateFailure(){
        String postCommentText = "Stop posting shrek photos";
        String authorName = "john_doe";
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFound.class, () -> commentService.create(1L, postCommentText, authorName));
    }

    @Test
    void testDeleteById(){
        doNothing().when(commentRepository).deleteById(1L);
        commentService.deleteById(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetPostByIdSuccessful() throws NotFound{
        Post post = new Post(1L, "full", "full", null,  new ArrayList<>(), null, null, null);
        post.getComments().add(TestEntities.COMMENT1);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        List<Comment> comments = commentService.getByPostId(1L);
        assertEquals(1, comments.size());
        assertEquals(TestEntities.COMMENT1, comments.get(0));
    }

    @Test
    void testGetPostByIdFailure(){
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFound.class, () -> commentService.getByPostId(1L));
    }

    @Test
    void testGetByIdSuccessful()throws NotFound{
        Comment comment = TestEntities.COMMENT1;
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        Comment found = commentService.getById(comment.getId());
        assertEquals(comment, found);
    }

    @Test
    void testGetByIdFailure(){
        when(commentRepository.findById(TestEntities.COMMENT1.getId())).thenReturn(Optional.empty());
        assertThrows(NotFound.class, () -> commentService.getById(TestEntities.COMMENT1.getId()));
    }

    @Test
    void testGetAll(){
        List<Comment> list = new ArrayList<>(List.of(TestEntities.COMMENT1, TestEntities.COMMENT2));
        when(commentRepository.findAll()).thenReturn(list);

        List<Comment> found = commentService.getAll();
        assertEquals(list.size(), found.size());
    }
}
