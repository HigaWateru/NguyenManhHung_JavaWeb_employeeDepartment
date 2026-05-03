package demo.employee.service;

import demo.employee.model.Department;
import demo.employee.model.Employee;
import demo.employee.repository.DepartmentRepository;
import demo.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại"));

        List<Employee> employees = employeeRepository.findAll().stream().filter(e -> e.getDepartment() != null && e.getDepartment().getId().equals(id)).toList();

        if (!employees.isEmpty()) {
            for (Employee employee : employees) employee.setDepartment(null);
            employeeRepository.saveAll(employees);
            employeeRepository.flush();
        }

        departmentRepository.delete(department);
        departmentRepository.flush();
    }
}
