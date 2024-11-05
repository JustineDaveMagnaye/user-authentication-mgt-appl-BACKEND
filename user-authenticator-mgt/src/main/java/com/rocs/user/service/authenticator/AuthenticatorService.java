package com.rocs.user.service.authenticator;

import com.rocs.user.domain.authenticator.Authenticator;
import com.rocs.user.exception.domain.DateExistsException;

public interface AuthenticatorService {
    Authenticator getAuthenticatorCode(String employeeNumber) throws DateExistsException;
}
