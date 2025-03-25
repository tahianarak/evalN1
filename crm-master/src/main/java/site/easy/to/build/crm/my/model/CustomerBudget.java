package site.easy.to.build.crm.my.model;



import jakarta.persistence.*;
import site.easy.to.build.crm.entity.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Entity
@Table(name = "customer_budget")
public class CustomerBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_budget_customer")
    private int idBudgetCustomer;

    @Column(name = "montant")
    private double montant;

    @Column(name="date_ens")
    private LocalDate dateEns;
    @Column(name = "customer_id")
    private int customerId;

    @Transient
    private  int ligne;

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }



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

    public void setMontant(double montant)throws Exception
    {
        if(montant<0)
        {
            throw new Exception("montant negatif pour la ligne de donnees:"+this.getLigne()+" dans les donnees de budgets");
        }
        this.montant = montant;
    }
    public void setMontant(String valeur)throws Exception
    {
        valeur=valeur.replace(".","");
        valeur=valeur.replace(',','.');
        valeur=valeur.replace(" ","");
        double montant=Double.valueOf(valeur);
        setMontant(montant);

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

    public static HashMap<Integer,Double> getCustomerBudgetBefore(Connection connection, int customerId, LocalDateTime date) throws SQLException {
        List<CustomerBudget> budgets = new ArrayList<>();
        HashMap<Integer,Double> map=new HashMap<>();
        String query = "SELECT sum(montant) as montant FROM customer_budget where date_ens<=? and customer_id=?";
        try(PreparedStatement statement=connection.prepareStatement(query))
        {
            statement.setTimestamp(1,Timestamp.valueOf(date));
            statement.setInt(2,customerId);
            try(ResultSet resultSet=statement.executeQuery())
            {
                if (resultSet.next())
                {
                    double montant =resultSet.getDouble("montant");

                    map.put(customerId,montant);
                    return map;
                }
            }
        }
        map.put(customerId, Double.valueOf(0));
        return map ;
    }


    public static List<CustomerBudget> getAll(Connection connection) throws Exception {
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

    public static CustomerBudget getById(Connection connection, int id) throws Exception {
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
