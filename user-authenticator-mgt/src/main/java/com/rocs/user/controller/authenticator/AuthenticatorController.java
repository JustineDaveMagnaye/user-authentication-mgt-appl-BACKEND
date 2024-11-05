package com.rocs.user.controller.authenticator;

import com.rocs.user.domain.authenticator.Authenticator;
import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.user.User;
import com.rocs.user.exception.domain.DateExistsException;
import com.rocs.user.service.authenticator.AuthenticatorService;
import com.rocs.user.service.employee.EmployeeService;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/authenticator")
public class AuthenticatorController {
    AuthenticatorService authenticatorService;

    public AuthenticatorController(AuthenticatorService authenticatorService) {
        this.authenticatorService = authenticatorService;
    }

    @PostMapping("/getAuthenticatorCode")
    public ResponseEntity<String> getAuthenticatorCode(@RequestBody Authenticator authenticator) throws DateExistsException {
        try{
            Authenticator authenticator1 = authenticatorService.getAuthenticatorCode(authenticator.getEmployee().getEmployeeNumber());
            return new ResponseEntity<>(authenticator1.getAuthenticatorCode(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
