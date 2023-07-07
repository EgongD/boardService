package user.entity;

import board.entity.Board;
import com.fasterxml.jackson.annotation.JsonIgnore;
import comment.entity.Comment;
import global.audit.Auditable;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@Table(name = "User")
public class User extends Auditable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    private String role;

    private String provider;

    private String providerId;

    @Column(name = "activated")
    private boolean activated;

    @CreationTimestamp
    private Timestamp createDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Board> boardList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    public User(Long userId, String email, String password, String username, String role,
                String provider, String providerId, boolean activated, Timestamp createDate,
                List<String> roles, List<Board> boardList, List<Comment> commentList) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.activated = activated;
        this.createDate = createDate;
        this.roles = roles;
        this.boardList = boardList;
        this.commentList = commentList;
    }
}
