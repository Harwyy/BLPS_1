package com.blps.blps.security.jaas;

import com.blps.blps.security.model.XmlUser;
import com.blps.blps.security.service.XmlUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class XmlLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> options;
    private static XmlUserDetailsService userDetailsService;
    private static PasswordEncoder passwordEncoder;
    private String username;
    private List<Principal> principals = new ArrayList<>();

    @Autowired
    public void setUserDetailsService(XmlUserDetailsService service) {
        XmlLoginModule.userDetailsService = service;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder encoder) {
        XmlLoginModule.passwordEncoder = encoder;
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.options = options;
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("No CallbackHandler available");
        }

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("Username: ");
        callbacks[1] = new PasswordCallback("Password: ", false);

        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback) callbacks[0]).getName();
            String password = new String(((PasswordCallback) callbacks[1]).getPassword());

            XmlUser xmlUser = loadUser(username);
            if (xmlUser == null) {
                throw new LoginException("User not found: " + username);
            }

            if (!passwordEncoder.matches(password, xmlUser.getPassword())) {
                throw new LoginException("Invalid password");
            }

            principals.add(new UserPrincipal(username));
            principals.add(new RolePrincipal(xmlUser.getRole()));
            principals.add(new ReferenceIdPrincipal(xmlUser.getReferenceId()));

            return true;
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Callback handling failed: " + e.getMessage());
        }
    }

    private XmlUser loadUser(String username) {
        try {
            return userDetailsService.getAllUsers().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean commit() throws LoginException {
        if (username == null) {
            return false;
        }
        subject.getPrincipals().addAll(principals);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        username = null;
        principals.clear();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        return true;
    }

    public static class UserPrincipal implements Principal {
        private final String name;
        public UserPrincipal(String name) { this.name = name; }
        @Override public String getName() { return name; }
    }

    public static class RolePrincipal implements Principal {
        private final String role;
        public RolePrincipal(String role) { this.role = role; }
        @Override public String getName() { return role; }
        public String getRole() { return role; }
    }

    public static class ReferenceIdPrincipal implements Principal {
        private final Long refId;
        public ReferenceIdPrincipal(Long refId) { this.refId = refId; }
        @Override public String getName() { return String.valueOf(refId); }
        public Long getReferenceId() { return refId; }
    }
}