package co.com.crediya.solicitudes.usecase.registrarsolicitud;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/** Cuota mensual por amortización: P * i * (1+i)^n / ((1+i)^n - 1). */
public class CalcularCuotaMensual {

    private static final int ESCALA = 2;
    private static final MathContext CONTEXTO = MathContext.DECIMAL64;

    public BigDecimal calcular(BigDecimal monto, BigDecimal tasaMensual, Integer plazoMeses) {
        if (monto == null || tasaMensual == null || plazoMeses == null || plazoMeses <= 0) {
            throw new IllegalArgumentException("Monto, tasa y plazo son obligatorios para calcular la cuota.");
        }
        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0) {
            return monto.divide(BigDecimal.valueOf(plazoMeses), ESCALA, RoundingMode.HALF_UP);
        }

        BigDecimal factor = BigDecimal.ONE.add(tasaMensual).pow(plazoMeses, CONTEXTO);
        BigDecimal numerador = monto.multiply(tasaMensual).multiply(factor);
        BigDecimal denominador = factor.subtract(BigDecimal.ONE);
        return numerador.divide(denominador, ESCALA, RoundingMode.HALF_UP);
    }
}
