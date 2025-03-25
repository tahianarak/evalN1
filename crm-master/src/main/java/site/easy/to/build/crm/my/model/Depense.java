package site.easy.to.build.crm.my.model;
import jakarta.persistence.*;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name="depense")
public class Depense {

    @Transient
    double pourcentage;

    @Id
    @Column(name ="id_depense")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDepense;

    @Column(name="montant")
    private double montant;
    @Column(name = "date_ens")// Utilisation du type primitif 'double' ici
    private LocalDateTime dateEns;

    @Column(name = "lead_id")// Utilisation de LocalDateTime
    private Integer leadId;
    @Column(name = "ticket_id")
    private Integer ticketId;
    @Transient
    private Integer customerId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static void updatePourcentage(Connection connection,double pourcentage)throws Exception
    {
        String sql ="update pourcentage set valeur=?";
        try(PreparedStatement preparedStatement=connection.prepareStatement(sql))
        {
            preparedStatement.setDouble(1,pourcentage);

            preparedStatement.executeUpdate();
        }
    }

    public static  double getPourcentage(Connection connection)throws Exception
    {
        String sql="select valeur from pourcentage";
        try(Statement statement=connection.createStatement())
        {
            try(ResultSet rs=statement.executeQuery(sql))
            {
                if(rs.next())
                {
                    return rs.getDouble(("valeur"));
                }
            }
        }
        return 0;
    }

    @Transient
    String description;
    @Transient
    String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }



    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }



    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
    }

    // Getters et setters
    public int getIdDepense() {
        return idDepense;
    }

    public void setIdDepense(int idDepense) {
        this.idDepense = idDepense;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateEns() {
        return dateEns;
    }

    public void setDateEns(LocalDateTime dateEns) {
        this.dateEns = dateEns;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    // Méthode pour obtenir toutes les dépenses

    public static HashMap<Integer,Double> getCustomerDepense(Connection connection,LocalDateTime date,int customerId)throws Exception
    {
        String sql="select sum(montant) as montant from v_depenses_liees where date_ens<=? and customer_id=?";
        HashMap<Integer,Double> map=new HashMap<>();
        try(PreparedStatement statement=connection.prepareStatement(sql))
        {
            statement.setTimestamp(1,Timestamp.valueOf( date));
            statement.setInt(2,customerId);
            try(ResultSet resultSet=statement.executeQuery())
            {
                if (resultSet.next())
                {

                    map.put(customerId, resultSet.getDouble("montant"));
                    return  map;
                }
            }
        }
        map.put(customerId, Double.valueOf(0));
        return map ;
    }

    public static List<Depense> getAllWithCust(Connection connection) throws SQLException {
        List<Depense> depenses = new ArrayList<>();
        String query = "SELECT * FROM v_depenses_liees ";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Depense depense = new Depense();
                depense.setIdDepense(rs.getInt("id_depense"));
                depense.setMontant(rs.getDouble("montant"));
                depense.setDateEns(rs.getTimestamp("date_ens").toLocalDateTime());
                depense.setLeadId(rs.getInt("lead_id"));
                depense.setTicketId(rs.getInt("ticket_id"));
                depense.setCustomerId(rs.getInt("customer_id"));
                depense.setCustomerName(rs.getString("name"));
                depense.setDescription(rs.getString("description"));
                depenses.add(depense);
            }
        }
        return depenses;
    }


    public static List<Depense> getAll(Connection connection) throws SQLException {
        List<Depense> depenses = new ArrayList<>();
        String query = "SELECT * FROM depense";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Depense depense = new Depense();
                depense.setIdDepense(rs.getInt("id_depense"));
                depense.setMontant(rs.getDouble("montant"));
                // Conversion de la colonne DATE vers LocalDateTime
                depense.setDateEns(rs.getTimestamp("date_ens").toLocalDateTime());
                depense.setLeadId(rs.getInt("lead_id"));
                depense.setTicketId(rs.getInt("ticket_id"));
                depenses.add(depense);
            }
        }
        return depenses;
    }

    // Méthode pour obtenir une dépense par son ID
    public static Depense getById(Connection connection, int id) throws SQLException {
        String query = "SELECT * FROM depense WHERE id_depense = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Depense depense = new Depense();
                    depense.setIdDepense(rs.getInt("id_depense"));
                    depense.setMontant(rs.getDouble("montant"));
                    // Conversion de la colonne DATE vers LocalDateTime
                    depense.setDateEns(rs.getTimestamp("date_ens").toLocalDateTime());
                    depense.setLeadId(rs.getInt("lead_id"));
                    depense.setTicketId(rs.getInt("ticket_id"));
                    return depense;
                }
            }
        }
        return null;
    }



    public static Depense getByIdLead(Connection connection, int idLead) throws SQLException {
        String query = "SELECT * FROM depense WHERE Lead_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idLead);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Depense depense = new Depense();
                    depense.setIdDepense(rs.getInt("id_depense"));
                    depense.setMontant(rs.getDouble("montant"));
                    // Conversion de la colonne DATE vers LocalDateTime
                    depense.setDateEns(rs.getTimestamp("date_ens").toLocalDateTime());
                    depense.setLeadId(rs.getInt("lead_id"));
                    depense.setTicketId(rs.getInt("ticket_id"));
                    return depense;
                }
            }
        }
        return null;
    }

    public static Depense getByIdTicket(Connection connection, int idTicket) throws SQLException {
        String query = "SELECT * FROM depense WHERE Lead_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idTicket);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Depense depense = new Depense();
                    depense.setIdDepense(rs.getInt("id_depense"));
                    depense.setMontant(rs.getDouble("montant"));
                    // Conversion de la colonne DATE vers LocalDateTime
                    depense.setDateEns(rs.getTimestamp("date_ens").toLocalDateTime());
                    depense.setLeadId(rs.getInt("lead_id"));
                    depense.setTicketId(rs.getInt("ticket_id"));
                    return depense;
                }
            }
        }
        return null;
    }


    public boolean update(Connection connection) throws SQLException {
        String query = "UPDATE depense SET montant = ?, date_ens = ?, lead_id = ?, ticket_id = ? WHERE id_depense = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, this.montant);
            stmt.setTimestamp(2, Timestamp.valueOf(this.dateEns));
            stmt.setInt(3, this.leadId);
            stmt.setInt(4, this.ticketId);
            stmt.setInt(5, this.idDepense);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean updateMontant(Connection connection,double montant,long idDepense) throws SQLException {
        String query = "UPDATE depense SET montant = ? WHERE id_depense = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, montant);
            stmt.setLong(2, idDepense);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Méthode pour supprimer une dépense
    public boolean delete(Connection connection) throws SQLException {
        String query = "DELETE FROM depense WHERE id_depense = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, this.idDepense);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }


    public boolean insert(Connection connection) throws SQLException {
        String query = "INSERT INTO depense (montant, date_ens, lead_id, ticket_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, this.montant);

            stmt.setTimestamp(2, Timestamp.valueOf(this.dateEns));
            if (leadId == null) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, leadId);
            }
            if (ticketId == null) {
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(4, ticketId);
            }
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.idDepense = generatedKeys.getInt(1);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
