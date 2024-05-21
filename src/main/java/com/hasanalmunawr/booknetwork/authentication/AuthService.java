package com.hasanalmunawr.booknetwork.authentication;

import com.hasanalmunawr.booknetwork.email.EmailService;
import com.hasanalmunawr.booknetwork.email.EmailTemplateName;
import com.hasanalmunawr.booknetwork.entity.TokenEntity;
import com.hasanalmunawr.booknetwork.entity.UserEntity;
import com.hasanalmunawr.booknetwork.repository.RoleRepository;
import com.hasanalmunawr.booknetwork.repository.TokenRepository;
import com.hasanalmunawr.booknetwork.repository.UserRepository;
import com.hasanalmunawr.booknetwork.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegisterRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
//                todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));

        var user = UserEntity.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    public void activateAccount(String token) {
        TokenEntity savedToken = tokenRepository.findByToken(token)
//                todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            throw new RuntimeException("Activation token has expired. A new token has been send to tha same email " +
                    "address");
        }

        UserEntity userEntity = userRepository.findById(savedToken.getUser().getId()).orElseThrow(() -> new UsernameNotFoundException("User " +
                "Not Found"));
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
    }

    private void sendValidationEmail(UserEntity user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(UserEntity user) {
        String generatedToken = generateActivationCode(6);

        TokenEntity token = TokenEntity.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode(int lengthCode) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < lengthCode; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
