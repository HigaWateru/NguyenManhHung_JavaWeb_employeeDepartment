package demo.employee.runner;

import demo.employee.model.Department;
import demo.employee.model.Employee;
import demo.employee.repository.DepartmentRepository;
import demo.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        if (departmentRepository.count() == 0 && employeeRepository.count() == 0) {
            Department hr = Department.builder().name("Resources").location("Hanoi").build();
            Department tech = Department.builder().name("Technology").location("Ho Chi Minh City").build();

            departmentRepository.saveAll(Arrays.asList(hr, tech));

            Employee emp1 = Employee.builder().name("Nguyen Van A").age(25).avatar("https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg").status(true).department(hr).build();
            Employee emp2 = Employee.builder().name("Tran Thi B").age(28).avatar("https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg").status(true).department(tech).build();
            Employee emp3 = Employee.builder().name("Le Van C").age(30).avatar("https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg").status(false).department(tech).build();
            Employee emp4 = Employee.builder().name("Pham Van D").age(22).avatar("https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg").status(true).department(hr).build();
            Employee emp5 = Employee.builder().name("Hoang Thi E").age(27).avatar("https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg").status(true).department(tech).build();
            Employee emp6 = Employee.builder().name("Vu Van F").age(35).avatar("https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg").status(false).department(hr).build();
            Employee emp7 = Employee.builder().name("Dang Thi G").age(24).avatar("https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg").status(true).department(tech).build();

            employeeRepository.saveAll(Arrays.asList(emp1, emp2, emp3, emp4, emp5, emp6, emp7));
            System.out.println("Data seeded successfully!");
        } else {
            System.out.println("Data already exists, skipping seeding.");
        }
    }
}
