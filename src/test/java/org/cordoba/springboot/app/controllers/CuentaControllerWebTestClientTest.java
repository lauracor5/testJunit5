package org.cordoba.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cordoba.springboot.app.models.Cuenta;
import org.cordoba.springboot.app.models.TransaccionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_wc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonProcessingException {
        //gIVEN
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con éxito");
        response.put("transaccion", dto);

        //WHEN
        webTestClient.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
        //Then
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(respuesta->{
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals("Transferencia realizada con éxito", json.path("mensaje").asText());
                        assertEquals(1, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaccion").path("monto").asText());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                /*
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("Transferencia realizada con éxito"))
                .jsonPath("$.mensaje").value( valor ->{
                    assertEquals("Transferencia realizada con éxito", valor); })
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con éxito")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString()) */
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @Order(2)
    void testDetalle() throws JsonProcessingException {
        Cuenta cuenta = new Cuenta(1L, "laura", new BigDecimal("900")); //

        webTestClient.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("laura")
                .jsonPath("$.saldo").isEqualTo(900)
                .json(objectMapper.writeValueAsString(cuenta));
    }

    @Test
    @Order(3)
    void testDetalle2() {
        webTestClient.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                   Cuenta cuenta= response.getResponseBody();
                   assertEquals("Sofía", cuenta.getPersona());
                   assertEquals("2100.00", cuenta.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(4)
    void testListar() {
        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("laura")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo("900.0")
                .jsonPath("$[1].persona").isEqualTo("Sofía")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo("2100.0")
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void testListar2() {
        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> {
                    List<Cuenta>cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2, cuentas.size());
                    assertEquals("laura", cuentas.get(0).getPersona());
                    assertEquals("900.0", cuentas.get(0).getSaldo().toPlainString());
                    assertEquals(1, cuentas.get(0).getId());
                    assertEquals("Sofía", cuentas.get(1).getPersona());
                    assertEquals("2100.0", cuentas.get(1).getSaldo().toPlainString());
                    assertEquals(2, cuentas.get(1).getId());
                });

    }

    @Test
    @Order(6)
    void testGuardar() {
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        webTestClient.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Pepe")
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.saldo").isEqualTo(3000);

    }

    @Test
    @Order(7)
    void testGuardar2() {
        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3000"));
        webTestClient.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta c = response.getResponseBody();
                            assertNotNull(c);
                            assertEquals(4L, c.getId());
                            assertEquals("Pepa", c.getPersona());
                            assertEquals("3000", c.getSaldo().toPlainString());
        });
    }

    @Test
    @Order(8)
    void testDelete() {
        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        webTestClient.delete().uri("/api/cuentas/3")
        //No se envía nada en el request
        .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        //Despues de eliminar
        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        webTestClient.get().uri("/api/cuentas/3").exchange()
               //expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

}



