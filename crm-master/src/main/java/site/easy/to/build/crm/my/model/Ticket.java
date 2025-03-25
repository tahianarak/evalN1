package site.easy.to.build.crm.my.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ticket {

    private static final Logger LOGGER = Logger.getLogger(Ticket.class.getName());

    private int ticketId;
    private String subject;
    private String description;
    private String status;
    private String priority;
    private int customerId;
    private Integer managerId;
    private Integer employeeId;
    private LocalDateTime createdAt;

    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Méthodes CRUD
    public static int create(Connection conn, Ticket ticket) throws SQLException {
        String query = "INSERT INTO trigger_ticket (subject, description, status, priority, customer_id, manager_id, employee_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ticket.getSubject());
            stmt.setString(2, ticket.getDescription());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getPriority());
            stmt.setInt(5, ticket.getCustomerId());


            if (ticket.getManagerId() != null) {
                stmt.setInt(6, ticket.getManagerId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (ticket.getEmployeeId() != null) {
                stmt.setInt(7, ticket.getEmployeeId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setObject(8, ticket.getCreatedAt(), Types.TIMESTAMP); // Convert LocalDateTime to SQL timestamp

            // Exécuter l'insertion
            int rowsAffected = stmt.executeUpdate();

            // Récupérer l'ID généré
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Retourne l'ID de la ligne insérée
                    }
                }
            }
            return -1; // Retourne -1 en cas d'échec
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du ticket", e);
            throw e;
        }
    }

    public static Ticket read(Connection conn, int ticketId) throws SQLException {
        String query = "SELECT * FROM trigger_ticket WHERE ticket_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la lecture du ticket", e);
            throw e;
        }
    }

    public static List<Ticket> getAll(Connection conn) throws SQLException {
        String query = "SELECT * FROM trigger_ticket";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            List<Ticket> tickets = new ArrayList<>();
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
            return tickets;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les tickets", e);
            throw e;
        }
    }

    public static boolean update(Connection conn, Ticket ticket) throws SQLException {
        String query = "UPDATE trigger_ticket SET subject = ?, description = ?, status = ?, priority = ?, customer_id = ?, manager_id = ?, employee_id = ?, created_at = ? " +
                "WHERE ticket_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ticket.getSubject());
            stmt.setString(2, ticket.getDescription());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getPriority());
            stmt.setInt(5, ticket.getCustomerId());

            // Handle possible null values for managerId and employeeId
            if (ticket.getManagerId() != null) {
                stmt.setInt(6, ticket.getManagerId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (ticket.getEmployeeId() != null) {
                stmt.setInt(7, ticket.getEmployeeId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setObject(8, ticket.getCreatedAt(), Types.TIMESTAMP); // Convert LocalDateTime to SQL timestamp
            stmt.setInt(9, ticket.getTicketId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du ticket", e);
            throw e;
        }
    }

    public static boolean delete(Connection conn, int ticketId) throws SQLException {
        String query = "DELETE FROM trigger_ticket WHERE ticket_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du ticket", e);
            throw e;
        }
    }

    // Méthode utilitaire pour mapper le ResultSet vers un objet Ticket
    private static Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setTicketId(rs.getInt("ticket_id"));
        ticket.setSubject(rs.getString("subject"));
        ticket.setDescription(rs.getString("description"));
        ticket.setStatus(rs.getString("status"));
        ticket.setPriority(rs.getString("priority"));
        ticket.setCustomerId(rs.getInt("customer_id"));
        ticket.setManagerId(rs.getObject("manager_id", Integer.class)); // Using `getObject` to handle possible null values
        ticket.setEmployeeId(rs.getObject("employee_id", Integer.class)); // Using `getObject` to handle possible null values
        ticket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // Convert SQL Timestamp to LocalDateTime
        return ticket;
    }
}
