package com.stackroute.AppRegistration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.AppRegistration.domain.Application;
import com.stackroute.AppRegistration.domain.Service;
import com.stackroute.AppRegistration.domain.User;
import com.stackroute.AppRegistration.exceptions.ApplicationAlreadyExistException;
import com.stackroute.AppRegistration.service.ApplicationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.mockito.Mockito;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    private User user;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApplicationService applicationService;
    @MockBean
    KafkaTemplate<String,String > kafkaTemplate;
    @InjectMocks
    private ApplicationController applicationController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        Service service = new Service("crudj2e","J2E", 8000);
        List<Service> serviceList = new ArrayList<>();
        serviceList.add(service);
        Date dateObj = new Date();
        Application application = new Application("SysCop","Docker","172.13.44.55",dateObj,serviceList);
        List<Application> applicationList = new ArrayList<>();
        user = new User("Ramya",applicationList);
    }

    @Test
    public void newAppRegistration() throws Exception, JsonProcessingException {
        when(applicationService.addApplication(user)).thenReturn(user);

        mockMvc.perform(post("/api/v2/syscop/appregistration/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonToString(user)))
                .andExpect(status().isCreated());
        verify(applicationService,times(1)).addApplication(user);
        verifyNoMoreInteractions(applicationService);
    }
    private static String jsonToString(final Object obj) throws JsonProcessingException {
        String result;
        try {

            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            result = jsonContent;
        } catch (JsonProcessingException e) {
            result = "Json processing error";
        }
        return result;

    }

    @Test
    public void showAllRegistrations() throws Exception, JsonProcessingException {
        when(applicationService.getAllApplications("Ramya")).thenReturn(user);

        mockMvc.perform(get("/api/v2/syscop/appregistration/showApp/username/Ramya")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonToString(user)))
                .andExpect(status().isOk());
        verify(applicationService,times(2)).getAllApplications("Ramya");
        verifyNoMoreInteractions(applicationService);

    }

    @Test
    public void updateApplication() throws Exception, JsonProcessingException{
        when(applicationService.updateApplications(user)).thenReturn(user);

        mockMvc.perform(put("/api/v2/syscop/appregistration/updateApp")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonToString(user)))
                .andExpect(status().isOk());

        verify(applicationService,times(1)).updateApplications(user);
        verifyNoMoreInteractions(applicationService);
    }

    @Test
    public void deleteApplicationTest() throws Exception, JsonProcessingException{
        when(applicationService.deleteApplication(user)).thenReturn(user);


    }
}