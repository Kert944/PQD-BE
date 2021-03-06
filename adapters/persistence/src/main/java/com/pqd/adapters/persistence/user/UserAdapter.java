package com.pqd.adapters.persistence.user;

import com.pqd.application.domain.user.User;
import com.pqd.application.domain.user.UserId;
import com.pqd.application.usecase.user.UserGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Function;

@Component //TODO make persistence adapter component
@Transactional
@AllArgsConstructor
public class UserAdapter implements UserGateway {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(toUser());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(toUser());
    }

    @Override
    public void save(User user) {
        userRepository.save(UserEntity.builder()
                                      .firstName(user.getFirstName())
                                      .lastName(user.getLastName())
                                      .username(user.getUsername())
                                      .email(user.getEmail())
                                      .password(user.getPassword())
                                      .build());
    }

    private Function<UserEntity, User> toUser() {
        return user -> User.builder()
                           .userId(UserId.of(user.getId()))
                           .username(user.getUsername())
                           .firstName(user.getFirstName())
                           .lastName(user.getLastName())
                           .email(user.getEmail())
                           .password(user.getPassword())
                           .build();
    }
}
