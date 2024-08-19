package com.gerenciadorlehsa.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;
import static com.gerenciadorlehsa.util.ConstantesErroValidadorUtil.*;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Pessoa extends BaseEntity {

    @Column(name = "NOME", length = 100,nullable = false)
    private String nome;

    @Column(name = "EMAIL", unique = true, nullable = false)
    @Pattern(regexp = "^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+\\.?([a-z]+)?$", message = MSG_ERRO_EMAIL)
    private String email;

}
