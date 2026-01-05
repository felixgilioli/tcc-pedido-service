package br.com.felixgilioli.pedido.dto.response

import br.com.felixgilioli.pedido.entity.Cliente
import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class PedidoResponse(
    val pedidoId: UUID? = null,
    val status: StatusPedido,
    val dataInicio: LocalDateTime,
    val dataFim: LocalDateTime?,
    val clienteNome: String,
    val cliente: Cliente?,
    val total: BigDecimal?
)

fun Pedido.toResponse() = PedidoResponse(
    pedidoId = id,
    status = status,
    dataInicio = dataInicio,
    dataFim = dataFim,
    clienteNome = clienteNome,
    cliente = cliente,
    total = total
)
