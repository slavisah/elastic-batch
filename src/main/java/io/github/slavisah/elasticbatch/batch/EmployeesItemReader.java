package io.github.slavisah.elasticbatch.batch;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import io.github.slavisah.elasticbatch.domain.Department;
import io.github.slavisah.elasticbatch.domain.Employee;

@Component
@JobScope
public class EmployeesItemReader extends JdbcCursorItemReader<Employee> {

    @Autowired
    public EmployeesItemReader(DataSource dataSource) {
        this.setSql("select * from employees " +
                "left join dept_emp on employees.emp_no = dept_emp.emp_no " +
                "left join departments on dept_emp.dept_no = departments.dept_no " +
                "where employees.emp_no = 10010");
        this.setRowMapper(new EmployeeRowMapper(new DepartmentRowMapper()));
        this.setDataSource(dataSource);
    }

    public class DepartmentRowMapper implements RowMapper<Department> {

        public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
            Department department = new Department();
            department.setDeptNo(rs.getString("dept_no"));
            department.setDeptName(rs.getString("dept_name"));
            return department;      
        }
    }

    public class EmployeeRowMapper implements RowMapper<Employee> {

        private final DepartmentRowMapper departmentRowMapper;

        public EmployeeRowMapper(DepartmentRowMapper departmentRowMapper) {
            this.departmentRowMapper = departmentRowMapper;
        }

        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee employee = new Employee();
            employee.setEmpNo(rs.getLong("emp_no"));
            employee.setFirstName(rs.getString("first_name"));
            
            Department department = this.departmentRowMapper.mapRow(rs, rowNum);
            // department.set(employee);
            employee.getDepartments().add(department);

            return employee;
        }
    }
}
