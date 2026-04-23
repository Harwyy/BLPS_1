package com.blps.blps.security.service;

import com.blps.blps.dto.request.CreateUserRequest;
import com.blps.blps.dto.request.RegisterRequest;
import com.blps.blps.security.model.XmlUser;
import com.blps.blps.security.model.XmlUsersWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class XmlUserDetailsService implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final Set<String> ALLOWED_ROLES = Set.of(
            "ROLE_CUSTOMER", "ROLE_RESTAURANT", "ROLE_COURIER", "ROLE_ADMIN"
    );
    private static final long DEFAULT_ADMIN_REFERENCE_ID = -1L;

    @Value("${admin.default.username}")
    private String defaultAdminUsername;

    @Value("${admin.default.password}")
    private String defaultAdminPassword;

    @Value("${users.file.path}")
    private String usersFilePath;

    private final PasswordEncoder passwordEncoder;

    private final Map<String, UserDetails> usersCache = new ConcurrentHashMap<>();
    private final Map<String, Long> referenceIdCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            Path path = getUsersFilePath();
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                createDefaultAdminFile(path);
            }
            loadUsersFromFile();
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize user storage", e);
        }
    }

    private Path getUsersFilePath() {
        return Paths.get(usersFilePath);
    }

    private void createDefaultAdminFile(Path path) throws Exception {
        XmlUser admin = new XmlUser();
        admin.setUsername(defaultAdminUsername);
        admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
        admin.setRole("ROLE_ADMIN");
        admin.setReferenceId(DEFAULT_ADMIN_REFERENCE_ID);

        saveUsersToFile(Collections.singletonList(admin), path);
    }

    private void loadUsersFromFile() throws Exception {
        Path path = getUsersFilePath();
        List<XmlUser> users = loadAllUsersFromFile(path);
        usersCache.clear();
        referenceIdCache.clear();
        for (XmlUser xmlUser : users) {
            UserDetails user = User.builder()
                    .username(xmlUser.getUsername())
                    .password(xmlUser.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(xmlUser.getRole())))
                    .build();
            usersCache.put(xmlUser.getUsername(), user);
            referenceIdCache.put(xmlUser.getUsername(), xmlUser.getReferenceId());
        }
    }

    private List<XmlUser> loadAllUsersFromFile(Path path) throws Exception {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        JAXBContext context = createJAXBContext();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        try (InputStream is = Files.newInputStream(path)) {
            XmlUsersWrapper wrapper = (XmlUsersWrapper) unmarshaller.unmarshal(is);
            return wrapper.getUsers() != null ? wrapper.getUsers() : new ArrayList<>();
        }
    }

    private void saveUsersToFile(List<XmlUser> users, Path path) throws Exception {
        XmlUsersWrapper wrapper = new XmlUsersWrapper();
        wrapper.setUsers(users);
        JAXBContext context = createJAXBContext();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        try (OutputStream os = Files.newOutputStream(path)) {
            marshaller.marshal(wrapper, os);
        }
    }

    private JAXBContext createJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(XmlUsersWrapper.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = usersCache.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

    public Long getReferenceId(String username) {
        Long refId = referenceIdCache.get(username);
        if (refId == null) {
            throw new IllegalArgumentException("No reference ID found for user: " + username);
        }
        return refId;
    }

    public synchronized void registerUser(RegisterRequest request) throws Exception {
        validateUsernameNotTaken(request.getUsername());

        XmlUser newUser = new XmlUser();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("ROLE_CUSTOMER");
        newUser.setReferenceId(-1L);

        modifyFile(users -> users.add(newUser));
    }

    public synchronized XmlUser createUser(CreateUserRequest request) throws Exception {
        validateUsernameNotTaken(request.getUsername());

        String role = normalizeRole(request.getRole());
        validateRole(role);

        Long referenceId = request.getReferenceId();
        if (referenceId == null || referenceId < 0) {
            referenceId = -1L;
        }

        XmlUser newUser = new XmlUser();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(role);
        newUser.setReferenceId(referenceId);

        modifyFile(users -> users.add(newUser));
        return newUser;
    }

    public List<XmlUser> getAllUsers() {
        try {
            return loadAllUsersFromFile(getUsersFilePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load users from XML", e);
        }
    }

    private void validateUsernameNotTaken(String username) {
        if (usersCache.containsKey(username)) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
    }

    private String normalizeRole(String rawRole) {
        return rawRole.startsWith(ROLE_PREFIX) ? rawRole : ROLE_PREFIX + rawRole;
    }

    private void validateRole(String role) {
        if (!ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    private interface UserListModifier {
        void modify(List<XmlUser> users);
    }

    private synchronized void modifyFile(UserListModifier modifier) throws Exception {
        Path path = getUsersFilePath();
        List<XmlUser> users = loadAllUsersFromFile(path);
        modifier.modify(users);
        saveUsersToFile(users, path);
        loadUsersFromFile();
    }

    public XmlUser getXmlUser(String username) {
        return getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}