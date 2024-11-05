package com.rocs.user.domain.register;

import com.rocs.user.domain.employee.Employee;
import com.rocs.user.domain.user.User;
import lombok.Data;

@Data
public class Register {
    private User user;
    private Employee employee;
}
