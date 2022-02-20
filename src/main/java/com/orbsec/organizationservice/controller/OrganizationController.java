package com.orbsec.organizationservice.controller;

import com.orbsec.organizationservice.exceptions.InvalidOrganizationRecord;
import com.orbsec.organizationservice.model.LicenseDTO;
import com.orbsec.organizationservice.model.OrganizationDto;
import com.orbsec.organizationservice.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/organization")
public class OrganizationController {

    private final OrganizationService service;

    @Autowired
    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    //TODO: Reactivate commented-out security rule
//    @RolesAllowed("ADMIN")
    @GetMapping(value = "/all")
    public ResponseEntity<List<OrganizationDto>> getAllOrganizations() {
        return ResponseEntity.ok(service.findAll());
    }

    //TODO: Reactivate commented-out security rule
//    @RolesAllowed({ "ADMIN", "USER" })
    @GetMapping(value="/{organizationId}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable("organizationId") String organizationId) {
        return ResponseEntity.ok(service.findById(organizationId));
    }

    //TODO: Reactivate commented-out security rule
//    @RolesAllowed("ADMIN")
    @PutMapping(value="/{organizationId}")
    public ResponseEntity<OrganizationDto> updateOrganization(@PathVariable("organizationId") String id, @Valid @RequestBody OrganizationDto organizationDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidOrganizationRecord("An error occured while trying to update organization record: invalid email address");
        }
        return ResponseEntity.ok(service.update(id, organizationDto));
    }

    //TODO: Reactivate commented-out security rule
//    @RolesAllowed({ "ADMIN", "USER" })
    @PostMapping
    public ResponseEntity<OrganizationDto> saveOrganization(@Valid @RequestBody OrganizationDto organizationDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidOrganizationRecord("An error occured while trying to save organization record: invalid email address");
        }
        return ResponseEntity.ok(service.create(organizationDto));
    }

    //TODO: Reactivate commented-out security rule
//    @RolesAllowed("ADMIN")
    @DeleteMapping(value="/{organizationId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> deleteOrganization( @PathVariable("organizationId") String id) {
        return ResponseEntity.ok(service.delete(id));
    }

    @GetMapping(value = "license/{organizationId}")
    public ResponseEntity<List<LicenseDTO>> fetchLicensesForOrganization(@RequestHeader(value = "Authorization") String authToken, @PathVariable ("organizationId") String organizationId) {
        return ResponseEntity.ok(service.findAllLicensesForOrganization(authToken, organizationId));
    }

}
