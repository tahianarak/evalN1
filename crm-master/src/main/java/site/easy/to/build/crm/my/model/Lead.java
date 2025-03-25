package site.easy.to.build.crm.my.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lead {

    private static final Logger LOGGER = Logger.getLogger(Lead.class.getName());

    private int leadId;
    private int customerId;
    private Integer userId;
    private String name;
    private String phone;
    private Integer employeeId;
    private String status;
    private String meetingId;
    private Boolean googleDrive;
    private String googleDriveFolderId;
    private LocalDateTime createdAt;

    // Getters and Setters
    public int getLeadId() { return leadId; }
    public void setLeadId(int leadId) { this.leadId = leadId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMeetingId() { return meetingId; }
    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }

    public Boolean getGoogleDrive() { return googleDrive; }
    public void setGoogleDrive(Boolean googleDrive) { this.googleDrive = googleDrive; }

    public String getGoogleDriveFolderId() { return googleDriveFolderId; }
    public void setGoogleDriveFolderId(String googleDriveFolderId) { this.googleDriveFolderId = googleDriveFolderId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Méthodes CRUD
    public static int create(Connection conn, Lead lead) throws SQLException {
        String query = "INSERT INTO trigger_lead (customer_id, user_id, name, phone, employee_id, status, meeting_id, google_drive, google_drive_folder_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, lead.getCustomerId());
            stmt.setObject(2, lead.getUserId(), Types.INTEGER);
            stmt.setString(3, lead.getName());
            stmt.setString(4, lead.getPhone());
            stmt.setObject(5, lead.getEmployeeId(), Types.INTEGER);
            stmt.setString(6, lead.getStatus());
            stmt.setString(7, lead.getMeetingId());
            stmt.setObject(8, lead.getGoogleDrive(), Types.BOOLEAN);  // Handle Boolean with null
            stmt.setString(9, lead.getGoogleDriveFolderId());
            stmt.setObject(10, lead.getCreatedAt(), Types.TIMESTAMP); // Handle LocalDateTime


            int rowsAffected = stmt.executeUpdate();


            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Retourne l'ID de la ligne insérée
                    }
                }
            }
            return -1; // Retourne -1 en cas d'échec
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du lead", e);
            throw e;
        }
    }

    public static Lead read(Connection conn, int leadId) throws SQLException {
        String query = "SELECT * FROM trigger_lead WHERE lead_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, leadId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLead(rs);
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la lecture du lead", e);
            throw e;
        }
    }

    public static List<Lead> getAll(Connection conn) throws SQLException {
        String query = "SELECT * FROM trigger_lead";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            List<Lead> leads = new ArrayList<>();
            while (rs.next()) {
                leads.add(mapResultSetToLead(rs));
            }
            return leads;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les leads", e);
            throw e;
        }
    }

    public static boolean update(Connection conn, Lead lead) throws SQLException {
        String query = "UPDATE trigger_lead SET customer_id = ?, user_id = ?, name = ?, phone = ?, employee_id = ?, status = ?, meeting_id = ?, google_drive = ?, google_drive_folder_id = ?, created_at = ? " +
                "WHERE lead_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, lead.getCustomerId());
            stmt.setObject(2, lead.getUserId(), Types.INTEGER);
            stmt.setString(3, lead.getName());
            stmt.setString(4, lead.getPhone());
            stmt.setObject(5, lead.getEmployeeId(), Types.INTEGER);
            stmt.setString(6, lead.getStatus());
            stmt.setString(7, lead.getMeetingId());
            stmt.setObject(8, lead.getGoogleDrive(), Types.BOOLEAN); // Handle Boolean with null
            stmt.setString(9, lead.getGoogleDriveFolderId());
            stmt.setObject(10, lead.getCreatedAt(), Types.TIMESTAMP); // Handle LocalDateTime
            stmt.setInt(11, lead.getLeadId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du lead", e);
            throw e;
        }
    }

    public static boolean delete(Connection conn, int leadId) throws SQLException {
        String query = "DELETE FROM trigger_lead WHERE lead_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, leadId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du lead", e);
            throw e;
        }
    }

    // Méthode utilitaire pour mapper le ResultSet vers un objet Lead
    private static Lead mapResultSetToLead(ResultSet rs) throws SQLException {
        Lead lead = new Lead();
        lead.setLeadId(rs.getInt("lead_id"));
        lead.setCustomerId(rs.getInt("customer_id"));
        lead.setUserId(rs.getObject("user_id", Integer.class));
        lead.setName(rs.getString("name"));
        lead.setPhone(rs.getString("phone"));
        lead.setEmployeeId(rs.getObject("employee_id", Integer.class));
        lead.setStatus(rs.getString("status"));
        lead.setMeetingId(rs.getString("meeting_id"));
        lead.setGoogleDrive(rs.getBoolean("google_drive"));
        lead.setGoogleDriveFolderId(rs.getString("google_drive_folder_id"));
        lead.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // Convert SQL Timestamp to LocalDateTime
        return lead;
    }
}
