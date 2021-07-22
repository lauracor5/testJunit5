package org.cordoba.springboot.app;

import org.cordoba.springboot.app.models.Cuenta;
import org.cordoba.springboot.app.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegracionJpaTest {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("laura", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("laura");
        assertTrue(cuenta.isPresent());
        assertEquals("laura", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");
        assertThrows(NoSuchElementException.class, () -> {
            cuenta.orElseThrow();
        });
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta>listaCuenta = cuentaRepository.findAll();
        assertFalse(listaCuenta.isEmpty());
        assertEquals(2, listaCuenta.size());
    }

    @Test
    void testSave() {
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        cuentaRepository.save(cuentaPepe);

        //when
        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

        //Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        //assertEquals(3, cuenta.getId());
    }

    @Test
    void testSaveUpdate() {
        //Given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));


        //when
        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

        //Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        //Then
        assertEquals("Pepe", cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());

    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Sofía", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, ()->{
            cuentaRepository.findByPersona("Sofía").orElseThrow();
        });

        assertEquals(1, cuentaRepository.findAll().size());
    }
}
