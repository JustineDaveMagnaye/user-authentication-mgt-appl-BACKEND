package com.rocs.user.repository.authenticator;

import com.rocs.user.domain.authenticator.Authenticator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticatorRepository extends JpaRepository<Authenticator, String> {
    boolean existsByAuthenticatorCode(String authenticatorCode);

    Authenticator findByEmployee_EmployeeNumber(String employeeNumber);
}
