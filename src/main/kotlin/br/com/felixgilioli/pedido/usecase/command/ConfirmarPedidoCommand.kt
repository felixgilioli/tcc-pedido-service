package br.com.felixgilioli.pedido.usecase.command

import java.util.*

data class ConfirmarPedidoCommand(
    val pedidoId: UUID,
    val itens: List<ConfirmarPedidoItemCommand>
)

data class ConfirmarPedidoItemCommand(
    val produtoId: UUID,
    val quantidade: Int
)

