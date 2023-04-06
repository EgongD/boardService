package board.service;

import board.entity.Board;
import board.repository.BoardRepository;
import comment.service.CommentService;
import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import member.entity.Member;
import member.repository.MemberRepository;
import member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private final BoardRepository boardRepository;

    @Autowired
    private final MemberService memberService;

    @Autowired
    private final CommentService commentService;

    @Autowired
    private final MemberRepository memberRepository;

    public BoardService(BoardRepository boardRepository,
                        MemberService memberService,
                        CommentService commentService,
                        MemberRepository memberRepository){

        this.boardRepository = boardRepository;
        this.memberService = memberService;
        this.commentService = commentService;
        this.memberRepository = memberRepository;
    }

    public Board createBoard(Board board, Long memberId){

        Member member = memberService.findMember(memberId);
        board.setMember(member);

        return boardRepository.save(board);
    }

    public Board findBoard(Long boardId){

        Board board = findExistedBoard(boardId);

        return board;
    }

    public Board updateBoard(Board board, Long boardId){

        Board findBoard = findExistedBoard(board.getBoardId());

        return findBoard;
    }

    public void deleteBoard(Long boardId){

        Board findBoard = findExistedBoard(boardId);
        boardRepository.delete(findBoard);
    }



    private Board findExistedBoard(Long boardId){

        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        return optionalBoard.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
    }
}
