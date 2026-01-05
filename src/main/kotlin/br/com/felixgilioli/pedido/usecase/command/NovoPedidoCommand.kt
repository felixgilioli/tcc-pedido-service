package br.com.felixgilioli.pedido.usecase.command

import br.com.felixgilioli.pedido.entity.CPF

data class NovoPedidoCommand(
    val clienteEmail: String?,
    val clienteCPF: CPF?
)
