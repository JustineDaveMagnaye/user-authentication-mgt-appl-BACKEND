package com.rocs.user.service.secretphrase;

import com.rocs.user.domain.secretphrase.SecretPhrase;
import com.rocs.user.exception.domain.DateExistsException;
import com.rocs.user.exception.domain.EmployeeNotFoundException;

import java.util.List;

public interface SecretPhraseService {
    List<SecretPhrase> getAllSecretPhrase();

    SecretPhrase generateSecretPhrase(String employeeNumber) throws DateExistsException, EmployeeNotFoundException;
}
