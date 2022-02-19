package com.orbsec.organizationservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDto {

    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;
}
