package com.rocs.user.repository.user;

import com.rocs.user.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    User findUserByDeviceId(String deviceId);

    User findUserById(long Id);
}
