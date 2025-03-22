package site.easy.to.build.crm.my.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.my.model.Depense;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class DepenseService
{
    @Autowired
    DataSource dataSource;
    public void insertDepense(Depense depense)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            depense.insert(connection);
        }
    }
}
