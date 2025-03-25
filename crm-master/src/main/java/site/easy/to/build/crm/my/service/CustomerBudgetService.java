package site.easy.to.build.crm.my.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.my.model.CustomerBudget;
import site.easy.to.build.crm.my.repository.CustomerBudgetRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@Service
public class CustomerBudgetService
{
    @Autowired
    DataSource dataSource;

    @Autowired
    CustomerBudgetRepository customerBudgetRepository;

    public void save(CustomerBudget customerBudget)
    {
        customerBudgetRepository.save(customerBudget);
    }

    public List<CustomerBudget> getAll()throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            return CustomerBudget.getAll(connection);
        }
    }
    public void insertCustomerBudget(CustomerBudget customerBudget)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            customerBudget.insert(connection);
        }
    }
}
