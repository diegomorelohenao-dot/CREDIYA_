package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exceptions.UsuarioYaRegistradoException;
import co.com.crediya.autenticacion.model.usuario.gateways.NormalizadorUsuario;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordHasher;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioValidator usuarioValidator;
    private final NormalizadorUsuario normalizadorUsuario;
    private final PasswordHasher passwordHasher;
    private final ValidarPermisoRegistroUsuario validarPermisoRegistroUsuario;

    public Usuario registrarUsuario(RegistrarUsuarioCommand command) {
        validarPermisoRegistroUsuario.validar(command.rolSolicitante(), command.rol());

        Usuario usuario = command.usuario();
        usuarioValidator.validar(usuario);
        Usuario usuarioNormalizado = normalizadorUsuario.normalizar(usuario);

        if (usuarioRepository.existsByCorreoElectronico(usuarioNormalizado.getCorreoElectronico())
                || usuarioRepository.existsByDocumento(usuarioNormalizado.getDocumento())) {
            throw new UsuarioYaRegistradoException();
        }

        Usuario usuarioConCredenciales = usuarioNormalizado.toBuilder()
                .passwordHash(passwordHasher.hash(command.password()))
                .rol(command.rol())
                .build();
        return usuarioRepository.save(usuarioConCredenciales);
    }
}
