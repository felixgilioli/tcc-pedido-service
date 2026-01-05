package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.PedidoItem
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PagamentoRepository
import br.com.felixgilioli.pedido.repository.PedidoRepository
import br.com.felixgilioli.pedido.repository.ProdutoRepository
import br.com.felixgilioli.pedido.usecase.command.ConfirmarPedidoCommand
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ConfirmarPedidoUseCase(
    private val pedidoRepository: PedidoRepository,
    private val produtoRepository: ProdutoRepository,
    private val pagamentoRepository: PagamentoRepository
) {

    fun execute(command: ConfirmarPedidoCommand): Pedido {
        val pedido = pedidoRepository.findByIdOrNull(command.pedidoId)
            ?: throw IllegalArgumentException("Pedido não encontrado")

        val produtoPorId = produtoRepository.findAllById(command.itens.map { it.produtoId }).associateBy { it.id }

        val pedidoItemList = command.itens.map {
            val produto = produtoPorId[it.produtoId] ?: throw IllegalArgumentException("Produto não encontrado")
            PedidoItem(
                pedidoId = pedido.id,
                produtoId = produto.id,
                quantidade = it.quantidade,
                precoUnitario = produto.preco
            )
        }

        val valorTotalPedido = pedidoItemList.fold(BigDecimal.ZERO) { acc, item -> acc + item.total() }

        return pedido.copy(status = StatusPedido.PAGAMENTO_SOLICITADO, itens = pedidoItemList, total = valorTotalPedido)
            .also { pagamentoRepository.solicitarPagamento(it) }
            .let { pedidoRepository.save(it) }
    }
}