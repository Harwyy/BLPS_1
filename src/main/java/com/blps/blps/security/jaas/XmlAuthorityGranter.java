package com.blps.blps.security.jaas;

import org.springframework.security.authentication.jaas.AuthorityGranter;
import com.blps.blps.security.jaas.XmlLoginModule.RolePrincipal;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

public class XmlAuthorityGranter implements AuthorityGranter {
    @Override
    public Set<String> grant(Principal principal) {
        if (principal instanceof RolePrincipal) {
            return Collections.singleton(((RolePrincipal) principal).getRole());
        }
        return Collections.emptySet();
    }
}