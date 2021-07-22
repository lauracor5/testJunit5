package org.cordoba.springboot.app;

import org.cordoba.springboot.app.exceptions.DineroInsuficienteExceptions;
import org.cordoba.springboot.app.models.Banco;
import org.cordoba.springboot.app.models.Cuenta;
import org.cordoba.springboot.app.repositories.BancoRepository;
import org.cordoba.springboot.app.repositories.CuentaRepository;
import org.cordoba.springboot.app.services.CuentaService;
import org.cordoba.springboot.app.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

	@MockBean
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;
	@Autowired
	CuentaService service;

	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoOrigen.toString());
		assertEquals("2000", saldoDestino.toString());

		service.tarsnferir(1L, 2L, new BigDecimal("100"), 1L);
		 saldoOrigen = service.revisarSaldo(1L);
		 saldoDestino = service.revisarSaldo(2L);

		 assertEquals("900", saldoOrigen.toPlainString());
		 assertEquals("2100", saldoDestino.toPlainString());

		 int total = service.revisarTotalTransferencias(1L);
		assertEquals(1, total);

		 verify(cuentaRepository, times(3)).findById(1L);
		 verify(cuentaRepository, times(3)).findById(2L);
		 verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		 verify(bancoRepository, times(2)).findById(1L);
		 verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

	}

	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoOrigen.toString());
		assertEquals("2000", saldoDestino.toString());

		Exception exception = assertThrows(DineroInsuficienteExceptions.class, ()-> {
			service.tarsnferir(1L, 2L, new BigDecimal("1200"), 1L);
		});
		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencias(1L);
		assertEquals(0, total);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);
		verify(cuentaRepository, never()).save(any(Cuenta.class));

		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()	).save(any(Banco.class));

		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoad3() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);
		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		assertEquals("lAURA ", cuenta1.getPersona());
		assertEquals("lAURA ", cuenta2.getPersona());
		verify(cuentaRepository, times(2)).findById(1L);

	}

	@Test
	void testFindAll() {
		//Given
		List<Cuenta> datos = Arrays.asList(Datos.crearCuenta001().orElseThrow(),
				Datos.crearCuenta002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(datos);

		//when
		List<Cuenta> cuentas = service.findAll();

		//then
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(Datos.crearCuenta001().orElseThrow()));

		verify(cuentaRepository).findAll();

	}

	@Test
	void testSave() {
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invocation -> {
				Cuenta c = invocation.getArgument(0);
				c.setId(3L);
				return c;
		});

		//when
		Cuenta cuenta = service.save(cuentaPepe);
		//then
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals(3, cuenta.getId());
		assertEquals("3000", cuenta.getSaldo().toPlainString());

		verify(cuentaRepository).save(any());
	}
}
