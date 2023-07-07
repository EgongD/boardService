package security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import user.entity.User;
import user.repositoory.UserRepository;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        // User 이름 찾기
        User userEntity = userRepository.findByUserName(username);

        // 만약 User가 Null이 아니면 userEntity를 반환
        if (userEntity != null){
            return new PrincipalDetails(userEntity);
        }

        return null;
    }
}
