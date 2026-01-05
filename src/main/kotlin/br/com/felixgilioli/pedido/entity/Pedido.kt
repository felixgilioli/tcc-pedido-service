package br.com.felixgilioli.pedido.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Document(collection = "pedido")
data class Pedido(

    @Id
    val id: UUID = UUID.randomUUID(),
    val status: StatusPedido,
    val dataInicio: LocalDateTime = LocalDateTime.now(),
    val dataFim: LocalDateTime? = null,
    val clienteNome: String,
    val cliente: Cliente? = null,
    val itens: List<PedidoItem> = emptyList(),
    val total: BigDecimal? = null
)