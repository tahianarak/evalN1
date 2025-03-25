package site.easy.to.build.crm.my.model;

import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
import site.easy.to.build.crm.service.customer.CustomerService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="customer_data")
public class CustomerData {

    @Transient
    String[] leadsStatus={"meeting-to-schedule","assign-to-sales","archived","success"};
    @Transient
    String[] ticketsStatus={"open","assigned","on-hold","in-progress","resolved","closed","reopened","pending-customer-response","escalated","archived"};
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Integer id;

    @Column(name="customer_email")
    private String customerEmail;

    @Column(name = "subject_or_name")
    private String subjectOrName;

    @Column(name="type")
    private String type;

    @Column(name = "status")
    private String status;
    @Column(name = "expense")
    private double expense;

    @Transient
    private  int ligne;
    public CustomerData() {

    }

    public void setCustomerEmail(String customerEmail, CustomerService customerService) throws Exception{
        if(customerService.findByEmail(customerEmail.trim())==null)
        {
            throw new Exception("L'email n'est pas dans la liste des clients a la ligne"+this.ligne+",dans les donnees de depenses");
        }
        this.customerEmail = customerEmail.trim();
    }

    public void setStatus(String status) throws Exception
    {
        if(this.getType().equalsIgnoreCase("lead"))
        {
            for(int i=0;i<leadsStatus.length;i++)
            {
                if(leadsStatus[i].equals(status))
                {
                    this.status = status;
                    return;
                }
            }
        }

        if(this.getType().equalsIgnoreCase("ticket"))
        {
            for(int i=0;i<ticketsStatus.length;i++)
            {
                if(ticketsStatus[i].equals(status))
                {
                    this.status = status;
                    return;
                }
            }
        }
        throw new Exception("le status de la ligne de donnees n"+this.getLigne()+" n'est pas conforme ,dans les donnees de depenses");

    }

    public void setExpense(String valeur)throws Exception
    {
        valeur=valeur.replace(".","");
        valeur=valeur.replace(',','.');
        valeur=valeur.replace(" ","");
        double montant=Double.valueOf(valeur);
        setExpense(montant);

    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }



    // Constructeur, Getters, Setters, et ToString
    public CustomerData(Integer id, String customerEmail, String subjectOrName, String type, String status, double expense) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.subjectOrName = subjectOrName;
        this.type = type;
        this.status = status;
        this.expense = expense;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }



    public void setSubjectOrName(String subjectOrName) {
        this.subjectOrName = subjectOrName;
    }

    public void setType(String type) {
        if (!type.equalsIgnoreCase("lead") && !type.equalsIgnoreCase("ticket")) {
            throw new IllegalArgumentException("Type doit être 'lead' ou 'ticket'. sur la ligne"+this.getLigne()+" dans les donnees de depenses");
        }
        this.type = type.toLowerCase();
    }



    public void setExpense(double expense)throws Exception {
        if(expense<0)
        {
            throw new Exception("montant negatif pour la ligne de donnees:"+this.getLigne()+" dans les donnees de depenses");
        }
        this.expense = expense;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getSubjectOrName() {
        return subjectOrName;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public double getExpense() {
        return expense;
    }
    @Override
    public String toString() {
        return "CustomerData{" +
                "id=" + id +
                ", customerEmail='" + customerEmail + '\'' +
                ", subjectOrName='" + subjectOrName + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", expense=" + expense +
                '}';
    }

    // Méthode pour insérer un customer dans la base de données
    public static int save(Connection conn, CustomerData customerData) throws SQLException {
        String sql = "INSERT INTO customer_data (customer_email, subject_or_name, type, status, expense) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerData.getCustomerEmail());
            stmt.setString(2, customerData.getSubjectOrName());
            stmt.setString(3, customerData.getType());
            stmt.setString(4, customerData.getStatus());
            stmt.setDouble(5, customerData.getExpense());
            return stmt.executeUpdate(); // Retourne le nombre de lignes affectées
        }
    }

    // Méthode pour obtenir un customer par son ID
    public static CustomerData getById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM customer_data WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CustomerData(
                            rs.getInt("id"),
                            rs.getString("customer_email"),
                            rs.getString("subject_or_name"),
                            rs.getString("type"),
                            rs.getString("status"),
                            rs.getDouble("expense")
                    );
                } else {
                    return null; // Aucun résultat
                }
            }
        }
    }

    // Méthode pour récupérer tous les customers
    public static List<CustomerData> getAll(Connection conn) throws SQLException {
        List<CustomerData> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer_data";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(new CustomerData(
                        rs.getInt("id"),
                        rs.getString("customer_email"),
                        rs.getString("subject_or_name"),
                        rs.getString("type"),
                        rs.getString("status"),
                        rs.getDouble("expense")
                ));
            }
        }
        return customers;
    }

    // Méthode pour mettre à jour un customer
    public static int update(Connection conn, CustomerData customerData) throws SQLException {
        String sql = "UPDATE customer_data SET customer_email = ?, subject_or_name = ?, type = ?, status = ?, expense = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerData.getCustomerEmail());
            stmt.setString(2, customerData.getSubjectOrName());
            stmt.setString(3, customerData.getType());
            stmt.setString(4, customerData.getStatus());
            stmt.setDouble(5, customerData.getExpense());
            stmt.setInt(6, customerData.getId());
            return stmt.executeUpdate(); // Retourne le nombre de lignes affectées
        }
    }

    // Méthode pour supprimer un customer par son ID
    public static int delete(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM customer_data WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate(); // Retourne le nombre de lignes affectées
        }
    }

    public static List<CustomerData> getLeads(Connection conn) throws SQLException {
        List<CustomerData> leads = new ArrayList<>();
        String sql = "SELECT * FROM customer_data WHERE type = 'lead'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                leads.add(new CustomerData(
                        rs.getInt("id"),
                        rs.getString("customer_email"),
                        rs.getString("subject_or_name"),
                        rs.getString("type"),
                        rs.getString("status"),
                        rs.getDouble("expense")
                ));
            }
        }
        return leads;
    }

    public static List<CustomerData> getTickets(Connection conn) throws SQLException {
        List<CustomerData> tickets = new ArrayList<>();
        String sql = "SELECT * FROM customer_data WHERE type = 'ticket'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tickets.add(new CustomerData(
                        rs.getInt("id"),
                        rs.getString("customer_email"),
                        rs.getString("subject_or_name"),
                        rs.getString("type"),
                        rs.getString("status"),
                        rs.getDouble("expense")
                ));
            }
        }
        return tickets;
    }




}
