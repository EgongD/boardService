package member.repository;

import member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.sql.rowset.serial.SerialStruct;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
