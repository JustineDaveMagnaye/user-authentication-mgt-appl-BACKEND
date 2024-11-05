package com.rocs.user.repository.employee;

import com.rocs.user.domain.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    boolean existsByEmployeeNumberAndEmail(String employeeNumber, String email);
    Employee findByEmail(String email);

    Employee findByEmployeeNumber(String employeeNumber);

    Employee findByUser_Id(Long user_id);

}
