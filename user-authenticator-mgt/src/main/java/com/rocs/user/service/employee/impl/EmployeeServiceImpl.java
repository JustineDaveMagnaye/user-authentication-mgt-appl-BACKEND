package com.rocs.user.service.employee.impl;

import com.rocs.user.domain.employee.Employee;
import com.rocs.user.repository.employee.EmployeeRepository;
import com.rocs.user.service.employee.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeByEmployeeNumber(String employeeNumber) {
        return employeeRepository.findByEmployeeNumber(employeeNumber);
    }

    @Override
    public Employee getEmployeeByLoginId(long id){
        return employeeRepository.findByUser_Id(id);
    }
}
