package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PedidoRepository
import org.springframework.stereotype.Service

@Service
class BuscarPedidosAguardandoConfirmacaoCozinhaUseCase(
    private val pedidoRepository: PedidoRepository
) {

    fun execute() = pedidoRepository.findByStatus(StatusPedido.PAGAMENTO_APROVADO)
        .sortedBy { it.dataInicio }
}