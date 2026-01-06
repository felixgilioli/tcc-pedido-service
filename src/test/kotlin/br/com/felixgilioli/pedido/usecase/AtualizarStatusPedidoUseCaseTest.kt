package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PedidoRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class AtualizarStatusPedidoUseCaseTest {

    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var useCase: AtualizarStatusPedidoUseCase

    @BeforeEach
    fun setUp() {
        pedidoRepository = mockk()
        useCase = AtualizarStatusPedidoUseCase(pedidoRepository)
    }

    @Test
    fun atualizaStatusQuandoPedidoExiste() {
        val pedidoId = UUID.randomUUID()
        val pedidoAtual = Pedido(
            id = pedidoId,
            status = StatusPedido.PAGAMENTO_APROVADO,
            clienteNome = "Cliente 1"
        )
        val novoStatus = StatusPedido.EM_PREPARACAO

        every { pedidoRepository.findByIdOrNull(pedidoId) } returns pedidoAtual
        every { pedidoRepository.save(any()) } answers { firstArg() }

        useCase.execute(pedidoId, novoStatus)

        verify(exactly = 1) { pedidoRepository.findByIdOrNull(pedidoId) }
        verify(exactly = 1) {
            pedidoRepository.save(withArg { pedidoSalvo ->
                assertEquals(pedidoId, pedidoSalvo.id)
                assertEquals(novoStatus, pedidoSalvo.status)
            })
        }
        confirmVerified(pedidoRepository)
    }

    @Test
    fun lancaExcecaoQuandoPedidoNaoEncontrado() {
        val pedidoId = UUID.randomUUID()

        every { pedidoRepository.findByIdOrNull(pedidoId) } returns null

        val exception = assertThrows(IllegalArgumentException::class.java) {
            useCase.execute(pedidoId, StatusPedido.EM_PREPARACAO)
        }

        assertEquals("Pedido não encontrado", exception.message)
        verify(exactly = 1) { pedidoRepository.findByIdOrNull(pedidoId) }
        verify(exactly = 0) { pedidoRepository.save(any()) }
    }
}