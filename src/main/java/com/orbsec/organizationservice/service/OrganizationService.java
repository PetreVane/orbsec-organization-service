package com.orbsec.organizationservice.service;

import com.orbsec.organizationservice.avro.model.ChangeType;
import com.orbsec.organizationservice.exceptions.MissingOrganizationException;
import com.orbsec.organizationservice.exceptions.UnauthorizedException;
import com.orbsec.organizationservice.kafka.EventProducer;
import com.orbsec.organizationservice.model.LicenseDTO;
import com.orbsec.organizationservice.model.Organization;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.repository.OrganizationRepository;
import com.orbsec.organizationservice.service.client.LicenseFeignClient;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OrganizationService {

    private final OrganizationRepository repository;
    private final LicenseFeignClient licenseFeignClient;
    private ModelMapper modelMapper;
    private static final String FAKE_DATA = "Unable to fetch data";
    private final EventProducer eventProducer;

    @Autowired
    public OrganizationService(OrganizationRepository repository, LicenseFeignClient licenseFeignClient, EventProducer eventProducer) {
        this.repository = repository;
        this.licenseFeignClient = licenseFeignClient;
        this.eventProducer = eventProducer;
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
        log.info("Created new record with organization id {}", organization.getId());
        eventProducer.publishNewEvent(organization.getId(), ChangeType.CREATION, String.format("A new Organization with id %s has been saved to the database.", organization.getId()));
        return mapOrganization(savedOrganization);
    }

    @CircuitBreaker(name = "organizationDatabase", fallbackMethod = "updateOrganizationFallback")
    @Retry(name ="retryOrganizationDatabase", fallbackMethod = "updateOrganizationFallback")
    @Bulkhead(name = "bulkheadOrganizationDatabase", fallbackMethod = "updateOrganizationFallback")
    public OrganizationDto update(String organizationId, OrganizationDto updateDto) {
        var existingDto = findById(organizationId);
       existingDto.setName(updateDto.getName());
       existingDto.setContactName(updateDto.getContactName());
       existingDto.setContactEmail(updateDto.getContactEmail());
       existingDto.setContactPhone(updateDto.getContactPhone());

        Organization updatedRecord = mapDto(existingDto);
        repository.save(updatedRecord);
        log.info("Updated organization with id {}", updatedRecord.getId());
        eventProducer.publishNewEvent(updatedRecord.getId(), ChangeType.UPDATE, String.format("Organization with id %s has been updated", updatedRecord.getId()));
        return existingDto;
    }

    @CircuitBreaker(name = "organizationDatabase", fallbackMethod = "deleteOrganizationFallback")
    @Retry(name ="retryOrganizationDatabase", fallbackMethod = "deleteOrganizationFallback")
    @Bulkhead(name = "bulkheadOrganizationDatabase", fallbackMethod = "deleteOrganizationFallback")
    public String delete(String organizationId) throws MissingOrganizationException {
        String message;
        var existingOrganization = repository.findById(organizationId);
        if (existingOrganization.isPresent()) {
            repository.delete(existingOrganization.get());
            message = String.format("Organization with id %s has been deleted", organizationId);
            log.info("Organization with id {} has been deleted", organizationId);
            eventProducer.publishNewEvent(organizationId, ChangeType.DELETION, String.format("Organization with id %s has been deleted", organizationId));
        } else {
            log.error("Failed to delete organization with id {}", organizationId);
            throw new MissingOrganizationException(String.format("No organization found for this id: %s", organizationId));
        }
        return message;
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
    public List<LicenseDTO> findAllLicensesForOrganization(String authToken, String organizationId) throws UnauthorizedException {
        return licenseFeignClient.getAllLicensesForOrganization(authToken, organizationId);
    }

//    FallBacks
    @SuppressWarnings("unused")
    private OrganizationDto findByIdFallback(String organizationId, Throwable exception) {
        if (exception instanceof MissingOrganizationException) {
            throw new MissingOrganizationException("No organization found for the provided id");
        }
        log.warn("CircuitBreaker: called findByIdFallback() methods ");
        return new OrganizationDto(organizationId, "Unable to fetch organization details", FAKE_DATA, FAKE_DATA, FAKE_DATA);
    }

    @SuppressWarnings("unused")
    private List<LicenseDTO> licensingServiceFallback(String authHeader,String organizationId, Throwable exception) {
        log.warn("CircuitBreaker: called licensingServiceFallback() with authHeader {}", authHeader);
        if (exception instanceof FeignException.Unauthorized) {
            log.error("Call to remote service is unauthorized. Do you have a valid Authorization Code?");
            throw new UnauthorizedException(exception.getMessage());
        }
        List<LicenseDTO> dtoList = new ArrayList<>();
        LicenseDTO licenseDTO = new LicenseDTO(
                "Unable to fetch License details", FAKE_DATA, FAKE_DATA, FAKE_DATA,
                FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA);
        dtoList.add(licenseDTO);
        return dtoList;
    }

    @SuppressWarnings("unused")
    private List<Organization> findAllFallback(Throwable exception) {
        log.warn("CircuitBreaker: called findAllFallback() method ");
        List<Organization> organizationList = new ArrayList<>();
         Organization dummyOrganization = new Organization("Unable to fetch organization id", "Unable to fetch organization details", FAKE_DATA, FAKE_DATA, FAKE_DATA);
        organizationList.add(dummyOrganization);
        return organizationList;
    }

    @SuppressWarnings("unused")
    private OrganizationDto crudOrganizationFallback(Throwable exception) {
        log.warn("CircuitBreaker: called  crudOrganizationFallback() method ");
        return new OrganizationDto("Database service unavailable. Try again later!", FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA);
    }

    @SuppressWarnings("unused")
    private OrganizationDto updateOrganizationFallback(String organizationID, OrganizationDto dto,Throwable exception) {
        log.warn("CircuitBreaker: called updateOrganizationFallback() method ");
        return new OrganizationDto("Database service unavailable. Try again later!", FAKE_DATA, FAKE_DATA, FAKE_DATA, FAKE_DATA);
    }

    @SuppressWarnings("unused")
    private String deleteOrganizationFallback(String organizationID,Throwable exception) {
        log.warn("CircuitBreaker: called deleteOrganizationFallback() method ");
        return "Error while processing your request: database service might be unavailable. Try again later!";
    }

}
