package com.orbsec.organizationservice.repository;

import com.orbsec.organizationservice.model.Organization;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrganizationRepository extends CrudRepository<Organization, String> {
    Optional<Organization> findOrganizationsById(String id);
}
