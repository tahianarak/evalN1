package site.easy.to.build.crm.my.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.my.model.CustomerBudget;
import site.easy.to.build.crm.my.model.CustomerData;
import site.easy.to.build.crm.my.model.Depense;
import site.easy.to.build.crm.my.repository.CustomerDataRepo;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

import javax.sql.DataSource;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImportService {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    CustomerBudgetService customerBudgetService;
    @Autowired
    CustomerServiceImpl customerService;

    @Autowired
    CustomerDataRepo customerDataRepo;

    @Autowired
    TicketService ticketService;

    @Autowired
    DepenseService depenseService;

    @Autowired
    AuthenticationUtils authenticationUtils;

    @Autowired
    LeadService leadService;

    @Autowired
    DataSource dataSource;

    @Autowired
    UserService userService;

    char separator=';';


    @Transactional(rollbackFor = Exception.class)
    public void importData(String dataCustomerFileName,String budgetCustomerFile,String customerFile,Authentication authentication)throws Exception
    {

        try
        {
            importCsvCustomer(customerFile, authentication);
            importBudgetCustomer(budgetCustomerFile, authentication);
            importDataCustomer(dataCustomerFileName, authentication);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public  void validate(Authentication authentication)throws Exception
    {
        int userId= authenticationUtils.getLoggedInUserId(authentication) ;
        try(Connection connection=dataSource.getConnection())
        {
            insertDepenseWithLead(connection,userId);
            insertDepenseWithTicket(connection,userId);
        } catch (Exception e) {
            throw e;
        }


    }
    public void cleanCustomerData()throws Exception
    {
        try(Connection connection=dataSource.getConnection()) {
            String sql = "delete from customer_data";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        }
    }

    public void insertDepenseWithTicket(Connection connection,int userId)throws Exception
    {
        List<CustomerData> tickets=customerDataRepo.getAllTicketCustData();
        LocalDateTime localDateTime=LocalDateTime.now();
        for(CustomerData ticketDepense:tickets)
        {
            Ticket ticket=new Ticket();
            ticket.setCustomer(customerService.findByEmail(ticketDepense.getCustomerEmail()));
            ticket.setCreatedAt(localDateTime);
            ticket.setStatus(ticketDepense.getStatus());

            User user=userService.findById(userId);

            ticket.setEmployee(user);
            ticket.setManager(user);
            ticket.setPriority("low");
            ticket.setSubject(ticketDepense.getSubjectOrName());
            ticket.setMontant(ticketDepense.getExpense());

            Ticket createdTicket=ticketService.save(ticket);

            Depense depense=new Depense();
            depense.setMontant(ticketDepense.getExpense());
            depense.setTicketId(createdTicket.getTicketId());
            depense.setDateEns(localDateTime);

            depenseService.save(depense);

        }
    }

    public void insertDepenseWithLead(Connection connection,int userId)throws Exception
    {
        List<CustomerData> leads=customerDataRepo.getAllLeadCustData();
        LocalDateTime localDateTime=LocalDateTime.now();
        for(CustomerData leadDepense:leads)
        {
            Lead lead=new Lead();
            lead.setCustomer(customerService.findByEmail(leadDepense.getCustomerEmail()));
            lead.setCreatedAt(localDateTime);
            lead.setStatus(leadDepense.getStatus());
            lead.setName(leadDepense.getSubjectOrName());
            lead.setEmployee(userService.findById(userId));
            lead.setMontant(leadDepense.getExpense());

            Lead createdlead=leadService.save(lead);


            Depense depense=new Depense();
            depense.setMontant(leadDepense.getExpense());
            depense.setLeadId(createdlead.getLeadId());
            depense.setDateEns(localDateTime);

            depenseService.save(depense);


        }
    }




    public void importDataCustomer(String csvFile,Authentication authentication) throws Exception{
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile))
                    .withCSVParser(parser)
                    .build();

            List<String[]> allData = reader.readAll();
            reader.close();

            allData.remove(0);
            int counter=1;
            for (String[] line : allData)
            {
                CustomerData customerData=new CustomerData();
                customerData.setLigne(counter);
                customerData.setCustomerEmail(line[0],customerService);
                customerData.setSubjectOrName(line[1]);
                customerData.setType(line[2]);
                customerData.setStatus(line[3]);
                customerData.setExpense(line[4]);

                customerDataRepo.save(customerData);
                counter++;
            }
            validate(authentication);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        }
    }

    public void importBudgetCustomer(String csvFile,Authentication authentication)throws Exception
    {
        try
        {
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile))
                    .withCSVParser(parser)
                    .build();

            List<String[]> allData = reader.readAll();
            reader.close();

            allData.remove(0);

            LocalDateTime localDateTime=LocalDateTime.now();
            int counter=1;
            for (String[] line : allData) {
                CustomerBudget customerBudget =new CustomerBudget();
                customerBudget.setLigne(counter);
                if(customerService.findByEmail(line[0].trim())==null)
                {
                    throw new Exception("le mail n'existe pas dans la liste des clients"+counter+",dans les budgets clients");
                }
                Customer customer=customerService.findByEmail(line[0].trim());
                customerBudget.setCustomerId(customer.getCustomerId());
                customerBudget.setMontant(line[1]);
                customerBudget.setDateEns(localDateTime.toLocalDate());
                customerBudgetService.save(customerBudget);




                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public void importCsvCustomer(String csvFile,Authentication authentication)throws Exception {
        try
        {
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile))
                    .withCSVParser(parser)
                    .build();

            List<String[]> allData = reader.readAll();
            reader.close();

            allData.remove(0);

            for (String[] line : allData) {
                Customer customer = new Customer();
                customer.setName(line[1]);
                customer.setEmail(line[0]);
                customer.setCountry("Mada");
                customer.setUser(userService.findById(authenticationUtils.getLoggedInUserId(authentication)));
                customer.setPhone("0321100319");

                customerService.save(customer);



            }

        } catch (Exception e) {
            e.printStackTrace();
            throw  e;
        }
    }



}
