package comment.service;

import board.entity.Board;
import board.repository.BoardRepository;
import board.service.BoardService;
import comment.entity.Comment;
import comment.mapper.CommentMapper;
import comment.repository.CommentRepository;
import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import user.entity.User;
import user.repositoory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final CommentMapper commentMapper;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BoardRepository boardRepository;

    @Autowired
    private final BoardService boardService;

    public CommentService(CommentRepository commentRepository,
                          CommentMapper commentMapper,
                          UserRepository userRepository,
                          BoardRepository boardRepository,
                          BoardService boardService){

        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.boardService = boardService;
    }

    public Comment createComment(Comment comment, Long boardId){

        Board board = boardService.findBoard(boardId);
        comment.setBoard(board);

        User user = userRepository.findById(comment.getUser().getUserId()).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        comment.setUser(user);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment, Long commentId){

        Comment findComment = findExistedComment(commentId);
        findComment.setContent(comment.getContent());

        User user = findComment.getUser();
        if(!user.getUserId().equals(user.getUserId())){
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }

        return commentRepository.save(findComment);
    }

    public void deleteComment(Long commentId){

        Comment findComment = findExistedComment(commentId);
        commentRepository.delete(findComment);
    }

    private Comment findExistedComment(Long commentId){

        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        return optionalComment.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }

    public void deleteCommentsByBoardId(Long boardId){

        commentRepository.deleteByBoardId(boardId);
    }
}
