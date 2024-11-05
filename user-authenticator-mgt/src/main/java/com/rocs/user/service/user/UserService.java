package com.rocs.user.service.user;


import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.user.User;
import com.rocs.user.exception.domain.*;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService {
    User register(String username, String password, String employeeNumber, String deviceId) throws UsernameNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, PersonExistsException, UserNotFoundException, UserExistsException, DeviceIdExistsException;

    //    User forgotPassword(User user) throws UsernameNotFoundException, MessagingException;

    //    User verifyOtpForgotPassword(User newUser) throws PersonExistsException, OtpExistsException;

    List<User> getUsers();

    User findUserByUsername(String username);

    boolean verifyOtp(String username, String otp);

    User resetDeviceId(long Id);

    User lock(long Id);

    User unlock(long Id);

    User reRegister(User user, String userDeviceId) throws DeviceIdExistsException;

}
