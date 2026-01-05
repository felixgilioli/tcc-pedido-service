package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PedidoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class AtualizarStatusPedidoUseCase(
    private val pedidoRepository: PedidoRepository
) {

    fun execute(pedidoId: UUID, status: StatusPedido) {
        val pedido = pedidoRepository.findByIdOrNull(pedidoId)
            ?: throw IllegalArgumentException("Pedido não encontrado")

        pedido.copy(status = status)
            .let { pedidoRepository.save(it) }
    }
}