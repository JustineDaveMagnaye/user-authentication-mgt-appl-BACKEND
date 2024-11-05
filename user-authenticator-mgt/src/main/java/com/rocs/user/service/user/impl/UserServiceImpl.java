package com.rocs.user.service.user.impl;



import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.user.User;
import com.rocs.user.domain.user.principal.UserPrincipal;
import com.rocs.user.exception.domain.*;
import com.rocs.user.repository.employee.EmployeeRepository;
import com.rocs.user.repository.user.UserRepository;
import com.rocs.user.service.email.EmailService;
import com.rocs.user.service.login.attempt.LoginAttemptService;
import com.rocs.user.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.rocs.user.utils.security.enumeration.Role.*;


@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           EmployeeRepository employeeRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException   {
        User user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error("Username not found...");
            throw new UsernameNotFoundException("Username not found.");
        }
        validateLoginAttempt(user);
        user.setLastLoginDate(new Date());
        this.userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        LOGGER.info("User information found...");
        return userPrincipal;
    }
    private void validateLoginAttempt(User user) {
        if(!user.isLocked()) {
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setLocked(true);
            } else {
                user.setLocked(false);
            }
        } else {
                loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
    @Override
    public User register(String username, String password, String employeeNumber, String deviceId) throws UsernameNotFoundException, UsernameExistsException, MessagingException, PersonExistsException, UserNotFoundException, UserExistsException, DeviceIdExistsException {
        validateNewUsername(username);
        validatePassword(password);
        validateDeviceId(deviceId);
        String otp = generateOTP();

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setJoinDate(new Date());
        user.setActive(true);
        user.setLocked(true);
        user.setOtp(otp);
        user.setRole(ROLE_EMPLOYEE.name());
        user.setAuthorities(Arrays.stream(ROLE_EMPLOYEE.getAuthorities()).toList());
        user.setDeviceId(deviceId);

        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber);
        if(employee != null && employee.getUser() != null){
            LOGGER.info("User is already registered!");
            throw new UserExistsException("This user is already registered!");
        } else if(employee != null){
        emailService.sendNewPasswordEmail(employee.getEmail(),otp);
        userRepository.save(user);
        employee.setUser(user);
        employeeRepository.save(employee);
            LOGGER.info("User registered successfully!");
        } else {
            LOGGER.info("Employee Number does not exist!");
            throw new UserExistsException("Employee Number does not exist!");
        }

        return new User();
    }

//    @Override
//    public User forgotPassword(User newUser) throws UsernameNotFoundException, MessagingException {
//
//        return newUser;
//    }
//    @Override
//    public User verifyOtpForgotPassword(User newUser) throws UsernameNotFoundException, PersonExistsException, OtpExistsException {
//        return newUser;
//    }
    @Override
    public boolean verifyOtp(String username, String otp) {
        User user = userRepository.findUserByUsername(username);
        if(user == null || user.getOtp() == null){
            return false;
        }
        if (user.getOtp().equals(otp)) {
            user.setLocked(false);
            user.setOtp(null);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    private void validateNewUsername(String newUsername)
            throws UserNotFoundException, UsernameExistsException, PersonExistsException {
        User userByNewUsername = findUserByUsername(newUsername);
        if (StringUtils.isNotBlank(StringUtils.EMPTY)) {
            User currentUser = findUserByUsername(StringUtils.EMPTY);
            if (currentUser == null) {
                throw new UserNotFoundException("User not found.");
            }
            if (userByNewUsername != null && !userByNewUsername.getId().equals(currentUser.getId())) {
                throw new PersonExistsException("Username already exists.");
            }
        } else {
            if (userByNewUsername != null) {
                throw new PersonExistsException("Username already exists.");
            }
        }
    }

    @Override
    public User resetDeviceId(long Id){
        User findId = userRepository.findUserById(Id);
        if(findId != null){
            findId.setDeviceId(null);
            userRepository.save(findId);
        }
        return null;
    }

    @Override
    public User lock(long Id){
        User findId = userRepository.findUserById(Id);
        if(findId != null){
            findId.setLocked(true);
            userRepository.save(findId);
        }
        return null;
    }

    @Override
    public User unlock(long Id){
        User findId = userRepository.findUserById(Id);
        if(findId != null){
            findId.setLocked(false);
            userRepository.save(findId);
        }
        return null;
    }

    @Override
    public User reRegister(User user, String userDeviceId) throws DeviceIdExistsException {
        validateDeviceId(userDeviceId);
        user.setDeviceId(userDeviceId);
        userRepository.save(user);
        return null;
    }

    private void validatePassword(String password) throws PersonExistsException {
        String passwordPattern = ".*[^a-zA-Z0-9].*";
        if (!password.matches(passwordPattern)) {
            throw new PersonExistsException("Please create a stronger password. Password should contain special characters.");
        }
    }
    private void validateDeviceId(String deviceId) throws DeviceIdExistsException{
    User duplicateId = this.userRepository.findUserByDeviceId(deviceId);
    if(duplicateId != null){
            throw new DeviceIdExistsException("Device already registered to an account.");
    }
    }
    private String generateOTP() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    @Override
    public User findUserByUsername(String username) {
        return this.userRepository.findUserByUsername(username);
    }

}

