package com.orbsec.organizationservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDto {

    String id;
    @NotNull
    String name;
    @NotNull
    String contactName;
    @Email
    String contactEmail;
    @NotNull
    String contactPhone;
}
