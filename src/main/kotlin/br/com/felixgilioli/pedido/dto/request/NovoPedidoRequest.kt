package br.com.felixgilioli.pedido.dto.request

import br.com.felixgilioli.pedido.entity.CPF
import br.com.felixgilioli.pedido.usecase.command.NovoPedidoCommand

data class NovoPedidoRequest(
    val clienteEmail: String? = null,
    val clienteCPF: String? = null
) {
    fun toCommand() = NovoPedidoCommand(
        clienteEmail = clienteEmail,
        clienteCPF = clienteCPF?.let { CPF(it) }
    )
}
