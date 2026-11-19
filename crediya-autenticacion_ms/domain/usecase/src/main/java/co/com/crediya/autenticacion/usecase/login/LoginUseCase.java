package co.com.crediya.autenticacion.usecase.login;

import co.com.crediya.autenticacion.model.usuario.exceptions.CredencialesInvalidasException;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordHasher;
import co.com.crediya.autenticacion.model.usuario.gateways.TokenGenerator;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginUseCase {
    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;

    public LoginResult login(LoginCommand command) {
        var usuario = usuarioRepository.findByCorreoElectronico(command.correoElectronico())
                .filter(user -> user.getPasswordHash() != null
                        && passwordHasher.matches(command.password(), user.getPasswordHash()))
                .orElseThrow(CredencialesInvalidasException::new);

        String token = tokenGenerator.generate(usuario);
        return new LoginResult(token, usuario.getId(), usuario.getCorreoElectronico(), usuario.getRol());
    }
}