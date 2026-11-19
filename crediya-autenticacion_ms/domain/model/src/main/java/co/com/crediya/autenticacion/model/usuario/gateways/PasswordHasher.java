package co.com.crediya.autenticacion.model.usuario.gateways;

public interface PasswordHasher {
    String hash(String password);
    boolean matches(String password, String passwordHash);
}