package com.orbsec.organizationservice.service;

import com.orbsec.organizationservice.exceptions.MissingOrganizationException;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.repository.OrganizationRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;

    @Autowired
    public OrganizationService(OrganizationRepository repository) {
        this.repository = repository;
    }

    public Organization findById(String organizationId) throws MissingOrganizationException {
        Optional<Organization> opt = repository.findById(organizationId);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new MissingOrganizationException("No organization found for the provided id");
        }
    }

    public Organization create(Organization organization){
        organization.setId( UUID.randomUUID().toString());
        return repository.save(organization);
    }

    public void update(Organization organization){
        repository.save(organization);
    }

    public void delete(Organization organization){
        repository.deleteById(organization.getId());
    }


}
