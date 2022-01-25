package com.orbsec.organizationservice.controller;

import com.orbsec.organizationservice.model.LicenseDTO;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/organization")
public class OrganizationController {

    private final OrganizationService service;

    @Autowired
    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @RolesAllowed("ADMIN")
    @GetMapping(value = "/all")
    public ResponseEntity<List<OrganizationDto>> getAllOrganizations() {
        return ResponseEntity.ok(service.findAll());
    }

    @RolesAllowed({ "ADMIN", "USER" })
    @GetMapping(value="/{organizationId}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable("organizationId") String organizationId) {
        return ResponseEntity.ok(service.findById(organizationId));
    }

    @RolesAllowed("ADMIN")
    @PutMapping(value="/{organizationId}")
    public void updateOrganization(@PathVariable("organizationId") String id, @RequestBody OrganizationDto organizationDto) {
        service.update(organizationDto);
    }

    @RolesAllowed({ "ADMIN", "USER" })
    @PostMapping
    public ResponseEntity<OrganizationDto> saveOrganization(@RequestBody OrganizationDto organizationDto) {
        return ResponseEntity.ok(service.create(organizationDto));
    }

    @RolesAllowed("ADMIN")
    @DeleteMapping(value="/{organizationId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteOrganization( @PathVariable("organizationId") String id,  @RequestBody OrganizationDto organizationDto) {
        service.delete(organizationDto);
    }

    @GetMapping(value = "license/{organizationId}")
    public ResponseEntity<List<LicenseDTO>> fetchLicensesForOrganization(@RequestHeader(value = "Authorization") String authToken, @PathVariable ("organizationId") String organizationId) {
        return ResponseEntity.ok(service.findAllLicensesForOrganization(authToken, organizationId));
    }

}
