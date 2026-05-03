package demo.employee.service;

import demo.employee.model.Department;
import demo.employee.model.Employee;
import demo.employee.repository.DepartmentRepository;
import demo.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Long departmentId;

    @BeforeEach
    void setUp() {
        Department dept = Department.builder().name("Test Dept").location("Test Loc").build();
        dept = departmentRepository.save(dept);
        departmentId = dept.getId();

        Employee emp1 = Employee.builder().name("Emp 1").age(25).department(dept).build();
        Employee emp2 = Employee.builder().name("Emp 2").age(30).department(dept).build();
        employeeRepository.saveAll(List.of(emp1, emp2));
    }

    @Test
    void testDeleteDepartmentUpdatesEmployees() {
        // Given: A department with 2 employees (setup in @BeforeEach)
        
        // When: Delete the department
        departmentService.deleteDepartment(departmentId);
        
        // Then: Department should be deleted
        assertFalse(departmentRepository.existsById(departmentId));
        
        // And: Employees should still exist but department_id should be null
        List<Employee> employees = employeeRepository.findAll();
        List<Employee> testDeptEmployees = employees.stream()
                .filter(e -> "Emp 1".equals(e.getName()) || "Emp 2".equals(e.getName()))
                .toList();
        
        assertEquals(2, testDeptEmployees.size());
        for (Employee emp : testDeptEmployees) {
            assertNull(emp.getDepartment(), "Employee " + emp.getName() + " should have null department");
        }
    }
}
