package bikram.businessmanager.service;

import bikram.businessmanager.entity.Employee;
import bikram.businessmanager.repository.EmployeeRepository;

public class EmployeeService extends BaseService<Employee> {

    public EmployeeService(){
        super(new EmployeeRepository());
    }
}
