package site.easy.to.build.crm.my.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.my.model.CustomerBudget;

@Repository
public interface CustomerBudgetRepository extends JpaRepository<CustomerBudget,Integer> {
}
