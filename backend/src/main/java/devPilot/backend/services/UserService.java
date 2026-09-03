package devPilot.backend.services;

import java.util.UUID;
import java.util.Map;

import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devPilot.backend.entity.User;
import devPilot.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {
    public final UserRepository userRepository;
    public final TextEncryptor tokenEncryptor;
    public final EntityManager entityManager;

    @Transactional(readOnly = true)
    public User requiredById(UUID id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return user;
    }

    public String decryptAccessToken(User user) {
        return tokenEncryptor.decrypt(user.getAccessToken());
    }

    private static Long toLong(Object value){
        if (value instanceof Number number) {
            return number.longValue();
        } 
        return Long.parseLong(String.valueOf(value));
    }   

    @Transactional
    public User upsertFromGithub(Map<String, Object> attributes, String accessToken, String scopes) {
        Long githubId = toLong(attributes.get("id"));
        String login = (String) attributes.get("login");
        String avatarUrl = (String) attributes.get("avatar_url") != null ? String.valueOf(attributes.get("avatar_url")) : "";
        String name = (String) attributes.get("name") != null ? String.valueOf(attributes.get("name")) : "";
        String encryptedToken = tokenEncryptor.encrypt(accessToken);

        User user = userRepository.findByGithubId(githubId).orElseGet(User::new);
        user.setGithubId(githubId);
        user.setGithubUsername(login);
        user.setDisplayName(name);
        user.setAvatarUrl(avatarUrl);
        user.setAccessToken(encryptedToken);
        user.setTokenScopes(scopes);

        return userRepository.save(user);
    }
}