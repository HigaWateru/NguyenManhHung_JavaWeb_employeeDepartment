package demo.employee.controller;

import demo.employee.dto.EmployeeDto;
import demo.employee.model.Department;
import demo.employee.model.Employee;
import demo.employee.repository.DepartmentRepository;
import demo.employee.repository.EmployeeRepository;
import demo.employee.service.upload.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UploadService uploadService;

    @ModelAttribute("departments")
    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employeeDto", new EmployeeDto());
        return "form";
    }

    @PostMapping("/add")
    public String addEmployee(@Valid @ModelAttribute("employeeDto") EmployeeDto employeeDto, BindingResult result, @RequestParam("file") MultipartFile file, Model model) {
        if (result.hasErrors()) return "form";

        String avatarUrl = "https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg"; // Default
        if (file != null && !file.isEmpty()) {
            avatarUrl = uploadService.upload(file);
        }

        Department department = departmentRepository.findById(employeeDto.getDepartmentId()).orElseThrow(() -> new RuntimeException("Department not found"));

        Employee employee = Employee.builder().name(employeeDto.getName()).age(employeeDto.getAge()).status(employeeDto.getStatus()).avatar(avatarUrl).department(department).build();

        employeeRepository.save(employee);
        return "redirect:/employees";
    }
}
