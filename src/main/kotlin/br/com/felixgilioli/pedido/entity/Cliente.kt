package br.com.felixgilioli.pedido.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "cliente")
data class Cliente(

    @Id
    val id: UUID? = null,
    val nomeCompleto: String,
    val email: String
) {
    init {
        require(nomeCompleto.isNotBlank()) { "Nome completo não pode estar em branco" }
        require(email.isNotBlank()) { "Email não pode estar em branco" }
    }

}