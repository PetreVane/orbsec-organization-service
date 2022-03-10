package com.orbsec.organizationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orbsec.organizationservice.exceptions.MissingOrganizationException;
import com.orbsec.organizationservice.exceptions.UnauthorizedException;
import com.orbsec.organizationservice.kafka.EventProducer;
import com.orbsec.organizationservice.model.LicenseDTO;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.repository.OrganizationRepository;
import com.orbsec.organizationservice.service.client.LicenseFeignClient;
import feign.FeignException;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {OrganizationService.class})
@ExtendWith(SpringExtension.class)
class OrganizationServiceTest {
    @MockBean
    private EventProducer eventProducer;

    @MockBean
    private LicenseFeignClient licenseFeignClient;

    @MockBean
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationService organizationService;

    @Test
    @DisplayName("It should find Organization by Id")
    void itShouldFindById() throws MissingOrganizationException {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("12342");
        organization.setName("Name");
        Optional<Organization> result = Optional.of(organization);

        when(this.organizationRepository.findById(any())).thenReturn(result);

        OrganizationDto actualFindByIdResult = this.organizationService.findById("12342");
        assertEquals("jane.doe@example.org", actualFindByIdResult.getContactEmail());
        assertEquals("Name", actualFindByIdResult.getName());
        assertEquals("12342", actualFindByIdResult.getId());
        assertEquals("4105551212", actualFindByIdResult.getContactPhone());
        assertEquals("Contact Name", actualFindByIdResult.getContactName());
        verify(this.organizationRepository).findById(any());
    }

    @Test
    @DisplayName("It should throw MissingOrganizationException")
    void itShouldThrowMissingOrgException() throws MissingOrganizationException {
        when(this.organizationRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(MissingOrganizationException.class, () -> this.organizationService.findById("12342"));
        verify(this.organizationRepository).findById(any());
    }

    @Test
    @DisplayName("It should create new Organization record")
    void itShouldCreate() {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("12342");
        organization.setName("Name");

        when(this.organizationRepository.save(any())).thenReturn(organization);
        doNothing().when(this.eventProducer)
                .publishNewEvent(any(), any(), any());

        OrganizationDto actualCreatedResult = this.organizationService
                .create(new OrganizationDto("12342", "Name", "Contact Name", "jane.doe@example.org", "4105551212"));
        assertEquals("jane.doe@example.org", actualCreatedResult.getContactEmail());
        assertEquals("Name", actualCreatedResult.getName());
        assertEquals("12342", actualCreatedResult.getId());
        assertEquals("4105551212", actualCreatedResult.getContactPhone());
        assertEquals("Contact Name", actualCreatedResult.getContactName());
        verify(this.organizationRepository).save(any());
        verify(this.eventProducer).publishNewEvent(any(),
                any(), any());
    }


    @Test
    void itShouldUpdate() {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("12342");
        organization.setName("Name");
        Optional<Organization> optionalResult = Optional.of(organization);

        Organization organization1 = new Organization();
        organization1.setContactEmail("jane.doe@example.org");
        organization1.setContactName("Contact Name");
        organization1.setContactPhone("4105551212");
        organization1.setId("12342");
        organization1.setName("Name");

        when(this.organizationRepository.save(any())).thenReturn(organization1);
        when(this.organizationRepository.findById(any())).thenReturn(optionalResult);
        doNothing().when(this.eventProducer).publishNewEvent(any(), any(), any());

        OrganizationDto actualUpdateResult = this.organizationService.update("12342",
                new OrganizationDto("12342", "Name", "Contact Name", "jane.doe@example.org", "4105551212"));

        assertEquals("jane.doe@example.org", actualUpdateResult.getContactEmail());
        assertEquals("Name", actualUpdateResult.getName());
        assertEquals("12342", actualUpdateResult.getId());
        assertEquals("4105551212", actualUpdateResult.getContactPhone());
        assertEquals("Contact Name", actualUpdateResult.getContactName());

        verify(this.organizationRepository).save(any());
        verify(this.organizationRepository).findById(any());
        verify(this.eventProducer).publishNewEvent(any(), any(), any());
    }


    @Test
    void itShouldDelete() throws MissingOrganizationException {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("12342");
        organization.setName("Name");
        Optional<Organization> optionalResult = Optional.of(organization);

        doNothing().when(this.organizationRepository).delete(any());
        when(this.organizationRepository.findById(any())).thenReturn(optionalResult);
        doNothing().when(this.eventProducer).publishNewEvent(any(), any(), any());

        assertEquals("Organization with id 12342 has been deleted", this.organizationService.delete("12342"));
        verify(this.organizationRepository).findById(any());
        verify(this.organizationRepository).delete(any());
        verify(this.eventProducer).publishNewEvent(any(), any(), any());
    }

    @Test
    void itShouldNotDelete() throws MissingOrganizationException {
        when(this.organizationRepository.findById(any())).thenThrow(new MissingOrganizationException("No organization found for this id"));
        doNothing().when(this.eventProducer).publishNewEvent(any(), any(), any());
        assertThrows(MissingOrganizationException.class, () -> this.organizationService.delete("0000000"));
    }


    @Test
    void itShouldFindAll() {
        Iterable<Organization> iterable = (Iterable<Organization>) mock(Iterable.class);
        doNothing().when(iterable).forEach(any());
        when(this.organizationRepository.findAll()).thenReturn(iterable);
        assertTrue(this.organizationService.findAll().isEmpty());
        verify(this.organizationRepository).findAll();
        verify(iterable).forEach(any());
    }

    @Test
    void itShouldFindAllLicensesForOrganization() throws UnauthorizedException {
        ArrayList<LicenseDTO> licenseDTOList = new ArrayList<>();
        when(this.licenseFeignClient.getAllLicensesForOrganization(any(), any())).thenReturn(licenseDTOList);
        List<LicenseDTO> actualFindAllLicensesForOrganizationResult = this.organizationService
                .findAllLicensesForOrganization("ABC123", "12342");
        assertSame(licenseDTOList, actualFindAllLicensesForOrganizationResult);
        assertTrue(actualFindAllLicensesForOrganizationResult.isEmpty());
        verify(this.licenseFeignClient).getAllLicensesForOrganization(any(), any());
    }

    @Test
    void itShouldNotFindAllLicensesForOrganization() throws UnauthorizedException {
        when(this.licenseFeignClient.getAllLicensesForOrganization(any(), any()))
                .thenThrow(new MissingOrganizationException("An error occurred"));
        assertThrows(MissingOrganizationException.class,
                () -> this.organizationService.findAllLicensesForOrganization("ABC123", "42"));
        verify(this.licenseFeignClient).getAllLicensesForOrganization(any(), any());
    }
}

