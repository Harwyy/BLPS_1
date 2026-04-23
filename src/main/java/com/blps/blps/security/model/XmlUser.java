package com.blps.blps.security.model;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class XmlUser {
    private String username;
    private String password;
    private String role;
    private Long referenceId;
}