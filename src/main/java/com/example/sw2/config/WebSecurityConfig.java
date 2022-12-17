package com.example.sw2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;


import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableJpaAuditing
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl=new JdbcTokenRepositoryImpl();
        jdbcTokenRepositoryImpl.setDataSource(dataSource);
        return jdbcTokenRepositoryImpl;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http.csrf().disable();

        http.formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/processLogin")
                .defaultSuccessUrl("/redirectByRole", true);
        http.authorizeRequests()
                .antMatchers("/admin", "/admin/**").hasAnyAuthority("admin")
                .antMatchers("/gestor", "/gestor/**").hasAnyAuthority("gestor")
                .antMatchers("/sede", "/sede/**").hasAnyAuthority("sede")
                .antMatchers("/notification", "/notification/**").hasAnyAuthority("admin","gestor","sede")
                .antMatchers("/loginForm","/signup","/forgotpassword","/processForgotPassword").anonymous()

                .and()
                .oauth2Login()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(authorizationRequestRepository())
                    .and()
                    .defaultSuccessUrl("/loginSuccess")
                    .failureUrl("/loginForm?errorGoogle")
        ;

        http.logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
    }


    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("SELECT correo, password, cuentaactivada FROM Usuarios WHERE correo = ?")
                .authoritiesByUsernameQuery("SELECT u.correo, r.nombrerol, u.cuentaactivada FROM Usuarios u INNER JOIN " +
                        " Roles r ON (u.rol = r.idroles) WHERE u.correo = ? and u.cuentaactivada = 1");

    }

}
