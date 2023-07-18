package user.service;

import global.CustomAuthorityUtils;
import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import user.entity.User;
import user.repositoory.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CustomAuthorityUtils authorityUtils;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CustomAuthorityUtils authorityUtils){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
    }

    public User createMember(User user){

        // 이메일 중복 확인
        verifyExistEmail(user.getEmail());

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        // 회원의 권한 설정
        List<String> roles = authorityUtils.createRoles(user.getEmail());
        user.setRoles(roles);
        user.setProvider("default");

        // 회원 저장
        User savedUser = userRepository.save(user);

        // 저장된 회원 반환
        return savedUser;
    }

    public void createSocialUser(User user) {
        verifyExistEmail(user.getEmail());

        List<String> roles = authorityUtils.createRoles(user.getEmail());
        user.setRoles(roles);

        userRepository.save(user);
    }

    public User updateMember(User user) {

        User findUser = findExistedUser(user.getUserId());

        Optional.ofNullable(user.getUsername()).ifPresent(findUser::setUsername);

        return userRepository.save(findUser);
    }

    public User findUser(Long userId){

        User user = findExistedUser(userId);

        return user;
    }

    // 전체 사용자 조회
    public List<User> findUsers(){

        return userRepository.findAll();
    }

    public void deleteMember(Long userId){

        userRepository.delete(findExistedUser(userId));
    }

    private void verifyExistEmail(String email){

        Optional<User> optionalMember = userRepository.findByEmail(email);

        if(optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.USER_EXIST);
        }
    }

    private User findExistedUser(Long userId){

        Optional<User> optionalMember = userRepository.findById(userId);

        return optionalMember.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );
    }
}
