package org.cordoba.springboot.app.services;

import org.cordoba.springboot.app.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    List<Cuenta>findAll();

    Cuenta  findById(Long id);

    Cuenta save(Cuenta cuenta);

    int revisarTotalTransferencias(Long bancoId);

    BigDecimal revisarSaldo(Long cuentaId);

    void tarsnferir(Long nuCuentaOrigen, Long numeroCuentaDestino, BigDecimal monto, Long bancoId);

    void deleteById(Long id);


}
