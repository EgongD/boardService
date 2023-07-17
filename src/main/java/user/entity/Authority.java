package user.entity;

import org.springframework.data.annotation.Id;

import javax.persistence.Column;

public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;
}
