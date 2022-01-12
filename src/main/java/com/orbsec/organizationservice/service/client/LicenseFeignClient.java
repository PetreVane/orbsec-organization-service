package com.orbsec.organizationservice.service.client;

import com.orbsec.organizationservice.model.LicenseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "licensing-service/api/v1/license")
public interface LicenseFeignClient {

    @GetMapping(value = "/organization/{organizationId}")
    List<LicenseDTO> getAllLicensesForOrganization(@PathVariable("organizationId") String organizationId);
}
