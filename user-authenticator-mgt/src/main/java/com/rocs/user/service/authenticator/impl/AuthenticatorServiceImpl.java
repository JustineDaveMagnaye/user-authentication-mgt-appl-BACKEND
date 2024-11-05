package com.rocs.user.service.authenticator.impl;

import com.rocs.user.domain.authenticator.Authenticator;
import com.rocs.user.service.authenticator.AuthenticatorService;
import com.rocs.user.domain.employee.Employee;
import com.rocs.user.exception.domain.DateExistsException;
import com.rocs.user.repository.authenticator.AuthenticatorRepository;
import com.rocs.user.repository.employee.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Service
public class AuthenticatorServiceImpl implements AuthenticatorService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private AuthenticatorRepository authenticatorRepository;
    private EmployeeRepository employeeRepository;

    public AuthenticatorServiceImpl(AuthenticatorRepository authenticatorRepository, EmployeeRepository employeeRepository) {
        this.authenticatorRepository = authenticatorRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Authenticator getAuthenticatorCode(String employeeNumber) throws DateExistsException {
        SecureRandom random = new SecureRandom();
        Authenticator findEmployeeNumber = this.authenticatorRepository.findByEmployee_EmployeeNumber(employeeNumber);
        Authenticator authenticator = new Authenticator();
        if(findEmployeeNumber != null){
            LocalDateTime dateTime = findEmployeeNumber.getCreatedAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = dateTime.toLocalDate().format(formatter);
            if(formattedDate.equals(LocalDate.now().toString())){
                LOGGER.info("Current Date Exists!");
                return findEmployeeNumber;
            } else {
                String randomCode = new BigInteger(30, random).toString(32).toUpperCase();
                while(authenticatorRepository.existsByAuthenticatorCode(randomCode)){
                    randomCode = new BigInteger(30, random).toString(32).toUpperCase();
                }
                findEmployeeNumber.setAuthenticatorCode(randomCode);
                findEmployeeNumber.setCreatedAt(new Date());
                authenticatorRepository.save(findEmployeeNumber);
                return findEmployeeNumber;
            }
        }
        String randomCode = new BigInteger(30, random).toString(32).toUpperCase();
        while(authenticatorRepository.existsByAuthenticatorCode(randomCode)){
            randomCode = new BigInteger(30, random).toString(32).toUpperCase();
        }

        Employee employee = this.employeeRepository.findByEmployeeNumber(employeeNumber);
        if(employee != null){
            authenticator.setDeviceId(employee.getUser().getDeviceId());
            authenticator.setEmployee(employee);
            authenticator.setAuthenticatorCode(randomCode);
            authenticator.setCreatedAt(new Date());
            authenticatorRepository.save(authenticator);
            return authenticator;
        } else {
            throw new  DateExistsException("Employee number does not exist!");
        }
    }

}
