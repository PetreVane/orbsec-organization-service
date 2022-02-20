package com.orbsec.organizationservice.repository;

import com.orbsec.organizationservice.model.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization, String> {
    Optional<Organization> findOrganizationsById(String id);
}
