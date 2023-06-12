package comment.mapper;

import comment.dto.CommentDto;
import comment.entity.Comment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment commentPostToComment(CommentDto.Post post);

    Comment commentPatchToComment(CommentDto.Patch patch);

    CommentDto.Response commentToCommentResponseDto(Comment comment);

    List<CommentDto.Response> commentsToCommentResponseDto(List<Comment> comments);
}
