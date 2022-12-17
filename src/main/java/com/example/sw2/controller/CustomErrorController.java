package com.example.sw2.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Authentication auth, Model m) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String rol = "";

        if (auth != null) {
            for (GrantedAuthority role : auth.getAuthorities()) {
                rol = role.getAuthority();
                break;
            }
            if (rol.equals("admin") || rol.equals("gestor") || rol.equals("sede")) {
                if (status != null) {
                    Integer statusCode = Integer.valueOf(status.toString());
                    m.addAttribute("etype",statusCode);
                    return rol+"/error";
                    //if (statusCode == HttpStatus.NOT_FOUND.value()) {

                    //} else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                     //   return rol + "/error500";
                    //} else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                    //    return rol + "/error403";
                    //}
                }
            } else {
                try {
                    SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
                    request.logout();
                } catch (ServletException e) {
                    e.printStackTrace();
                }
                return "redirect:/loginForm";
            }
        } else {
            return "redirect:/";
        }
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
