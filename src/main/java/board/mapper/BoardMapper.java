package board.mapper;

import board.dto.BoardDto;
import board.entity.Board;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")

public interface BoardMapper {

    Board boardPostToBoard(BoardDto.Post post);

    Board boardPatchToBoard(BoardDto.Patch patch);

    BoardDto.Response boardToBoardResponseDto(Board board);

    List<BoardDto.Response> boardsToBoardResponseDto(List<Board> boards);
}
