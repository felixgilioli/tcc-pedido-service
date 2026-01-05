package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PedidoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class ConfirmarPedidoCozinhaUseCase(
    private val pedidoRepository: PedidoRepository
) {

    fun execute(pedidoId: UUID) = pedidoRepository.findByIdOrNull(pedidoId)
        ?.let { pedidoRepository.save(it.copy(status = StatusPedido.EM_PREPARACAO)) }
        ?: throw IllegalArgumentException("Pedido não encontrado")
}