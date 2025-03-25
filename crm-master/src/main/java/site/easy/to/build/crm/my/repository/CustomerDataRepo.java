package site.easy.to.build.crm.my.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.my.model.CustomerData;

import java.util.List;

@Repository
public interface CustomerDataRepo extends JpaRepository<CustomerData, Integer>
{

    @Query("SELECT c FROM CustomerData c WHERE c.type = 'ticket'")
    List<CustomerData> getAllTicketCustData();
    @Query("SELECT c FROM CustomerData c WHERE c.type = 'lead'")
    List<CustomerData> getAllLeadCustData();

}
