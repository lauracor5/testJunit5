package org.cordoba.springboot.app.models;

import javax.persistence.*;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bancos")
public class Banco {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Column(name = "total_transferencias")
    private int totalTrasnferencia;

    public Banco(Long id, String nombre, int totalTrasnferencia) {
        this.id = id;
        this.nombre = nombre;
        this.totalTrasnferencia = totalTrasnferencia;
    }

    public Banco(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTotalTrasnferencia() {
        return totalTrasnferencia;
    }

    public void setTotalTrasnferencia(int totalTrasnferencia) {
        this.totalTrasnferencia = totalTrasnferencia;
    }


}
