package board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import comment.entity.Comment;
import global.audit.Auditable;
import lombok.*;
import member.entity.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BOARD")
public class Board extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String hashtag;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @JsonIgnore
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();
}