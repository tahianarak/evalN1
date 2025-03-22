package site.easy.to.build.crm.my.model;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Depense {

    private int idDepense;
    private double montant;  // Utilisation du type primitif 'double' ici
    private LocalDateTime dateEns;  // Utilisation de LocalDateTime
    private Integer leadId;
    private Integer ticketId;

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
