package com.ebac.homework.sessions.controller;

import com.ebac.homework.sessions.dto.UsuarioLoginDTO;
import com.ebac.homework.sessions.model.Usuario;
import com.ebac.homework.sessions.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UsuarioController {

    private UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrar(@RequestBody Usuario usuario) throws Exception {
        if(usuarioRepository.findByEmail(usuario.getEmail())==null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Email já cadastrado, por favor tente novamente com outro email.");
        } else if (usuarioRepository.findByUsername(usuario.getUsername())==null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Username já cadastrado, por favor tente novamente com outro nome.");
        }
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario.getUsername()+" cadastrado com sucesso.");
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestBody UsuarioLoginDTO userDTO,
                                        HttpServletRequest request) throws Exception {

        Optional<Usuario> usuarioPossivel = usuarioRepository.findByUsername(userDTO.username());
        if (usuarioPossivel.isPresent() && usuarioPossivel.get().getSenha().equals(userDTO.senha())) {
            //criando a sessão
            HttpSession session = request.getSession(true);
            //armazenando os dados da sessão
            request.getSession().setAttribute("usuarioLogado", usuarioPossivel.get().getUsername());

            return ResponseEntity.status(HttpStatus.OK).body("Login efetuado com sucesso. Seu id é: "+session.getId());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao efetuar login, verifique seu username ou senha e tente novamente.");
    }

    @GetMapping("/perfil")
    public ResponseEntity<String> perfil(HttpSession session) throws Exception {

        String username = session.getAttribute("usuarioLogado").toString();

        //verifica se a sessão está logada
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão inválida ou expirada!");
        }
        String info = username + " é um(a) aluno(a) ou professor(a) da ebac, uma excelente instituiçao de ensino!";
        return ResponseEntity.status(HttpStatus.OK).body(info);
    }
}
