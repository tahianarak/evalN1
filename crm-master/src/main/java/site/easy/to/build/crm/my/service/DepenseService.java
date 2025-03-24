package site.easy.to.build.crm.my.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.my.model.CustomerBudget;
import site.easy.to.build.crm.my.model.Depense;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

@Service
public class DepenseService
{

    @Autowired
    DataSource dataSource;

    @Autowired
    LeadServiceImpl leadService;

    @Autowired
    TicketService ticketService;

    @Transactional
    public void deleteRecursive(int idDepense)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            Depense depense=Depense.getById(connection,idDepense);
            System.out.println(idDepense);
            System.out.println(depense.getLeadId());
            System.out.println(depense.getTicketId());

            depense.delete(connection);

            if(depense.getLeadId()!=0) {
                Lead lead = leadService.findByLeadId(depense.getLeadId());
                leadService.delete(lead);
            }
            if(depense.getTicketId()!=0) {
                Ticket ticket = ticketService.findByTicketId(depense.getTicketId());
                ticketService.delete(ticket);
            }


        }
    }
    public void updatePourcentage(double valeur)throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            Depense.updatePourcentage(connection,valeur);
        }
    }

    public boolean modify(double montant , long idDepense)throws  Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            Depense.updateMontant(connection,montant,idDepense);
            return true;
        }
    }

    public List<Depense> getAllWithCust()throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            return Depense.getAllWithCust(connection);
        }
    }

    public void deleteById(int idDepense)throws Exception{
        try(Connection connection=dataSource.getConnection())
        {
            Depense depense=new Depense();
            depense.setIdDepense(idDepense);
            depense.delete(connection);
        }
    }

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
            if(montantDepense+depense.getMontant()>=montantBudget*(Depense.getPourcentage(connection)/100))
            {
                double reste=montantBudget-montantDepense-depense.getMontant();
                ans="Cota d'alerte atteint pour ce client,il reste "+reste;
            }
            depense.insert(connection);
            return ans;
        }
    }


}
