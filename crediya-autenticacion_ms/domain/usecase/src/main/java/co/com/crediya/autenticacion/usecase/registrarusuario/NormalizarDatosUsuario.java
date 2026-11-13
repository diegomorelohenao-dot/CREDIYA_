package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.NormalizadorUsuario;

import java.util.Locale;

public class NormalizarDatosUsuario implements NormalizadorUsuario {
    @Override
    public Usuario normalizar(Usuario usuario) {
        return usuario.toBuilder()
                .nombres(usuario.getNombres().trim())
                .apellidos(usuario.getApellidos().trim())
                .documento(usuario.getDocumento().trim())
                .direccion(usuario.getDireccion().trim())
                .telefono(usuario.getTelefono().trim())
                .correoElectronico(usuario.getCorreoElectronico().trim().toLowerCase(Locale.ROOT))
                .build();
    }
}
