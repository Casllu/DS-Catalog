package com.lucasalmeida.dscatalog.servicies;

import com.lucasalmeida.dscatalog.dto.EmailDTO;
import com.lucasalmeida.dscatalog.dto.NewPasswordDTO;
import com.lucasalmeida.dscatalog.entities.PasswordRecover;
import com.lucasalmeida.dscatalog.entities.User;
import com.lucasalmeida.dscatalog.repository.PasswordRevocerRepository;
import com.lucasalmeida.dscatalog.repository.UserRepository;
import com.lucasalmeida.dscatalog.servicies.exceptions.EmailException;
import com.lucasalmeida.dscatalog.servicies.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoveryUri;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRevocerRepository passwordRevocerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createRecoverToken(EmailDTO body) {

        User user = userRepository.findByEmail(body.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("Email não encontrado");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        passwordRevocerRepository.save(entity);

        String text = "Acesso o link para definir uma nova senha\n\n"
                + recoveryUri + token + ". Validade de " + tokenMinutes + " minutos";
        emailService.sendEmail(entity.getEmail(), "Recuperação de senha", text);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO body) {
        List<PasswordRecover> result = passwordRevocerRepository.searchValidTokens(body.getToken(), Instant.now());

        if(result.isEmpty()) {
            throw new ResourceNotFoundException("Token inválido");
        }

        User user = userRepository.findByEmail(result.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user = userRepository.save(user);


    }
}
