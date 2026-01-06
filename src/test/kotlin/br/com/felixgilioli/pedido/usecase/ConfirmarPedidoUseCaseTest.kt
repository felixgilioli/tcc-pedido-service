package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.dto.Produto
import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PagamentoRepository
import br.com.felixgilioli.pedido.repository.PedidoRepository
import br.com.felixgilioli.pedido.repository.ProdutoRepository
import br.com.felixgilioli.pedido.usecase.command.ConfirmarPedidoCommand
import br.com.felixgilioli.pedido.usecase.command.ConfirmarPedidoItemCommand
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.util.UUID

class ConfirmarPedidoUseCaseTest {

    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var produtoRepository: ProdutoRepository
    private lateinit var pagamentoRepository: PagamentoRepository
    private lateinit var useCase: ConfirmarPedidoUseCase

    @BeforeEach
    fun setUp() {
        pedidoRepository = mockk()
        produtoRepository = mockk()
        pagamentoRepository = mockk(relaxed = true)
        useCase = ConfirmarPedidoUseCase(pedidoRepository, produtoRepository, pagamentoRepository)
    }

    @Test
    fun confirmaPedidoCalculaTotalSolicitaPagamentoESalva() {
        val pedidoId = UUID.randomUUID()
        val produto1Id = UUID.randomUUID()
        val produto2Id = UUID.randomUUID()

        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.CRIADO,
            clienteNome = "Cliente 1"
        )

        val command = ConfirmarPedidoCommand(
            pedidoId = pedidoId,
            itens = listOf(
                ConfirmarPedidoItemCommand(produtoId = produto1Id, quantidade = 2),
                ConfirmarPedidoItemCommand(produtoId = produto2Id, quantidade = 1)
            )
        )

        val produtos = listOf(
            Produto(id = produto1Id, preco = BigDecimal("10.00")),
            Produto(id = produto2Id, preco = BigDecimal("5.50"))
        )

        every { pedidoRepository.findByIdOrNull(pedidoId) } returns pedido
        every { produtoRepository.findAllById(listOf(produto1Id, produto2Id)) } returns produtos
        every { pedidoRepository.save(any()) } answers { firstArg() }

        val resultado = useCase.execute(command)

        // total = 2*10.00 + 1*5.50 = 25.50
        assertEquals(pedidoId, resultado.id)
        assertEquals(StatusPedido.PAGAMENTO_SOLICITADO, resultado.status)
        assertEquals(2, resultado.itens.size)
        assertEquals(BigDecimal("25.50"), resultado.total)

        verify(exactly = 1) { pedidoRepository.findByIdOrNull(pedidoId) }
        verify(exactly = 1) { produtoRepository.findAllById(listOf(produto1Id, produto2Id)) }
        verify(exactly = 1) {
            pagamentoRepository.solicitarPagamento(withArg { pedidoPagamento ->
                assertEquals(pedidoId, pedidoPagamento.id)
                assertEquals(StatusPedido.PAGAMENTO_SOLICITADO, pedidoPagamento.status)
                assertEquals(BigDecimal("25.50"), pedidoPagamento.total)
            })
        }
        verify(exactly = 1) {
            pedidoRepository.save(withArg { pedidoSalvo ->
                assertEquals(pedidoId, pedidoSalvo.id)
                assertEquals(StatusPedido.PAGAMENTO_SOLICITADO, pedidoSalvo.status)
                assertEquals(BigDecimal("25.50"), pedidoSalvo.total)
            })
        }

        confirmVerified(pedidoRepository, produtoRepository, pagamentoRepository)
    }

    @Test
    fun lancaExcecaoQuandoPedidoNaoEncontrado() {
        val pedidoId = UUID.randomUUID()
        val command = ConfirmarPedidoCommand(
            pedidoId = pedidoId,
            itens = listOf(ConfirmarPedidoItemCommand(produtoId = UUID.randomUUID(), quantidade = 1))
        )

        every { pedidoRepository.findByIdOrNull(pedidoId) } returns null

        val exception = assertThrows(IllegalArgumentException::class.java) {
            useCase.execute(command)
        }

        assertEquals("Pedido não encontrado", exception.message)
        verify(exactly = 1) { pedidoRepository.findByIdOrNull(pedidoId) }
        verify(exactly = 0) { produtoRepository.findAllById(any()) }
        verify(exactly = 0) { pedidoRepository.save(any()) }
    }

    @Test
    fun lancaExcecaoQuandoProdutoNaoEncontrado() {
        val pedidoId = UUID.randomUUID()
        val produtoId = UUID.randomUUID()

        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.CRIADO,
            clienteNome = "Cliente 1"
        )

        val command = ConfirmarPedidoCommand(
            pedidoId = pedidoId,
            itens = listOf(ConfirmarPedidoItemCommand(produtoId = produtoId, quantidade = 1))
        )

        every { pedidoRepository.findByIdOrNull(pedidoId) } returns pedido
        every { produtoRepository.findAllById(listOf(produtoId)) } returns emptyList()

        val exception = assertThrows(IllegalArgumentException::class.java) {
            useCase.execute(command)
        }

        assertEquals("Produto não encontrado", exception.message)
        verify(exactly = 1) { pedidoRepository.findByIdOrNull(pedidoId) }
        verify(exactly = 1) { produtoRepository.findAllById(listOf(produtoId)) }
        verify(exactly = 0) { pagamentoRepository.solicitarPagamento(any()) }
        verify(exactly = 0) { pedidoRepository.save(any()) }
    }
}