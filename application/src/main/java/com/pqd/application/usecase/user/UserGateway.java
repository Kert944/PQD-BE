package com.pqd.application.usecase.user;

import com.pqd.application.domain.user.User;

import java.util.Optional;

public interface UserGateway {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void save(User user);
}
