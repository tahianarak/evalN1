package site.easy.to.build.crm.my.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.my.model.CustomerBudget;
import site.easy.to.build.crm.my.model.Depense;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;

@Service
public class DepenseService
{
    double cota=0.8;
    @Autowired
    DataSource dataSource;

    public void deleteByIdLead(Lead lead)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
             Depense.getByIdLead(connection,lead.getLeadId()).delete(connection);
        }
    }



    public void deleteByIdTicket(Ticket ticket)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
             Depense.getByIdTicket(connection,ticket.getTicketId()).delete(connection);
        }
    }


    public boolean checkBudget(Depense depense ,int idCustomer)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            HashMap<Integer, Double> montantTotalBudgetCust= CustomerBudget.getCustomerBudgetBefore(connection,idCustomer,depense.getDateEns());
            HashMap<Integer, Double> montantTotalDepenseCust= Depense.getCustomerDepense(connection,depense.getDateEns(),idCustomer);
            double montantDepense=montantTotalDepenseCust.get(idCustomer);
            double montantBudget=montantTotalBudgetCust.get(idCustomer);

            if(montantBudget-montantDepense-depense.getMontant()<0)
            {
                return  false;
            }
        }
        return true;
    }

    public String insertDepense(Depense depense ,int idCustomer)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            HashMap<Integer, Double> montantTotalBudgetCust= CustomerBudget.getCustomerBudgetBefore(connection,idCustomer,depense.getDateEns());
            HashMap<Integer, Double> montantTotalDepenseCust= Depense.getCustomerDepense(connection,depense.getDateEns(),idCustomer);
            double montantDepense=montantTotalDepenseCust.get(idCustomer);
            double montantBudget=montantTotalBudgetCust.get(idCustomer);
            String ans="";
            if(montantDepense+depense.getMontant()>=montantBudget*cota)
            {
                double reste=montantBudget-montantDepense-depense.getMontant();
                ans="Cota d'alerte atteint pour ce client,il reste "+reste;
            }
            depense.insert(connection);
            return ans;
        }
    }


}
