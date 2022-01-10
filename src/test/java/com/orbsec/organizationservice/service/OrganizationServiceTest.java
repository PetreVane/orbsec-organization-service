package com.orbsec.organizationservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
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
        // Arrange
        Organization organization = new Organization();
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactName("Contact Name");
        organization.setContactPhone("4105551212");
        organization.setId("42");
        organization.setName("Name");
        Optional<Organization> ofResult = Optional.of(organization);
        when(this.organizationRepository.findById(any())).thenReturn(ofResult);
        String organizationId = "42";

        // Act
        OrganizationDto actualFindByIdResult = this.organizationService.findById(organizationId);

        // Assert
        assertEquals("jane.doe@example.org", actualFindByIdResult.getContactEmail());
        assertEquals("Name", actualFindByIdResult.getName());
        assertEquals("42", actualFindByIdResult.getId());
        assertEquals("4105551212", actualFindByIdResult.getContactPhone());
        assertEquals("Contact Name", actualFindByIdResult.getContactName());
        verify(this.organizationRepository).findById(any());
    }

    @Test
    void itShouldThrowException() throws MissingOrganizationException {
        // Given
        when(this.organizationRepository.findById(any())).thenReturn(Optional.empty());
        String organizationId = "42";

        // When / Then
        assertThatThrownBy(() -> this.organizationService.findById(organizationId)).isInstanceOf(MissingOrganizationException.class);
        verify(this.organizationRepository).findById(any());
    }


    @Test
    void itShouldCreate() {
        // Given
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

        // When
        OrganizationDto actualCreateResult = this.organizationService.create(organizationDto);

        // Then
        assertEquals("jane.doe@example.org", actualCreateResult.getContactEmail());
        assertEquals("Name", actualCreateResult.getName());
        assertEquals("42", actualCreateResult.getId());
        assertEquals("4105551212", actualCreateResult.getContactPhone());
        assertEquals("Contact Name", actualCreateResult.getContactName());
        verify(this.organizationRepository).save(any());
    }


    @Test
    void itShouldUpdate() {
        // Given
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

        // When
        this.organizationService.update(organizationDto);

        // Then
        verify(this.organizationRepository).save(any());
    }

    @Test
    void itShouldThrowException2() {
        // Given
        when(this.organizationRepository.save(any()))
                .thenThrow(new MissingOrganizationException("An error occurred"));

        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId("42");
        organizationDto.setContactName("Contact Name");
        organizationDto.setName("Name");
        organizationDto.setContactEmail("jane.doe@example.org");
        organizationDto.setContactPhone("4105551212");

        // Then
        assertThrows(MissingOrganizationException.class, () -> this.organizationService.update(organizationDto));
        verify(this.organizationRepository).save(any());
    }

    @Test
    void itShouldDelete() {
        // Given
        doNothing().when(this.organizationRepository).deleteById(any());

        OrganizationDto organization = new OrganizationDto();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("Name");
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactPhone("4105551212");

        // When
        this.organizationService.delete(organization);

        // Then
        verify(this.organizationRepository).deleteById(any());
    }

    @Test
    void itShouldFindAll() {
        // Given
        Iterable<Organization> iterable = (Iterable<Organization>) mock(Iterable.class);
        doNothing().when(iterable).forEach(any());
        when(this.organizationRepository.findAll()).thenReturn(iterable);

        // When
        List<OrganizationDto> actualFindAllResult = this.organizationService.findAll();

        // Then
        assertTrue(actualFindAllResult.isEmpty());
        verify(this.organizationRepository).findAll();
        verify(iterable).forEach(any());
    }

    @Test
    void itShouldThrowException3() {
        // Given and When
        when(this.organizationRepository.findAll()).thenThrow(new MissingOrganizationException("An error occurred"));

        // Then
        assertThrows(MissingOrganizationException.class, () -> this.organizationService.findAll());
        verify(this.organizationRepository).findAll();
    }

    @Test
    void itShouldFindAllLicensesForOrganization() {
        // When
        ArrayList<LicenseDTO> licenseDTOList = new ArrayList<>();
        when(this.licenseFeignClient.getAllLicenses(any())).thenReturn(licenseDTOList);
        String organizationId = "5554552";

        // Given
        List<LicenseDTO> actualFindAllLicensesForOrganizationResult = this.organizationService
                .findAllLicensesForOrganization(organizationId);

        // Then
        assertSame(licenseDTOList, actualFindAllLicensesForOrganizationResult);
        assertTrue(actualFindAllLicensesForOrganizationResult.isEmpty());
        verify(this.licenseFeignClient).getAllLicenses(any());
    }

    @Test
    void itShouldThrowMissingOrganizationException() {
        // Arrange
        when(this.licenseFeignClient.getAllLicenses(any()))
                .thenThrow(new MissingOrganizationException("Missing organization"));
        String organizationId = "555552";

        // Act and Assert
        assertThrows(MissingOrganizationException.class,
                () -> this.organizationService.findAllLicensesForOrganization(organizationId));
        verify(this.licenseFeignClient).getAllLicenses(any());
    }
}

