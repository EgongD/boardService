package comment.controller;

import comment.dto.CommentDto;
import comment.entity.Comment;
import comment.mapper.CommentMapper;
import comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    private final CommentMapper commentMapper;

    public CommentController(CommentService commentService,
                             CommentMapper commentMapper){

        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @PostMapping
    public ResponseEntity<?> postComment(@RequestBody CommentDto.Post post){

        Comment comment = commentService.createComment(
                commentMapper.commentPostToComment(post), post.getBoardId());

        return new ResponseEntity<>((commentMapper.commentToCommentResponseDto(comment)), HttpStatus.OK);
    }

    @PatchMapping("/{comment-id}")
    public ResponseEntity<?> patchComment(@RequestBody CommentDto.Patch patch, @PathVariable("comment-id") Long commentId){

        Comment comment = commentService.updateComment(commentMapper.commentPatchToComment(patch), commentId);

        return new ResponseEntity<>(commentMapper.commentToCommentResponseDto(comment), HttpStatus.OK);
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity<?> deleteComment(@PathVariable("comment-id") @Positive Long commentId){

        commentService.deleteComment(commentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
