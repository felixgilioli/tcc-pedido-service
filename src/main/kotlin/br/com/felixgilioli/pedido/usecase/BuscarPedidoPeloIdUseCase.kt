package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.repository.PedidoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class BuscarPedidoPeloIdUseCase(
    private val pedidoRepository: PedidoRepository
) {

    fun execute(pedidoId: UUID) = pedidoRepository.findByIdOrNull(pedidoId)
}