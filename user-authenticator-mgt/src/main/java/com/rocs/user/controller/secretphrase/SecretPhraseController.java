package com.rocs.user.controller.secretphrase;

import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.secretphrase.SecretPhrase;
import com.rocs.user.service.employee.EmployeeService;
import com.rocs.user.service.secretphrase.SecretPhraseService;
import com.rocs.user.service.secretphrase.impl.SecretPhraseServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/secret-phrase")
public class SecretPhraseController {
    private SecretPhraseService secretPhraseService;
    private EmployeeService employeeService;

    public SecretPhraseController(SecretPhraseService secretPhraseService, EmployeeService employeeService) {
        this.secretPhraseService = secretPhraseService;
        this.employeeService = employeeService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<SecretPhrase>> getAllEmployee() {
        return new ResponseEntity<>(secretPhraseService.getAllSecretPhrase(), HttpStatus.OK);
    }
    @PostMapping("/generate-secret-phrase")
    public ResponseEntity<String> verifyOtp(@RequestBody SecretPhrase secretPhrase) {
        try {
            String employeeNumber = secretPhrase.getEmployee().getEmployeeNumber();

            Employee employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber);

            if(employee != null){
                if(employee.getSecretPhrase() == null){
                    SecretPhrase generateSecretPhrase = this.secretPhraseService.generateSecretPhrase(employeeNumber);
                    if(generateSecretPhrase != null){
                        return new ResponseEntity<>("Generated Successfully!", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Generated Unsuccessfully!", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Employee already has a secret phrase!", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Employee Number does not exist!", HttpStatus.INTERNAL_SERVER_ERROR);

            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
