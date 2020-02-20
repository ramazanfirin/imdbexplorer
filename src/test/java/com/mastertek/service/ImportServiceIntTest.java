package com.mastertek.service;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.mastertek.ImdbexplorerApp;
import com.mastertek.domain.Person;
import com.mastertek.domain.enumeration.RecordStatus;
import com.mastertek.repository.PersonRepository;

import io.github.jhipster.config.JHipsterProperties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImdbexplorerApp.class)
public class ImportServiceIntTest {

    @Autowired
    private JHipsterProperties jHipsterProperties;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ImportService importService;
    
    @Autowired
    private PersonRepository personRepository;
    
    @Spy
    private JavaMailSenderImpl javaMailSender;

    @Captor
    private ArgumentCaptor messageCaptor;

    private MailService mailService;

    @Before
    public void setup() {

    }

    @Test
    public void startImport() throws Exception {
    	importService.startImport();
    	
    	Person person = personRepository.findAll().get(0);
    	assertThat(person.getName()).isEqualTo("Terence_Bernie_Hines");
    	assertThat(person.getIndex()).isEqualTo("nm0385722");
    	assertThat(person.getStatus()).isEqualTo(RecordStatus.PROCESSED);
    	//assertThat(person.getInsert()).isNotNull();
    	assertThat(person.getPath()).isEqualTo("https://images-na.ssl-images-amazon.com/images/M/MV5BMjM3ODk1NjEzOV5BMl5BanBnXkFtZTgwOTAyNjU1MzE@._V1_.jpg");
    	assertThat(person.getAfid()).isNotNull();
    	
    	
    }

}
