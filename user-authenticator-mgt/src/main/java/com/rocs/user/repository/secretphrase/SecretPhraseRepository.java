package com.rocs.user.repository.secretphrase;

import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.secretphrase.SecretPhrase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretPhraseRepository extends JpaRepository<SecretPhrase, String> {
    SecretPhrase findByEmployee(Employee employee);
}
