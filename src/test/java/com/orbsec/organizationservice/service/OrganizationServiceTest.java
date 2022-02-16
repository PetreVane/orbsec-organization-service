package com.orbsec.organizationservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
import com.orbsec.organizationservice.model.LicenseDTO;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.repository.OrganizationRepository;
import com.orbsec.organizationservice.service.client.LicenseFeignClient;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {OrganizationService.class})
@ExtendWith(SpringExtension.class)
class OrganizationServiceTest {

    @MockBean
    private LicenseFeignClient licenseFeignClient;

    @MockBean
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationService organizationService;

    @Test
    void itShouldFindById() throws MissingOrganizationException {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("42");
        organization.setName("Name");
        Optional<Organization> ofResult = Optional.of(organization);
        when(this.organizationRepository.findById(any())).thenReturn(ofResult);
        String organizationId = "42";

        OrganizationDto actualFindByIdResult = this.organizationService.findById(organizationId);

        assertEquals("jane.doe@example.org", actualFindByIdResult.getContactEmail());
        assertEquals("Name", actualFindByIdResult.getName());
        assertEquals("42", actualFindByIdResult.getId());
        assertEquals("4105551212", actualFindByIdResult.getContactPhone());
        assertEquals("Contact Name", actualFindByIdResult.getContactName());
        verify(this.organizationRepository).findById(any());
    }

    @Test
    void itShouldThrowException() throws MissingOrganizationException {
        when(this.organizationRepository.findById(any())).thenReturn(Optional.empty());
        String organizationId = "42";

        assertThatThrownBy(() -> this.organizationService.findById(organizationId)).isInstanceOf(MissingOrganizationException.class);
        verify(this.organizationRepository).findById(any());
    }


    @Test
    void itShouldCreate() {
        Organization organization = new Organization();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("Name");
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactPhone("4105551212");
        when(this.organizationRepository.save(any())).thenReturn(organization);

        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId("42");
        organizationDto.setContactName("Contact Name");
        organizationDto.setName("Name");
        organizationDto.setContactEmail("jane.doe@example.org");
        organizationDto.setContactPhone("4105551212");

        OrganizationDto actualCreateResult = this.organizationService.create(organizationDto);

        assertEquals("jane.doe@example.org", actualCreateResult.getContactEmail());
        assertEquals("Name", actualCreateResult.getName());
        assertEquals("42", actualCreateResult.getId());
        assertEquals("4105551212", actualCreateResult.getContactPhone());
        assertEquals("Contact Name", actualCreateResult.getContactName());
        verify(this.organizationRepository).save(any());
    }

    @Test
    void itShouldUpdate() {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("42");
        organization.setName("Name");
        Optional<Organization> ofResult = Optional.of(organization);

        Organization organization1 = new Organization();
        organization1.setContactEmail("jane.doe@example.org");
        organization1.setContactName("Contact Name");
        organization1.setContactPhone("4105551212");
        organization1.setId("42");
        organization1.setName("Name");

        when(this.organizationRepository.save(any())).thenReturn(organization1);
        when(this.organizationRepository.findById(any())).thenReturn(ofResult);
        OrganizationDto actualUpdateResult = this.organizationService.update("42",
                new OrganizationDto("42", "Name", "Contact Name", "jane.doe@example.org", "4105551212"));

        assertEquals("jane.doe@example.org", actualUpdateResult.getContactEmail());
        assertEquals("Name", actualUpdateResult.getName());
        assertEquals("42", actualUpdateResult.getId());
        assertEquals("4105551212", actualUpdateResult.getContactPhone());
        assertEquals("Contact Name", actualUpdateResult.getContactName());
        verify(this.organizationRepository).save(any());
        verify(this.organizationRepository).findById(any());
    }

    @Test
    void itShouldThrowUpdateException() {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("42");
        organization.setName("Name");
        Optional<Organization> ofResult = Optional.of(organization);

        when(this.organizationRepository.save(any())).thenThrow(new MissingOrganizationException("An error occurred"));
        when(this.organizationRepository.findById(any())).thenReturn(ofResult);

        assertThrows(MissingOrganizationException.class, () -> this.organizationService.update("42",
                new OrganizationDto("42", "Name", "Contact Name", "jane.doe@example.org", "4105551212")));

        verify(this.organizationRepository).save(any());
        verify(this.organizationRepository).findById(any());
    }

    @Test
    void itShouldDelete() throws MissingOrganizationException {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("42");
        organization.setName("Name");
        Optional<Organization> ofResult = Optional.of(organization);

        when(this.organizationRepository.findById(any())).thenReturn(ofResult);

        this.organizationService.delete("42");
        verify(this.organizationRepository).findById(any());
        verify(this.organizationRepository).delete(any());
    }

    @Test
    void itShouldThrowDeleteException() throws MissingOrganizationException {
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("42");
        organization.setName("Name");
        Optional<Organization> ofResult = Optional.of(organization);

        doThrow(new MissingOrganizationException("An error occurred"))
                .when(this.organizationRepository)
                .delete(any());
        when(this.organizationRepository.findById(any())).thenReturn(ofResult);

        assertThrows(MissingOrganizationException.class, () -> this.organizationService.delete("42"));
        verify(this.organizationRepository).findById(any());
        verify(this.organizationRepository).delete(any());
    }

    @Test
    void itShouldNotDelete() throws MissingOrganizationException {
        doNothing().when(this.organizationRepository).delete(any());

        when(this.organizationRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(MissingOrganizationException.class, () -> this.organizationService.delete("42"));
        verify(this.organizationRepository).findById(any());
    }


    @Test
    void itShouldFindAll() {
        Iterable<Organization> iterable = (Iterable<Organization>) mock(Iterable.class);
        doNothing().when(iterable).forEach(any());
        when(this.organizationRepository.findAll()).thenReturn(iterable);

        List<OrganizationDto> actualFindAllResult = this.organizationService.findAll();

        assertTrue(actualFindAllResult.isEmpty());
        verify(this.organizationRepository).findAll();
        verify(iterable).forEach(any());
    }

    @Test
    void itShouldThrowFindAllException() {
        when(this.organizationRepository.findAll())
                .thenThrow(new MissingOrganizationException("An error occurred"));

        assertThrows(MissingOrganizationException.class, () -> this.organizationService.findAll());
        verify(this.organizationRepository).findAll();
    }

    @Test
    void itShouldFindAllLicensesForOrganization() {
        ArrayList<LicenseDTO> licenseDTOList = new ArrayList<>();
        when(this.licenseFeignClient.getAllLicensesForOrganization(any(), any())).thenReturn(licenseDTOList);
        String organizationId = "5554552";
        String authHeader = "random auth header";

        List<LicenseDTO> actualFindAllLicensesForOrganizationResult = this.organizationService
                .findAllLicensesForOrganization(authHeader, organizationId);

        assertSame(licenseDTOList, actualFindAllLicensesForOrganizationResult);
        assertTrue(actualFindAllLicensesForOrganizationResult.isEmpty());
        verify(this.licenseFeignClient).getAllLicensesForOrganization(any(), any());
    }

    @Test
    void itShouldThrowMissingOrganizationException() {
        when(this.licenseFeignClient.getAllLicensesForOrganization(any(), any()))
                .thenThrow(new MissingOrganizationException("Missing organization"));
        String organizationId = "555552";
        String authHeader = "random auth header";

        assertThrows(MissingOrganizationException.class,
                () -> this.organizationService.findAllLicensesForOrganization(authHeader, organizationId));
        verify(this.licenseFeignClient).getAllLicensesForOrganization(any(), any());
    }
}

