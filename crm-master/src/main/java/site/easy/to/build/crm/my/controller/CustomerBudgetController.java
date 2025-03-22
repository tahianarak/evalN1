package site.easy.to.build.crm.my.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.my.model.CustomerBudget;
import site.easy.to.build.crm.my.service.CustomerBudgetService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;

import java.util.List;

@RequestMapping("/employee/customerBudget")
@Controller
public class CustomerBudgetController
{
    @Autowired
    CustomerBudgetService customerBudgetService;

    @Autowired
    CustomerServiceImpl customerService;

    @GetMapping("create")
    public String insertFormulaire(Model model)
    {
        List<Customer> customers=customerService.findAll();
        model.addAttribute("customes",customers);
        return "budget/formulaire";
    }

    @PostMapping("create")
    public String insertClientBudget(
            @RequestParam("montant") double montant,
            @RequestParam("idCustomer") int idCustomer,
            @RequestParam("date") String date, HttpServletRequest request)throws Exception
    {
        try {
            CustomerBudget customerBudget = new CustomerBudget();
            customerBudget.setCustomerId(idCustomer);
            customerBudget.setMontant(montant);
            customerBudget.setDateEns((java.sql.Date.valueOf(date).toLocalDate()));
            customerBudgetService.insertCustomerBudget(customerBudget);
        }catch (Exception e)
        {
            e.printStackTrace();
            return "error/500";
        }
        return  "redirect:/employee/customerBudget/create";
    }

}
