package demo.employee.specification;

import demo.employee.model.Department;
import demo.employee.model.Employee;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class EmployeeSpecification {

    public static Specification<Employee> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Employee> hasDepartment(Long departmentId) {
        return (root, query, criteriaBuilder) -> {
            if (departmentId == null) {
                return null;
            }
            Join<Employee, Department> departmentJoin = root.join("department");
            return criteriaBuilder.equal(departmentJoin.get("id"), departmentId);
        };
    }

    public static Specification<Employee> hasAgeBetween(Integer minAge, Integer maxAge) {
        return (root, query, criteriaBuilder) -> {
            if (minAge == null && maxAge == null) {
                return null;
            }
            if (minAge != null && maxAge != null) {
                return criteriaBuilder.between(root.get("age"), minAge, maxAge);
            }
            if (minAge != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("age"), minAge);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("age"), maxAge);
        };
    }
}
