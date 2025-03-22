package site.easy.to.build.crm.my.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.my.model.CustomerBudget;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class CustomerBudgetService
{
    @Autowired
    DataSource dataSource;

    public void insertCustomerBudget(CustomerBudget customerBudget)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            customerBudget.insert(connection);
        }
    }
}
