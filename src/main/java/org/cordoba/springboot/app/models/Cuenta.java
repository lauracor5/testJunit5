package org.cordoba.springboot.app.models;

import org.cordoba.springboot.app.exceptions.DineroInsuficienteExceptions;

import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "persona")
    private String Persona;

    private BigDecimal saldo;

    public Cuenta(Long id, String persona, BigDecimal saldo) {
        this.id = id;
        this.Persona = persona;
        this.saldo = saldo;
    }

    public Cuenta(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersona() {
        return Persona;
    }

    public void setPersona(String persona) {
        Persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldoPersona) {
        this.saldo = saldoPersona;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return Objects.equals(id, cuenta.id) && Objects.equals(Persona, cuenta.Persona) && Objects.equals(saldo, cuenta.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Persona, saldo);
    }

    public void debito(BigDecimal monto){
       BigDecimal nuevoSaldo =  this.saldo.subtract(monto);
       if(nuevoSaldo.compareTo(BigDecimal.ZERO) <0 ){
           throw new DineroInsuficienteExceptions("Dinero Insuficiente en la cuenta");
       }
           this.saldo = nuevoSaldo;

    }

    public void credito(BigDecimal monto){
        this.saldo = this.saldo.add(monto);
    }
}
