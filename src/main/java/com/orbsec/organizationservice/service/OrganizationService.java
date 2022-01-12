package com.orbsec.organizationservice.service;

import com.orbsec.organizationservice.exceptions.MissingOrganizationException;
import com.orbsec.organizationservice.model.LicenseDTO;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.repository.OrganizationRepository;
import com.orbsec.organizationservice.service.client.LicenseFeignClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;
    private final LicenseFeignClient licenseFeignClient;
    private ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);
    private static final String FAKE_DATA = "Unable to fetch data";

    @Autowired
    public OrganizationService(OrganizationRepository repository, LicenseFeignClient licenseFeignClient) {
        this.repository = repository;
        this.licenseFeignClient = licenseFeignClient;
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

//    Database calls
    @CircuitBreaker(name = "organizationDatabase", fallbackMethod = "findByIdFallback")
    @Retry(name ="retryOrganizationDatabase", fallbackMethod = "findByIdFallback")
    @Bulkhead(name = "bulkheadOrganizationDatabase",  fallbackMethod = "findByIdFallback")
    public OrganizationDto findById(String organizationId) throws MissingOrganizationException {
        Optional<Organization> opt = repository.findById(organizationId);
        if (opt.isPresent()) {
            return mapOrganization(opt.get());
        } else {
            throw new MissingOrganizationException("No organization found for the provided id");
        }
    }

    @CircuitBreaker(name = "organizationDatabase", fallbackMethod = "crudOrganizationFallback")
    @Retry(name ="retryOrganizationDatabase", fallbackMethod = "crudOrganizationFallback")
    @Bulkhead(name = "bulkheadOrganizationDatabase", fallbackMethod = "crudOrganizationFallback")
    public OrganizationDto create(OrganizationDto organizationDto) {
        Organization organization = mapDto(organizationDto);
        organization.setId( UUID.randomUUID().toString());
        Organization savedOrganization = repository.save(organization);
        return mapOrganization(savedOrganization);
    }

    @CircuitBreaker(name = "organizationDatabase", fallbackMethod = "crudOrganizationFallback")
    @Retry(name ="retryOrganizationDatabase", fallbackMethod = "crudOrganizationFallback")
    @Bulkhead(name = "bulkheadOrganizationDatabase", fallbackMethod = "crudOrganizationFallback")
    public void update(OrganizationDto organizationDto) {
        Organization organization = mapDto(organizationDto);
        repository.save(organization);
    }

    @CircuitBreaker(name = "organizationDatabase", fallbackMethod = "crudOrganizationFallback")
    @Retry(name ="retryOrganizationDatabase", fallbackMethod = "crudOrganizationFallback")
    @Bulkhead(name = "bulkheadOrganizationDatabase", fallbackMethod = "crudOrganizationFallback")
    public void delete(OrganizationDto organizationDto) {
        Organization organization = mapDto(organizationDto);
        repository.deleteById(organization.getId());
    }

    @CircuitBreaker(name = "organizationDatabase", fallbackMethod = "findAllFallback")
    @Retry(name ="retryOrganizationDatabase", fallbackMethod = "findAllFallback")
    @Bulkhead(name = "bulkheadOrganizationDatabase", fallbackMethod = "findAllFallback")
    public List<OrganizationDto> findAll() {
        var orgDtoList = new ArrayList<OrganizationDto>();
        var organizations = repository.findAll();
        organizations.forEach(organization -> orgDtoList.add(mapOrganization(organization)));
        return orgDtoList;
    }

//  Remote service calls
    @CircuitBreaker(name = "licensingService", fallbackMethod = "licensingServiceFallback")
    @Retry(name ="retryLicenseService", fallbackMethod = "licensingServiceFallback")
    @Bulkhead(name = "bulkheadLicensingService",  fallbackMethod = "licensingServiceFallback")
    public List<LicenseDTO> findAllLicensesForOrganization(String organizationId) {
        return licenseFeignClient.getAllLicensesForOrganization(organizationId);
    }

//    FallBacks
    @SuppressWarnings("unused")
    private OrganizationDto findByIdFallback(String organizationId, Throwable exception) {
        LOGGER.warn("Called findByIdFallback() ");
        return new OrganizationDto(organizationId, "Unable to fetch organization details", FAKE_DATA, FAKE_DATA, FAKE_DATA);
    }

    @SuppressWarnings("unused")
    private List<LicenseDTO> licensingServiceFallback(String organizationId, Throwable exception) {
        LOGGER.warn("Called licensingServiceFallback() ");
        List<LicenseDTO> dtoList = new ArrayList<>();
        LicenseDTO licenseDTO = new LicenseDTO( "Unable to fetch License details", FAKE_DATA, FAKE_DATA, FAKE_DATA,
                FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA);
        dtoList.add(licenseDTO);
        return dtoList;
    }

    @SuppressWarnings("unused")
    private List<Organization> findAllFallback(Throwable exception) {
        LOGGER.warn("Called @CircuitBreaker findAllFallback() ");
        List<Organization> organizationList = new ArrayList<>();
         Organization dummyOrganization = new Organization("Unable to fetch organization id", "Unable to fetch organization details", FAKE_DATA, FAKE_DATA, FAKE_DATA);
        organizationList.add(dummyOrganization);
        return organizationList;
    }

    @SuppressWarnings("unused")
    private OrganizationDto crudOrganizationFallback(Throwable exception) {
        LOGGER.warn("Called crudOrganizationFallback() ");
        return new OrganizationDto("Database service unavailable. Try again later!", FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA);
    }

}
