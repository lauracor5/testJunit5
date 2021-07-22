package org.cordoba.springboot.app;

import org.cordoba.springboot.app.models.Banco;
import org.cordoba.springboot.app.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {

    //public static final Cuenta CUENTA_001 = new Cuenta(1L, "lAURA ", new BigDecimal("1000"));
    //public static final Cuenta CUENTA_002 = new Cuenta(2L, "SOFIA ", new BigDecimal("2000"));
    //public static final Cuenta CUENTA_003 = new Cuenta(3L, "SOFIA ", new BigDecimal("3000"));
    //public static final Banco BANCO = new Banco(1L, "El banco financiero", 0);

    public static Optional<Cuenta> crearCuenta001(){
        return Optional.of(new Cuenta(1L, "lAURA ", new BigDecimal("1000")));
    }

    public static Optional<Cuenta> crearCuenta002(){
        return Optional.of(new Cuenta(2L, "SOFIA ", new BigDecimal("2000")));
    }

    public static Optional<Banco> crearBanco(){
    return Optional.of(new Banco(1L, "El banco financiero", 0));
    }

}
