package com.orbsec.organizationservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orbsec.organizationservice.exceptions.MissingOrganizationException;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.repository.OrganizationRepository;

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
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationService organizationService;

    @Test
    void itShouldFindById() throws MissingOrganizationException {
        // Given
        Organization organization = new Organization();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("Name");
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactPhone("4105551212");
        Optional<Organization> ofResult = Optional.of(organization);
        when(this.organizationRepository.findById(any())).thenReturn(ofResult);
        String organizationId = "42";

        // When
        Organization actualFindByIdResult = this.organizationService.findById(organizationId);

        // Then
        assertThat(actualFindByIdResult).isSameAs(organization);
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
    void itShouldCreate2() {
        // Given
        Organization organization = new Organization();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("42Name");
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
        assertEquals("42Name", actualCreateResult.getName());
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
    void itShouldUpdate2() {
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
}

