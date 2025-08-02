package com.lucasalmeida.dscatalog.resources;

import com.lucasalmeida.dscatalog.dto.EmailDTO;
import com.lucasalmeida.dscatalog.dto.UserDTO;
import com.lucasalmeida.dscatalog.dto.UserInsertDTO;
import com.lucasalmeida.dscatalog.dto.UserUpdateDTO;
import com.lucasalmeida.dscatalog.servicies.AuthService;
import com.lucasalmeida.dscatalog.servicies.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/auth")
public class AuthResource {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDTO body) {
        authService.createRecoverToken(body);

        return ResponseEntity.noContent().build();
    }
}
