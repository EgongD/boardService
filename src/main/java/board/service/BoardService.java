package board.service;

import board.entity.Board;
import board.repository.BoardRepository;
import comment.service.CommentService;
import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import member.entity.Member;
import member.repositoory.MemberRepository;
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

        // 멤버아이디로 멤버 찾기
        Member member = memberService.findMember(memberId);
        // 멤버 세팅하기
        board.setMember(member);

        // 게시글 저장 후 반환
        return boardRepository.save(board);
    }

    public Board findBoard(Long boardId){

        // 게지글 ID로 게시글 찾기
        Board board = findExistedBoard(boardId);

        return board;
    }

    public Board updateBoard(Board board, Long boardId, Long memberId){

        // 게시글 ID를 통해 게시글을 찾기
        Board findBoard = findExistedBoard(board.getBoardId());

        if(findBoard.getMember().getMemberId() != memberId){
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
