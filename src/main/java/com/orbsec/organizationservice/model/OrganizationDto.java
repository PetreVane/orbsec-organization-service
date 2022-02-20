package com.orbsec.organizationservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDto {

    String id;
    String name;
    String contactName;
    @Email
    String contactEmail;
    String contactPhone;
}
