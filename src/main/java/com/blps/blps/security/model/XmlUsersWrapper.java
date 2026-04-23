package com.blps.blps.security.model;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class XmlUsersWrapper {
    @XmlElement(name = "user")
    private List<XmlUser> users;
}