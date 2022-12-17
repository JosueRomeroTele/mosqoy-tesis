package com.example.sw2.controller;

import com.example.sw2.controller.gestor.ListaSedeGestorController;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.repository.UsuariosRepository;
import com.example.sw2.utils.ExceptionView;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;


@ControllerAdvice
public class MainController {
    Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    /*
    @ExceptionHandler(value = MultipartException.class)
    public String erroHandler11(Exception ex, Model model, HttpSession session){
        return errorHandler(ex,model,session);
    }

    @ExceptionHandler(value = SizeLimitExceededException.class)
    public String erroHandler13(Exception ex, Model model, HttpSession session){
        return errorHandler(ex,model,session);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public String erroHandler14(Exception ex, Model model, HttpSession session){
        return errorHandler(ex,model,session);
    }*/

    //@Autowired
    //ListaSedeGestorController listaSedeGestorController;


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(HttpServletRequest request, Throwable ex, HttpSession session,
                                         RedirectAttributes attributes){
        attributes.addFlashAttribute("msgError","Archivo mayor a 2MB");
        String s = (String) session.getAttribute("controller");
        if (s==null || s.isEmpty()){
            s="";
        }
        return "redirect:/"+s;
    }

    /*

    public String erroHandler12(Exception ex, Model model, HttpSession session, RedirectAttributes attr){

        String targetView;
        if (session.getAttribute("view")!=null) {
            attr.addFlashAttribute("msg", "El archivo es mayor al tamaño máximo (2MB)");
             return "redirect:/" ; //+(String)session.getAttribute("view");
        } else {
            targetView = "redirect:/"; // kind of a fallback
        }
        return "index";j
    }*/



}
