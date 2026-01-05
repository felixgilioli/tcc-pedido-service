package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PedidoRepository
import org.springframework.stereotype.Service

@Service
class ListarPedidosUseCase(
    private val pedidoRepository: PedidoRepository
) {

    companion object {
        val ETAPAS_ANDAMENTO_PEDIDO = listOf(
            StatusPedido.PAGAMENTO_APROVADO,
            StatusPedido.EM_PREPARACAO,
            StatusPedido.PRONTO
        )
    }

    fun execute(): List<Pedido> = pedidoRepository.findByStatusIn(ETAPAS_ANDAMENTO_PEDIDO)
        .sortedWith(
            compareBy<Pedido> { ETAPAS_ANDAMENTO_PEDIDO.indexOf(it.status) }
                .thenBy { it.dataInicio }
        )
}