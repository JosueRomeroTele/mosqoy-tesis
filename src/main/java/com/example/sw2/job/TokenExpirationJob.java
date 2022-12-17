package com.example.sw2.job;

import com.example.sw2.entity.Usuarios;
import com.example.sw2.repository.UsuariosRepository;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class TokenExpirationJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(TokenExpirationJob.class);

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

        Usuarios user = usuariosRepository.findById(jobDataMap.getInt("user")).orElse(null);
        if (user!=null){
            user.setToken(null);
            usuariosRepository.save(user);
        }

    }
}
