package com.rocs.user.service.employee;

import com.rocs.user.domain.employee.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployee();

    Employee getEmployeeByEmployeeNumber(String employeeNumber);

    Employee getEmployeeByLoginId(long id);
}
