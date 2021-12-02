package com.orbsec.organizationservice.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.service.OrganizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
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
    void itShouldNotDeleteOrganization() throws Exception {
        // Given
        Organization organization = new Organization();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("Name");
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactPhone("4105551212");
        String content = (new ObjectMapper()).writeValueAsString(organization);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/v1/organization/{organizationId}", "42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(this.organizationController).build();

        // When
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Then
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(500));
    }

    @Test
    void itShouldGetOrganization() throws Exception {
        // Given
        Organization organization = new Organization();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("Name");
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactPhone("4105551212");
        when(this.organizationService.findById(any())).thenReturn(organization);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/organization/{organizationId}",
                "42");
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(this.organizationController).build();

        // When
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Then
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":\"42\",\"name\":\"Name\",\"contactName\":\"Contact Name\",\"contactEmail\":\"jane.doe@example.org\",\"contactPhone"
                                        + "\":\"4105551212\"}"));
    }

    @Test
    void itShouldSaveOrganization() throws Exception {
        // Given
        Organization organization = new Organization();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("Name");
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactPhone("4105551212");
        String content = (new ObjectMapper()).writeValueAsString(organization);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/organization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(this.organizationController).build();

        // When
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Then
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(405));
    }

    @Test
    void itShouldUpdateOrganization() throws Exception {
        // Given
        doNothing().when(this.organizationService).update(any());

        Organization organization = new Organization();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("Name");
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactPhone("4105551212");
        String content = (new ObjectMapper()).writeValueAsString(organization);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/v1/organization/{organizationId}", "42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(this.organizationController).build();

        // When
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Then
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isOk());
    }
}

