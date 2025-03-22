package site.easy.to.build.crm.my.model;



import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerBudget {

    private int idBudgetCustomer;
    private double montant;
    private LocalDate dateEns;
    private int customerId;

    // Getters et setters
    public int getIdBudgetCustomer() {
        return idBudgetCustomer;
    }

    public void setIdBudgetCustomer(int idBudgetCustomer) {
        this.idBudgetCustomer = idBudgetCustomer;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDate getDateEns() {
        return dateEns;
    }

    public void setDateEns(LocalDate dateEns) {
        this.dateEns = dateEns;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public static List<CustomerBudget> getAll(Connection connection) throws SQLException {
        List<CustomerBudget> budgets = new ArrayList<>();
        String query = "SELECT * FROM customer_budget";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                CustomerBudget budget = new CustomerBudget();
                budget.setIdBudgetCustomer(rs.getInt("id_budget_customer"));
                budget.setMontant(rs.getDouble("montant"));
                budget.setDateEns(rs.getDate("date_ens").toLocalDate());  // Conversion de la colonne DATE vers LocalDate
                budget.setCustomerId(rs.getInt("customer_id"));
                budgets.add(budget);
            }
        }
        return budgets;
    }

    public static CustomerBudget getById(Connection connection, int id) throws SQLException {
        String query = "SELECT * FROM customer_budget WHERE id_budget_customer = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CustomerBudget budget = new CustomerBudget();
                    budget.setIdBudgetCustomer(rs.getInt("id_budget_customer"));
                    budget.setMontant(rs.getDouble("montant"));
                    budget.setDateEns(rs.getDate("date_ens").toLocalDate());
                    budget.setCustomerId(rs.getInt("customer_id"));
                    return budget;
                }
            }
        }
        return null;
    }


    public boolean update(Connection connection) throws SQLException {
        String query = "UPDATE customer_budget SET montant = ?, date_ens = ?, customer_id = ? WHERE id_budget_customer = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, this.montant);
            stmt.setDate(2, Date.valueOf(this.dateEns));  // Conversion de LocalDate en Date SQL
            stmt.setInt(3, this.customerId);
            stmt.setInt(4, this.idBudgetCustomer);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }


    public boolean delete(Connection connection) throws SQLException {
        String query = "DELETE FROM customer_budget WHERE id_budget_customer = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, this.idBudgetCustomer);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }


    public boolean insert(Connection connection) throws SQLException {
        String query = "INSERT INTO customer_budget (montant, date_ens, customer_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, this.montant);
            stmt.setDate(2, Date.valueOf(this.dateEns));  // Conversion de LocalDate en Date SQL
            stmt.setInt(3, this.customerId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.idBudgetCustomer = generatedKeys.getInt(1);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
