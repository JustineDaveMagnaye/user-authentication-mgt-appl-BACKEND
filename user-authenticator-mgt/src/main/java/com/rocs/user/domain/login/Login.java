package com.rocs.user.domain.login;

import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.user.User;
import lombok.Data;

@Data
public class Login {
    private Employee employee;
    private User user;
}
