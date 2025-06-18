package io.lalitrow.batch.repository;

import io.lalitrow.batch.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
