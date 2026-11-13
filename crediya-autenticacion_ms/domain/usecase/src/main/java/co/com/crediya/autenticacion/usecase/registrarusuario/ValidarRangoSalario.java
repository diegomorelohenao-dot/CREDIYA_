package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.ReglaRegistroUsuario;

import java.math.BigDecimal;

public class ValidarRangoSalario implements ReglaRegistroUsuario {
    private final BigDecimal salarioMaximo;

    public ValidarRangoSalario(BigDecimal salarioMaximo) {
        this.salarioMaximo = salarioMaximo;
    }

    @Override
    public void validar(Usuario usuario) {
        BigDecimal salario = usuario.getSalarioBase();
        if (salario == null) {
            return;
        }
        if (salario.compareTo(BigDecimal.ZERO) < 0 || salario.compareTo(salarioMaximo) > 0) {
            throw new IllegalArgumentException(
                    "El salario base debe estar entre 0 y " + salarioMaximo.toPlainString() + ".");
        }
    }
}
