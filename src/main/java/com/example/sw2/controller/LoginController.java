package com.example.sw2.controller;


import com.example.sw2.entity.Usuarios;
import com.example.sw2.job.TokenExpirationJob;
import com.example.sw2.repository.UsuariosRepository;
import com.example.sw2.utils.CustomMailService;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.quartz.*;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.*;

@Controller
@RequestMapping(value = {"/",""})
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final int EXPIRATION_TIME_MINUTES = 60;

    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    CustomMailService customMailService;
    @Autowired
    private Scheduler scheduler;


    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    private static String authorizationRequestBaseUri = "oauth2/authorize";
    private Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private static String NO_REGISTERED_USER_MESSAGE = "Usuario no registrado con google mail, contacte a un administrador";


    @GetMapping(value = {"/","/loginForm"})
    public String login(Model model, Authentication auth, HttpServletRequest request){
        /* For google auth:*/
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);

        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration ->
                oauth2AuthenticationUrls.put(registration.getClientName(),
                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));

        model.addAttribute("urls", oauth2AuthenticationUrls);
         /*For google auth ---- end */

        String rol = "";

        if(auth == null){
            return "login";
        }else{
            for (GrantedAuthority role : auth.getAuthorities()) {
                rol = role.getAuthority();
                break;
            }
            switch (rol) {
                case "admin":
                    return "redirect:/admin/";
                case "gestor":
                    return "redirect:/gestor/";
                case "sede":
                    return "redirect:/sede/";
                default:
                    try{
                        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
                        request.logout();}
                    catch (ServletException e) {
                        e.printStackTrace();
                    }
            }
            return "redirect:/";
        }
    }


    @GetMapping("/loginSuccess")
    public String getLoginInfo(HttpServletRequest request, OAuth2AuthenticationToken authentication,
                               HttpSession session, RedirectAttributes attr) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());
        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());
            HttpEntity entity = new HttpEntity("", headers);
            ResponseEntity<Map> response = restTemplate
                    .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            Usuarios usuario = usuariosRepository.findByCorreo((String)userAttributes.get("email"));
            if (usuario==null){
                try { //Salir
                    SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
                    request.logout();
                    attr.addFlashAttribute("msgError",NO_REGISTERED_USER_MESSAGE);
                    return "redirect:/loginForm";
                } catch (ServletException e) {
                    e.printStackTrace();
                }
            }
            else {
                //Usuario registrado en el sistema
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(usuario.getRoles().getNombrerol()));
                Authentication auth =  new UsernamePasswordAuthenticationToken(usuario.getCorreo(), usuario.getPassword(), authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

                session.setAttribute("usuario", usuario);
                String rol="";
                for (GrantedAuthority role : auth.getAuthorities()) {
                    rol = role.getAuthority();
                    break;
                }
                switch (rol) {
                    case "admin":
                        return "redirect:/admin/";
                    case "gestor":
                        return "redirect:/gestor/";
                    case "sede":
                        return "redirect:/sede/";
                    default:
                        try{
                        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
                        request.logout();}
                        catch (ServletException e) {
                            e.printStackTrace();
                        }
                }
            }
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
            attr.addFlashAttribute("msgError","Error en el inicio de sesion");
            return "redirect:/loginForm";
        }

        return "/loginForm?error";
    }


    @GetMapping("/redirectByRole")
    public String redirectByRole(Authentication auth, HttpSession session, HttpServletRequest request) {
        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }

        Usuarios usuarioLogueado = usuariosRepository.findByCorreo(auth.getName());
        session.setAttribute("usuario", usuarioLogueado);

        switch (rol) {
            case "admin":
                return "redirect:/admin/";
            case "gestor":
                return "redirect:/gestor/";
            case "sede":
                return "redirect:/sede/";
            default:
                try{
                    SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
                    request.logout();}
                catch (ServletException e) {
                    e.printStackTrace();
                }
        }
        return "redirect:/";
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword(@ModelAttribute("usuario") Usuarios usuarios){return "forgot-password";}

    @PostMapping("/processForgotPassword")
    public String processForgotPassword(@ModelAttribute("usuario") Usuarios usuarios,
                                        BindingResult bindingResult, Model model,
                                        RedirectAttributes attr) throws IOException, MessagingException, NoSuchAlgorithmException, SchedulerException {


        ZonedDateTime dateTime = ZonedDateTime.now();



        String URL = "http://mosqoy.cloudns.cl:9000";
       //String URL = "http://ec2-54-198-53-188.compute-1.amazonaws.com:9000";

        String email = "";
        String id = "";
        String token = "";
        Usuarios u;
        //valida si el campo está vacío
        if(usuarios.getCorreo() == null){
            bindingResult.rejectValue("correo", "error.user", "Este campo no puede estar vacío");
        }else{
            //valida si es una dirección tiene un dominio váldio
            if (!(usuarios.getCorreo().contains("@"))){
                bindingResult.rejectValue("correo", "error.user", "Ingrese una dirección de correo válida");
            }else{
                //Busca el correo en la bd
                u = usuariosRepository.findByCorreo(usuarios.getCorreo());
                if(u == null){
                    bindingResult.rejectValue("correo","error.user","Este email no está registrado");
                }else{
                    id = Integer.toString(u.getIdusuarios()) + Integer.toString(getRandom());
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] encodedhash = digest.digest(
                            id.getBytes(StandardCharsets.UTF_8));
                    token =bytesToHex(encodedhash);
                    u.setToken(token);
                    usuariosRepository.save(u);
                    JobDetail jobDetail = buildJobDetail(u);
                    Trigger trigger = buildJobTrigger(jobDetail, dateTime.plusMinutes(EXPIRATION_TIME_MINUTES));
                    scheduler.scheduleJob(jobDetail, trigger);
                    email = u.getCorreo();
                }
            }
        }

        if(bindingResult.hasErrors()){
            return "forgot-password";
        }else{

            customMailService.sendSimpleMail(email,
                    "Mosqoy - Recuperación de contraseña", "Nueva contraseña",
                    "Para restablecer su contraseña ingrese al siguiente enlace \n"
                            +URL+"/newpassword?t="+ token+"\n<br> " +
                            "Este enlace solo estará disponible por los siguientes "+ EXPIRATION_TIME_MINUTES +  " minutos");
            return "message-sent";
        }

    }

    @GetMapping("/newpassword")
    public String newPassword(@ModelAttribute("usuario") Usuarios u, @RequestParam("t") String token,
                                  Model model){

        u = usuariosRepository.findByToken(token);
        if (u==null){
            return "redirect:/";
        }
        model.addAttribute("token", token);
        return "new-password";
    }

    @PostMapping("/savenewpassword")
    public String saveNewPassword(@ModelAttribute("usuario") Usuarios u,
                                         BindingResult bindingResult,Model model,
                                         @RequestParam("pass") String pass,@RequestParam("token") String token){

        Usuarios usuario = usuariosRepository.findByToken(token);

        if (usuario!=null){
            if(pass == null || pass.equals("")){
                bindingResult.rejectValue("password2","error.user","Este campo no puede estar vacío");
            }else{
                if(!(u.getPassword().equals(pass))){
                    bindingResult.rejectValue("password2","error.user","Las contraseñas deben de ser idénticas");
                }else{
                    if(!u.validatePassword()){
                        bindingResult.rejectValue("password2","error.user","La contraseña debe tener 8 caracteres como mínimo y un caracter especial");
                    }
                }
            }

            if(bindingResult.hasErrors()){
                model.addAttribute("msgError", "ERROR");
                model.addAttribute("token", token);
                u.setPassword2("");
                u.setPassword("");
                return "new-password";
            }else{
                u.setPassword(pass);
                usuariosRepository.actualizar_password(token, u.getPassword(), usuario.getIdusuarios());
            }
        }

        return "redirect:/";

    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static int getRandom(){
        int min = 10;
        int max = 1000;
        return (int)(Math.random()*((max-min)+1))+min;
    }

    private JobDetail buildJobDetail(Usuarios usuarios) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("user", usuarios.getIdusuarios());

        return JobBuilder.newJob(TokenExpirationJob.class)
                .withIdentity(UUID.randomUUID().toString(), "login-jobs")
                .withDescription("Token expiration Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "token-password-triggers")
                .withDescription("Token expiration for password recovery")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }


}
