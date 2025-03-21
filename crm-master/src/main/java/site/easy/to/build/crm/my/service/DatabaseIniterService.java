package site.easy.to.build.crm.my.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.my.model.DatabaseIniter;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class DatabaseIniterService
{
    @Autowired
    DataSource dataSource;

    public void reinitDatabase()throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            connection.setAutoCommit(false);
            DatabaseIniter.reinitDatabase(connection);
            connection.setAutoCommit(true);
        }
    }

}
