package com.orbsec.organizationservice.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbsec.organizationservice.exceptions.InvalidOrganizationRecord;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.service.OrganizationService;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {OrganizationController.class})
@ExtendWith(SpringExtension.class)
class OrganizationControllerTest {
    @Autowired
    private OrganizationController organizationController;

    @MockBean
    private OrganizationService organizationService;

    @Test
    void itShouldGetAllOrganizations() throws Exception {
        when(this.organizationService.findAll()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/organization/all");
        MockMvcBuilders.standaloneSetup(this.organizationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void itShouldDeleteOrganization() throws Exception {
        when(this.organizationService.delete(any())).thenReturn("Delete");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/v1/organization/{organizationId}", "12342");
        MockMvcBuilders.standaloneSetup(this.organizationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Delete"));
    }

    @Test
    void itShouldFetchLicensesForOrganization() throws Exception {
        when(this.organizationService.findById(any()))
                .thenReturn(new OrganizationDto("12342", "Name", "Contact Name", "jane.doe@example.org", "4105551212"));

        when(this.organizationService.findAllLicensesForOrganization(any(), any()))
                .thenThrow(new InvalidOrganizationRecord("Invalid organization record"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/organization/license/{organizationId}", "", "Uri Vars")
                .header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");

        MockMvcBuilders.standaloneSetup(this.organizationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"id\":\"12342\",\"name\":\"Name\",\"contactName\":\"Contact Name\",\"contactEmail\":\"jane.doe@example.org\",\"contactPhone"
                                        + "\":\"4105551212\"}"));
    }

    @Test
    void itShouldGetOrganization() throws Exception {
        when(this.organizationService.findById(any()))
                .thenReturn(new OrganizationDto("12342", "Name", "Contact Name", "jane.doe@example.org", "4105551212"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/organization/{organizationId}",
                "12342");
        MockMvcBuilders.standaloneSetup(this.organizationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":\"12342\",\"name\":\"Name\",\"contactName\":\"Contact Name\",\"contactEmail\":\"jane.doe@example.org\",\"contactPhone"
                                        + "\":\"4105551212\"}"));
    }


    @Test
    void itShouldSaveOrganization() throws Exception {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setContactEmail("jane.doe@example.org");
        organizationDto.setContactName("Contact Name");
        organizationDto.setContactPhone("4105551212");
        organizationDto.setId("12342");
        organizationDto.setName("Name");
        String content = (new ObjectMapper()).writeValueAsString(organizationDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/organization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.organizationController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(405));
    }

    @Test
    void itShouldNotSaveOrganization() throws Exception {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("12342");
        organization.setName("Name");
        String content = (new ObjectMapper()).writeValueAsString(organization);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/organization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.organizationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void itShouldUpdateOrganization() throws Exception {
        when(this.organizationService.update(any(), any()))
                .thenReturn(new OrganizationDto("12342", "Name", "Contact Name", "jane.doe@example.org", "4105551212"));

        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setContactEmail("jane.doe@example.org");
        organizationDto.setContactName("Contact Name");
        organizationDto.setContactPhone("4105551212");
        organizationDto.setId("12342");
        organizationDto.setName("Name");
        String content = (new ObjectMapper()).writeValueAsString(organizationDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/v1/organization/{organizationId}", "12342")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(this.organizationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":\"12342\",\"name\":\"Name\",\"contactName\":\"Contact Name\",\"contactEmail\":\"jane.doe@example.org\",\"contactPhone"
                                        + "\":\"4105551212\"}"));
    }
}

