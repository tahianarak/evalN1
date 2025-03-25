package site.easy.to.build.crm.my.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.my.service.ImportService;

@Controller
@RequestMapping("/import/manager")
public class ImportController
{
    private String folder = "D:\\tahiana\\s6\\evaluation n1\\csv";
    @Autowired
    ImportService importService;
    @GetMapping("/form")
    public String getFormulaireImport(Model model,@RequestParam(name = "error",required = false) String error)
    {
        if(error!=null)
        {
            model.addAttribute("error", error);
        }
        return "import/import";
    }

    @PostMapping("/save")
    public String save(@RequestParam("customerFile") MultipartFile customerFile,
                       @RequestParam("dataFile") MultipartFile dataFile,
                       @RequestParam("bugdetFile") MultipartFile budgetFile,
                       Authentication authentication)
    {
        try {
            if (!customerFile.isEmpty() && !dataFile.isEmpty() && !budgetFile.isEmpty()) {
                importService.importData(folder + "\\" +dataFile.getOriginalFilename() ,folder + "\\" +budgetFile.getOriginalFilename() ,folder + "\\" + customerFile.getOriginalFilename(),authentication);
                importService.cleanCustomerData();
            }

        }
        catch (Exception e)
        {
            String message=e.getMessage();
           return "redirect:/import/manager/form?error="+message;
        }
        return "redirect:/import/manager/form";
    }
}
