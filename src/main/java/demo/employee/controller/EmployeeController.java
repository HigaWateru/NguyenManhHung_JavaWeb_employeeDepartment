package demo.employee.controller;

import demo.employee.dto.EmployeeDto;
import demo.employee.model.Department;
import demo.employee.model.Employee;
import demo.employee.repository.DepartmentRepository;
import demo.employee.repository.EmployeeRepository;
import demo.employee.service.upload.UploadService;
import demo.employee.service.DepartmentService;
import demo.employee.specification.EmployeeSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UploadService uploadService;
    private final DepartmentService departmentService;

    @ModelAttribute("departments")
    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    @GetMapping
    public String listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            Model model) {

        if (page < 0) page = 0;

        Sort sortOrder = direction.equalsIgnoreCase("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Specification<Employee> spec = Specification.where(EmployeeSpecification.hasName(keyword)).and(EmployeeSpecification.hasDepartment(departmentId)).and(EmployeeSpecification.hasAgeBetween(minAge, maxAge));

        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);

        if (page >= employeePage.getTotalPages() && employeePage.getTotalPages() > 0) {
            return "redirect:/employees?page=" + (employeePage.getTotalPages() - 1) + 
                   "&size=" + size + "&sort=" + sort + "&direction=" + direction + 
                   "&keyword=" + (keyword != null ? keyword : "") + 
                   "&departmentId=" + (departmentId != null ? departmentId : "") + 
                   "&minAge=" + (minAge != null ? minAge : "") + 
                   "&maxAge=" + (maxAge != null ? maxAge : "");
        }

        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("employeePage", employeePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("totalItems", employeePage.getTotalElements());
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("keyword", keyword);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("minAge", minAge);
        model.addAttribute("maxAge", maxAge);

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
        if (file != null && !file.isEmpty()) avatarUrl = uploadService.upload(file);

        Department department = null;
        if (employeeDto.getDepartmentId() != null) department = departmentRepository.findById(employeeDto.getDepartmentId()).orElse(null);

        Employee employee = Employee.builder().name(employeeDto.getName()).age(employeeDto.getAge()).status(employeeDto.getStatus()).avatar(avatarUrl).department(department).build();

        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @PostMapping("/departments/delete")
    public String deleteDepartment(@RequestParam Long departmentId, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(departmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phòng ban và cập nhật trạng thái cho các nhân viên liên quan.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa phòng ban: " + e.getMessage());
        }
        return "redirect:/employees";
    }
}
