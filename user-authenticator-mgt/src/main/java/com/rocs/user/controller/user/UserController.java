package com.rocs.user.controller.user;

import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.register.Register;
import com.rocs.user.domain.user.User;
import com.rocs.user.domain.user.principal.UserPrincipal;
import com.rocs.user.service.employee.EmployeeService;
import com.rocs.user.service.user.UserService;
import com.rocs.user.utils.security.jwt.provider.token.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.rocs.user.utils.security.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final EmployeeService employeeService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, EmployeeService employeeService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register register) {
        try {
            String username = register.getUser().getUsername();
            String password = register.getUser().getPassword();
            String deviceId = register.getUser().getDeviceId();
            String employeeNumber = register.getEmployee().getEmployeeNumber();

            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }
            // Handle registration logic with user and employeeNumber
            User newUser = userService.register(username, password, employeeNumber, deviceId);
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody User user) {
//        try {
//            User newUser = userService.forgotPassword(user);
//            return new ResponseEntity<>(newUser, HttpStatus.OK);
//        } catch (Exception e) {
//            return handleException(e);
//        }
//    }
//
//    @PostMapping("/verify-forgot-password")
//    public ResponseEntity<?> verifyForgotPassword(@RequestBody User user) {
//        try {
//            User newUser = userService.verifyOtpForgotPassword(user);
//            return new ResponseEntity<>(newUser, HttpStatus.OK);
//        } catch (Exception e) {
//            return handleException(e);
//        }
//    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String otp = request.get("otp");

            if (username == null || otp == null) {
                return new ResponseEntity<>("Both username and otp are required", HttpStatus.BAD_REQUEST);
            }
            boolean isVerified = userService.verifyOtp(username, otp);
            if (isVerified) {
                return new ResponseEntity<>("Account unlocked successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid OTP", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }
    @PostMapping("/login-admin")
    public ResponseEntity<String> loginAdmin(@RequestBody User user) {
        try {
            User loginUser = userService.findUserByUsername(user.getUsername());
            if(loginUser == null){
                return new ResponseEntity<>("Username not found!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(!BCrypt.checkpw(user.getPassword(),loginUser.getPassword())){
                return new ResponseEntity<>("Incorrect Password!", HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                if(!loginUser.getRole().equals("ROLE_ADMIN")){
                return new ResponseEntity<>("You don't have enough permission to be here!", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                authenticate(user.getUsername(), user.getPassword());
                UserPrincipal userPrincipal = new UserPrincipal(loginUser);
                HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);
                Employee employee = this.employeeService.getEmployeeByLoginId(loginUser.getId());
                return new ResponseEntity<>( employee.getEmployeeNumber(), jwtHeaders, HttpStatus.OK);

            }
        } catch (Exception e) {
            return handleException(e);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            User loginUser = userService.findUserByUsername(user.getUsername());
            if(loginUser == null){
                return new ResponseEntity<>("Username not found!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(!BCrypt.checkpw(user.getPassword(),loginUser.getPassword())){
                return new ResponseEntity<>("Incorrect Password!", HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                if(loginUser.getRole().equals("ROLE_ADMIN")){
                    return new ResponseEntity<>("You don't have permission to use this account!", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (loginUser.getDeviceId() == null){
                    User setDeviceId = userService.reRegister(loginUser,user.getDeviceId());
                    return new ResponseEntity<>("User Successfully Re-registered!", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (loginUser.getDeviceId() != null && !loginUser.getDeviceId().equals(user.getDeviceId())) {
                    return new ResponseEntity<>("Device ID mismatch. Access denied.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if(loginUser.getOtp() != null){
                    return new ResponseEntity<>("2FA is required!", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                authenticate(user.getUsername(), user.getPassword());
                UserPrincipal userPrincipal = new UserPrincipal(loginUser);
                HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);
                Employee employee = this.employeeService.getEmployeeByLoginId(loginUser.getId());
                return new ResponseEntity<>( employee.getEmployeeNumber(), jwtHeaders, HttpStatus.OK);

            }
        } catch (Exception e) {
            return handleException(e);
        }
    }
    @GetMapping("/list")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/reset-device-id")
    public ResponseEntity<?> resetDeviceId(@RequestBody User user) {
        try {
            long Id = user.getId();
            User reset = userService.resetDeviceId(Id);
            return new ResponseEntity<>("Device Id successfully unregistered!", HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/lock")
    public ResponseEntity<?> lock(@RequestBody User user) {
        try {
            long Id = user.getId();
            User lock = userService.lock(Id);
            return new ResponseEntity<>("User Account successfully locked!", HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlock(@RequestBody User user) {
        try {
            long Id = user.getId();
            User unlock = userService.unlock(Id);
            return new ResponseEntity<>("User Account successfully unlocked!", HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private void authenticate(String username, String password) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
