package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.Cliente
import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.ClienteRepository
import br.com.felixgilioli.pedido.repository.PedidoRepository
import br.com.felixgilioli.pedido.usecase.command.NovoPedidoCommand
import org.springframework.stereotype.Service

@Service
class NovoPedidoUseCase(
    private val clienteRepository: ClienteRepository,
    private val pedidoRepository: PedidoRepository
) {

    fun execute(command: NovoPedidoCommand): Pedido {
        var cliente: Cliente? = null

        val clienteNome = when {
            !command.clienteEmail.isNullOrBlank() -> {
                cliente = clienteRepository.findByEmail(command.clienteEmail)
                cliente?.nomeCompleto ?: throw IllegalArgumentException("Cliente não encontrado")
            }

            command.clienteCPF != null -> command.clienteCPF.value
            else -> (10000..99999).random().toString()
        }

        return Pedido(
            status = StatusPedido.CRIADO,
            clienteNome = clienteNome,
            cliente = cliente
        ).let { pedidoRepository.save(it) }
    }
}