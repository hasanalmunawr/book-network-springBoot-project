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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static com.hasanalmunawr.booknetwork.utils.EmailUtils.ACTIVATION_ACCOUNT;

@Service
@RequiredArgsConstructor
@Slf4j
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
        log.info("[AuthService:register] REgisting {}", request.getEmail());
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
        sendValidationEmail(user); // THE ERROR CAN NOT SEND THE CODE ???
    }

//    @Transactional
    public void activateAccount(String tokenCode) {
        TokenEntity savedToken = tokenRepository.findByToken(tokenCode)
//                todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            throw new RuntimeException("Activation token has expired. A new token has been send to tha same email " +
                    "address");
        }

        UserEntity userEntity = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User " +
                "Not Found"));
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
    }

    private void sendValidationEmail(UserEntity user) throws MessagingException {
        var newToken = generateAndSaveActivationCode(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                ACTIVATION_ACCOUNT
        );
    }

    public AuthResponse login(AuthRequest request) {
        log.info("[AuthService:login] Processing login by {}", request.getEmail());
        var authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = (UserEntity) authenticate.getPrincipal();
        claims.put("fullName", user.getFullName());

        var token = jwtService.generateToken(claims, user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }


    private String generateAndSaveActivationCode(UserEntity user) {
        String generatedCode = generateActivationCode(6);

        TokenEntity token = TokenEntity.builder()
                .token(generatedCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedCode;
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
