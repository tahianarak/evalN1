package site.easy.to.build.crm.my.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import site.easy.to.build.crm.my.model.DatabaseIniter;
import site.easy.to.build.crm.my.service.DatabaseIniterService;

@Controller
public class DatabaseIniterController
{
    @Autowired
    DatabaseIniterService databaseIniterService;


    @GetMapping("/manager/deleteData")
    public String reiniDatabase()throws Exception
    {
        try
        {
            databaseIniterService.reinitDatabase();
            return "redirect:/";
        }
        catch (Exception e )
        {
            e.printStackTrace();
            throw e;
        }
    }
}
