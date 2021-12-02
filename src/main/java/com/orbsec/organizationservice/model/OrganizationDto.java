package com.orbsec.organizationservice.model;


import lombok.Data;

@Data
public class OrganizationDto {

    String id;

    String name;

    String contactName;

    String contactEmail;

    String contactPhone;
}
