package co.com.crediya.autenticacion.model.usuario.gateways;

import co.com.crediya.autenticacion.model.usuario.Usuario;

public interface UsuarioRepository {
    boolean existsByCorreoElectronico(String correoElectronico);
    boolean existsByDocumento(String documento);
    Usuario save(Usuario usuario);
}