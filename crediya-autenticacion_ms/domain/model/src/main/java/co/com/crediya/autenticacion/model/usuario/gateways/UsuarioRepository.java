package co.com.crediya.autenticacion.model.usuario.gateways;

import co.com.crediya.autenticacion.model.usuario.Usuario;

import java.util.Optional;

public interface UsuarioRepository {
    boolean existsByCorreoElectronico(String correoElectronico);
    boolean existsByDocumento(String documento);
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
    Usuario save(Usuario usuario);
}