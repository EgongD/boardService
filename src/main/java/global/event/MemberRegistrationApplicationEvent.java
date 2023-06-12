package global.event;

import member.entity.Member;
import org.springframework.context.ApplicationEvent;

public class MemberRegistrationApplicationEvent extends ApplicationEvent {

    private Member member;

    public MemberRegistrationApplicationEvent(Object source, Member member) {
        super(source);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
