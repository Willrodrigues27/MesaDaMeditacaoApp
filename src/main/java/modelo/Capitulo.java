package modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Capitulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;
}
