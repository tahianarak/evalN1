package site.easy.to.build.crm.my.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseIniter
{
    public static void reinitDatabase(Connection connection) throws SQLException {

        String[] sqls= {
                        "set foreign_key_checks=0",
                "TRUNCATE TABLE contract_settings",
                "TRUNCATE TABLE google_drive_file",
                "TRUNCATE TABLE file",
                "TRUNCATE TABLE email_template",
                "TRUNCATE TABLE lead_action",
                "TRUNCATE TABLE lead_settings",
                "TRUNCATE TABLE ticket_settings",
                "TRUNCATE TABLE trigger_contract",
                "TRUNCATE TABLE trigger_lead",
                "TRUNCATE TABLE trigger_ticket",
                "set foreign_key_checks=1"
        };
        try(Statement statement= connection.createStatement()) {

            for ( String sql:sqls)
            {
                statement.executeUpdate(sql);
            }
            connection.commit();
        }
    }
}
