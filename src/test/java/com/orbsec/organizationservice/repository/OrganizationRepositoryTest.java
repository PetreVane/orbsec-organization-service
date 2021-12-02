package com.orbsec.organizationservice.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.orbsec.organizationservice.model.Organization;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {OrganizationRepository.class})
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.orbsec.organizationservice.model"})
@DataJpaTest
class OrganizationRepositoryTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void itShouldFindOrganizationsById() {
        // Given
        Organization organization = new Organization();
        organization.setId("42");
        organization.setContactName("Contact Name");
        organization.setName("Name");
        organization.setContactEmail("jane.doe@example.org");
        organization.setContactPhone("4105551212");

        Organization organization1 = new Organization();
        organization1.setId("42");
        organization1.setContactName("Contact Name");
        organization1.setName("Name");
        organization1.setContactEmail("jane.doe@example.org");
        organization1.setContactPhone("4105551212");
        this.organizationRepository.<Organization>save(organization);
        this.organizationRepository.<Organization>save(organization1);

        Organization organization2 = new Organization();
        organization2.setId("42");
        organization2.setContactName("Contact Name");
        organization2.setName("Name");
        organization2.setContactEmail("jane.doe@example.org");
        organization2.setContactPhone("4105551212");
        String id = this.organizationRepository.<Organization>save(organization2).getId();

        // When
        Optional<Organization> actualFindOrganizationsByIdResult = this.organizationRepository.findOrganizationsById(id);

        // Then
        assertThat(actualFindOrganizationsByIdResult.isPresent());
    }
}

