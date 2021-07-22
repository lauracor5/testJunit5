package org.cordoba.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cordoba.springboot.app.Datos;
import org.cordoba.springboot.app.models.Cuenta;
import org.cordoba.springboot.app.models.TransaccionDto;
import org.cordoba.springboot.app.services.CuentaService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CuentaService cuentaService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testListar() throws Exception {
        //Given
        List<Cuenta>cuentas = Arrays.asList(Datos.crearCuenta001().orElseThrow(),
                Datos.crearCuenta002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);

        //When
        mockMvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].persona").value("lAURA "))
                .andExpect(jsonPath("$.[1].persona").value("SOFIA "))
                .andExpect(jsonPath("$.[0].saldo").value("1000"))
                .andExpect(jsonPath("$.[1].saldo").value("2000"))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));

        verify(cuentaService).findAll();
    }

    @Test
    void testDetalle() throws Exception {
        //simula el service y el serviidor y el htto, el único real es el controlador
        //given => CONTEXTO DE PRUEBA
        when(cuentaService.findById(1L)).thenReturn(Datos.crearCuenta001().orElseThrow());

        //when => Cuando se hace la llamada con una api serverlet y esperamos un resultado
        //La lllamada al controlador mediante la ruta
        mockMvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
        //then
            .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("lAURA "))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(cuentaService).findById(1L);
    }

    @Test
    void testSave() throws Exception {
        //Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        when(cuentaService.save(any())).then(invocation -> {
            Cuenta c = invocation.getArgument(0);  //=>capturamos el objeto que estamos pasando al contenedor
            c.setId(3L);
            return c;
        });

        //when
        mockMvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuenta)))
        //THEN
                .andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.persona").value("Pepe"))
                .andExpect(jsonPath("$.saldo").value("3000"));

        verify(cuentaService).save(any());

    }
    @Test
    void testTransferir() throws Exception {

        //Given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);
        System.out.println(objectMapper.writeValueAsString(dto));

        //convertir el objeto dto a json

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con éxito");
        response.put("transaccion", dto);
        System.out.println(objectMapper.writeValueAsString(response)); //=>convierte el map a json



        //When
        mockMvc.perform(post("/api/cuentas/transferir").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))


        //then
        .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con éxito"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(dto.getCuentaOrigenId()))
        .andExpect(content().json(objectMapper.writeValueAsString(response)));


    }
}
