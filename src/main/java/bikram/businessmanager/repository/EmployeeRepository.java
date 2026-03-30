package bikram.businessmanager.repository;

import bikram.businessmanager.entity.Employee;

public class EmployeeRepository extends BaseRepository<Employee>{
    public EmployeeRepository(){
        super(Employee.class);
    }
}
