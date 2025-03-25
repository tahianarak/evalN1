package site.easy.to.build.crm.my.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.my.model.Depense;
import site.easy.to.build.crm.my.service.DepenseService;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;

import java.sql.SQLOutput;
import java.util.List;

@RestController
@RequestMapping("/api/depense")
public class DepenseAPI {

    @Autowired
    DepenseService depenseService;

    @Autowired
    LeadServiceImpl leadService;

    @Autowired
    TicketServiceImpl ticketService;
    @GetMapping("/all")
    public ResponseEntity<List<Depense>> getAllDepenses() {
        try {
            List<Depense> depenses = depenseService.getAllWithCust();

            if (depenses.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(depenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/update")
    public ResponseEntity<String> updateDepense(@RequestParam Long idDepense, @RequestParam String montant) {
        try {
            boolean updated = depenseService.modify(Double.parseDouble(montant),idDepense);
            if (updated) {
                return ResponseEntity.ok("Dépense mise à jour avec succès");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dépense non trouvée");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> updateDepense(@RequestParam Integer idDepense) {
        try {
            depenseService.deleteRecursive(idDepense);
            return ResponseEntity.ok("Dépense mise à jour avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }

    @PostMapping("/modiferPourcentage")
    public ResponseEntity<String> updatePourcentage(@RequestParam double pourcentage) {
        try {
            depenseService.updatePourcentage(pourcentage);
            return ResponseEntity.ok("Dépense mise à jour avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }



}
