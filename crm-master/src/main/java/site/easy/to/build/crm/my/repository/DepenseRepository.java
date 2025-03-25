package site.easy.to.build.crm.my.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.my.model.Depense;

@Repository
public interface DepenseRepository extends JpaRepository<Depense,Integer> {

}
