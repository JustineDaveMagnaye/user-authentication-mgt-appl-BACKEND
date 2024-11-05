package com.rocs.user.service.secretphrase.impl;

import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.secretphrase.SecretPhrase;
import com.rocs.user.exception.domain.EmployeeNotFoundException;
import com.rocs.user.repository.employee.EmployeeRepository;
import com.rocs.user.repository.secretphrase.SecretPhraseRepository;
import com.rocs.user.service.employee.EmployeeService;
import com.rocs.user.service.secretphrase.SecretPhraseService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;


@Service
public class SecretPhraseServiceImpl implements SecretPhraseService {
    private SecretPhraseRepository secretPhraseRepository;
    private EmployeeRepository employeeRepository;

    public SecretPhraseServiceImpl(SecretPhraseRepository secretPhraseRepository, EmployeeRepository employeeRepository) {
        this.secretPhraseRepository = secretPhraseRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<SecretPhrase> getAllSecretPhrase() {
        return secretPhraseRepository.findAll();
    }

    @Override
    public SecretPhrase generateSecretPhrase(String employeeNumber) throws EmployeeNotFoundException {
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber);
        if(employee != null){
            SecretPhrase secretPhrase = this.secretPhraseRepository.findByEmployee(employee);
            if(secretPhrase == null){
                SecretPhrase newSecretPhrase = new SecretPhrase();
                newSecretPhrase.setSecretPhrase(getSecretPhrase());
                newSecretPhrase.setEmployee(employee);
                secretPhraseRepository.save(newSecretPhrase);
                employee.setSecretPhrase(newSecretPhrase);
                employeeRepository.save(employee);
                return new SecretPhrase();
            } else {
                throw new EmployeeNotFoundException("Employee already has a secret phrase!");
            }
        }
        throw new EmployeeNotFoundException("Employee Number does not exist!");
    }
    public String getSecretPhrase(){
        String[] WORDS = {
                "John", "Exodus", "Genesis", "Psalm", "leviticus", "Numbers", "Deuteronomy", "Joshua", "Judges", "Ruth", "Samuel", "Kings", "Chronicles", "Ezra", "Nehemiah", "Tobit", "Judith", "Esther", "Maccabees", "Job", "Proverbs", "Ecclesiastes", "Song of Songs", "Wisdom", "Sirach", "Isaiah", "Jeremiah", "Lamentation", ""
        };
        int WORD_COUNT = 2;
        String DIGITS = "0123456789";
        int NUMBER_LENGTH = 4;

            SecureRandom random = new SecureRandom();
            StringBuilder sb = new StringBuilder();

            // Add random words
            for (int i = 0; i < WORD_COUNT; i++) {
                int randomIndex = random.nextInt(WORDS.length);
                sb.append(WORDS[randomIndex]);
                if (i < WORD_COUNT - 1) {
                    sb.append("-");
                }
            }

            // Add random number
            sb.append("-");
            for (int i = 0; i < NUMBER_LENGTH; i++) {
                int randomIndex = random.nextInt(DIGITS.length());
                sb.append(DIGITS.charAt(randomIndex));
            }

            return sb.toString();
        }
}
