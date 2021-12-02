package com.orbsec.organizationservice.service;

import com.orbsec.organizationservice.exceptions.MissingOrganizationException;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.repository.OrganizationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;
    private ModelMapper modelMapper;

    @Autowired
    public OrganizationService(OrganizationRepository repository) {
        this.repository = repository;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    private OrganizationDto mapOrganization(Organization organization) {
        return modelMapper.map(organization, OrganizationDto.class);
    }

    private Organization mapDto(OrganizationDto organizationDto) {
        return modelMapper.map(organizationDto, Organization.class);
    }

    public Organization findById(String organizationId) throws MissingOrganizationException {
        Optional<Organization> opt = repository.findById(organizationId);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new MissingOrganizationException("No organization found for the provided id");
        }
    }

    public OrganizationDto create(OrganizationDto organizationDto) {
        Organization organization = mapDto(organizationDto);
        organization.setId( UUID.randomUUID().toString());
        Organization savedOrganization = repository.save(organization);
        return mapOrganization(savedOrganization);
    }

    public void update(OrganizationDto organizationDto) {
        Organization organization = mapDto(organizationDto);
        repository.save(organization);
    }

    public void delete(OrganizationDto organizationDto) {
        Organization organization = mapDto(organizationDto);
        repository.deleteById(organization.getId());
    }


}
