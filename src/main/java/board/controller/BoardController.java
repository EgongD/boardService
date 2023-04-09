package board.controller;

import board.dto.BoardDto;
import board.entity.Board;
import board.mapper.BoardMapper;
import board.repository.BoardRepository;
import board.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/posts")
public class BoardController {

    private final BoardService boardService;

    private final BoardRepository boardRepository;

    private final BoardMapper boardMapper;

    public BoardController(BoardService boardService,
                           BoardRepository boardRepository,
                           BoardMapper boardMapper){

        this.boardService = boardService;
        this.boardRepository = boardRepository;
        this.boardMapper = boardMapper;
    }

    @PostMapping
    public ResponseEntity<?> postBoard(@RequestBody BoardDto.Post post){

        Board board = boardService.createBoard(boardMapper.boardPostToBoard(post), post.getMemberId());

        return new ResponseEntity<>(
                (boardMapper.boardToBoardResponseDto(board)), HttpStatus.OK);
    }

    @PatchMapping("/{board-id}")
    public ResponseEntity<?> patchBoard(@RequestBody BoardDto.Patch patch, @PathVariable("board-id") Long boardId, Long memberId) {

        Board board = boardService.updateBoard(boardMapper.boardPatchToBoard(patch), boardId, memberId);
        BoardDto.Response boardResponse = boardMapper.boardToBoardResponseDto(board);

        return ResponseEntity.ok(boardResponse);
    }


    @GetMapping("/{board-id}")
    public ResponseEntity<?> getBoard(@PathVariable("board-id") @Positive Long boardId) {

        Board board = boardService.findBoard(boardId);

        BoardDto.Response boardResponse = boardMapper.boardToBoardResponseDto(board);

        return new ResponseEntity(boardResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{board-id}")
    public ResponseEntity<?> deleteBoard(@PathVariable("board-id") @Positive Long boardId){

        boardService.deleteBoard(boardId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
