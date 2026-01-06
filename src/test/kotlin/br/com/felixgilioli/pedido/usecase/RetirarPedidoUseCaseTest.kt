package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PedidoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class RetirarPedidoUseCaseTest {

    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var useCase: RetirarPedidoUseCase

    @BeforeEach
    fun setUp() {
        pedidoRepository = mockk()
        useCase = RetirarPedidoUseCase(pedidoRepository)
    }

    @Test
    fun retiraPedidoEAtualizaStatusParaFinalizado() {
        val pedidoId = UUID.randomUUID()
        val pedidoAtual = Pedido(
            id = pedidoId,
            status = StatusPedido.PRONTO,
            clienteNome = "Cliente 1"
        )

        every { pedidoRepository.findByIdOrNull(pedidoId) } returns pedidoAtual
        every { pedidoRepository.save(any()) } answers { firstArg() }

        val resultado = useCase.execute(pedidoId)

        assertEquals(pedidoId, resultado.id)
        assertEquals(StatusPedido.FINALIZADO, resultado.status)
        verify(exactly = 1) { pedidoRepository.findByIdOrNull(pedidoId) }
        verify(exactly = 1) {
            pedidoRepository.save(withArg { pedidoSalvo ->
                assertEquals(pedidoId, pedidoSalvo.id)
                assertEquals(StatusPedido.FINALIZADO, pedidoSalvo.status)
            })
        }
    }

    @Test
    fun lancaExcecaoQuandoPedidoNaoEncontrado() {
        val pedidoId = UUID.randomUUID()

        every { pedidoRepository.findByIdOrNull(pedidoId) } returns null

        val exception = assertThrows(IllegalArgumentException::class.java) {
            useCase.execute(pedidoId)
        }

        assertEquals("Pedido não encontrado", exception.message)
        verify(exactly = 1) { pedidoRepository.findByIdOrNull(pedidoId) }
        verify(exactly = 0) { pedidoRepository.save(any()) }
    }
}

