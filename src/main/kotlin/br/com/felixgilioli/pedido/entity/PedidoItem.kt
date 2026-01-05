package br.com.felixgilioli.pedido.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document(collection = "pedido_item")
data class PedidoItem(

    @Id
    val id: UUID? = null,
    val pedidoId: UUID,
    val produtoId: UUID,
    val quantidade: Int,
    val precoUnitario: BigDecimal
) {
    fun total() = precoUnitario.multiply(quantidade.toBigDecimal())
}