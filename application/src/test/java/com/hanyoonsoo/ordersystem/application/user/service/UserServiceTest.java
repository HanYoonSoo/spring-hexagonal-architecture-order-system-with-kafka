package com.hanyoonsoo.ordersystem.application.user.service;

import com.hanyoonsoo.ordersystem.application.support.fixture.UserFixture;
import com.hanyoonsoo.ordersystem.application.user.dto.SignUpCommand;
import com.hanyoonsoo.ordersystem.application.user.dto.UserDetailResult;
import com.hanyoonsoo.ordersystem.application.user.port.out.UserRepository;
import com.hanyoonsoo.ordersystem.common.exception.base.ConflictException;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void 회원가입시_인증정보가_이미_존재하면_예외가_발생한다() {
        // given
        SignUpCommand command = UserFixture.회원가입명령();
        given(userRepository.existsByLoginId("test@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(command))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void 회원가입에_성공하면_유저_권한과_인증정보를_저장한다() {
        // given
        SignUpCommand command = UserFixture.회원가입명령();
        given(userRepository.existsByLoginId("test@example.com")).willReturn(false);
        given(passwordEncoder.encode("test@example.compassword12")).willReturn("encoded-password");

        // when
        userService.signUp(command);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userCaptor.capture());
        then(userRepository).should().saveUserRole(any(UserRole.class));
        ArgumentCaptor<UserCredential> credentialCaptor = ArgumentCaptor.forClass(UserCredential.class);
        then(userRepository).should().saveUserCredential(credentialCaptor.capture());
        assertThat(userCaptor.getValue().getName()).isEqualTo("tester");
        assertThat(credentialCaptor.getValue().getValue()).isEqualTo("encoded-password");
    }

    @Test
    void 내_정보를_조회하면_사용자_상세_결과를_반환한다() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.from("tester");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.findRolesByUserId(userId)).willReturn(List.of(Role.USER));

        // when
        UserDetailResult result = userService.getMyInfo(userId);

        // then
        assertThat(result.name()).isEqualTo("tester");
        assertThat(result.roles()).containsExactly(Role.USER);
    }

    @Test
    void 내_정보_조회시_사용자가_없으면_예외가_발생한다() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getMyInfo(userId))
                .isInstanceOf(NotFoundException.class);
    }
}
