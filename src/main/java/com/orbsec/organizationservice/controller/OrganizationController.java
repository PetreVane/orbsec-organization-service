package com.orbsec.organizationservice.controller;

import com.orbsec.organizationservice.model.LicenseDTO;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/organization")
public class OrganizationController {

    private final OrganizationService service;

    @Autowired
    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<OrganizationDto>> getAllOrganizations() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value="/{organizationId}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable("organizationId") String organizationId) {
        return ResponseEntity.ok(service.findById(organizationId));
    }

    @PutMapping(value="/{organizationId}")
    public void updateOrganization(@PathVariable("organizationId") String id, @RequestBody OrganizationDto organizationDto) {
        service.update(organizationDto);
    }

    @PostMapping
    public ResponseEntity<OrganizationDto> saveOrganization(@RequestBody OrganizationDto organizationDto) {
        return ResponseEntity.ok(service.create(organizationDto));
    }

    @DeleteMapping(value="/{organizationId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteOrganization( @PathVariable("organizationId") String id,  @RequestBody OrganizationDto organizationDto) {
        service.delete(organizationDto);
    }

    @GetMapping(value = "license/{organizationId}")
    public ResponseEntity<List<LicenseDTO>> fetchLicensesForOrganization(@PathVariable ("organizationId") String organizationId) {
        return ResponseEntity.ok(service.findAllLicensesForOrganization(organizationId));
    }

}
