package board.service;

import board.entity.Board;
import board.repository.BoardRepository;
import comment.service.CommentService;
import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import user.entity.User;
import user.repositoory.UserRepository;
import user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private final BoardRepository boardRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final CommentService commentService;

    @Autowired
    private final UserRepository userRepository;

    public BoardService(BoardRepository boardRepository,
                        UserService userService,
                        CommentService commentService,
                        UserRepository userRepository){

        this.boardRepository = boardRepository;
        this.userService = userService;
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    public Board createBoard(Board board, Long userId){

        // 멤버아이디로 멤버 찾기
        User user = userService.findUser(userId);
        // 멤버 세팅하기
        board.setUser(user);

        // 게시글 저장 후 반환
        return boardRepository.save(board);
    }

    public Board findBoard(Long boardId){

        // 게지글 ID로 게시글 찾기
        Board board = findExistedBoard(boardId);

        return board;
    }

    public Board updateBoard(Board board, Long boardId, Long userId){

        // 게시글 ID를 통해 게시글을 찾기
        Board findBoard = findExistedBoard(board.getBoardId());

        if(findBoard.getUser().getUserId() != userId){
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }

        // update 제목, 내용, 해시태그
        findBoard.setTitle(board.getTitle());
        findBoard.setContent(board.getContent());
        findBoard.setHashtag(board.getHashtag());

        // 게시글 저장하고 반환
        return boardRepository.save(findBoard);
    }

    public void deleteBoard(Long boardId){

        Board findBoard = findExistedBoard(boardId);
        commentService.deleteCommentsByBoardId(boardId);
        boardRepository.delete(findBoard);
    }

    private Board findExistedBoard(Long boardId){

        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        return optionalBoard.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
    }
}
