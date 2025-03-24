package site.easy.to.build.crm.my.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.my.service.LoginService;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lo")
public class LoginAPI {

    @Autowired
    LoginService loginService;
    @GetMapping("/data")
    public boolean getSessionData(@RequestParam("jsessionid") String jsessionid, HttpServletRequest request) {
        HttpSession session = loginService.getSessionById(jsessionid, request);
        return loginService.hasRoleManager(session);
    }


}
