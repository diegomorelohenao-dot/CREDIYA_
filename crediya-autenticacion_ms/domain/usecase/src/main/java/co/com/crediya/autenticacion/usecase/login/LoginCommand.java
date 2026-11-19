package co.com.crediya.autenticacion.usecase.login;

public record LoginCommand(String correoElectronico, String password) {
}