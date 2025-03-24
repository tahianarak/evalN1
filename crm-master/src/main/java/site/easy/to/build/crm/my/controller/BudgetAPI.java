package site.easy.to.build.crm.my.controller;

import jdk.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.my.model.CustomerBudget;
import site.easy.to.build.crm.my.model.Depense;
import site.easy.to.build.crm.my.service.CustomerBudgetService;
import site.easy.to.build.crm.my.service.DepenseService;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetAPI {

    @Autowired
    CustomerBudgetService customerBudgetService;

    @GetMapping("/all")
    public ResponseEntity<List<CustomerBudget>> getAll() {
        try {
            List<CustomerBudget> customerBudgets = customerBudgetService.getAll();
            if (customerBudgets.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(customerBudgets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





}
