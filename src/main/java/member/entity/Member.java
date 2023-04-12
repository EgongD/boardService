package member.entity;

import board.entity.Board;
import com.fasterxml.jackson.annotation.JsonIgnore;
import comment.entity.Comment;
import global.audit.Auditable;
import global.auth.Authority;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MEMBER")
public class Member extends Auditable {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "activated")
    private boolean activated;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Board> boardList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "member_authority", joinColumns = {@JoinColumn(name = "member_id",
    referencedColumnName = "member")}, inverseJoinColumns = {@JoinColumn(name = "authority_name",
    referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    private Long tokenWeight;

    @Builder
    public Member(String email, String password, String nickname,
                  Set<Authority> authorities, boolean activated){

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.authorities = authorities;
        this.activated = activated;
        this.tokenWeight = 1L;
    }
}
