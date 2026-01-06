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
import java.util.UUID

class BuscarPedidoPeloIdUseCaseTest {

    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var useCase: BuscarPedidoPeloIdUseCase

    @BeforeEach
    fun setUp() {
        pedidoRepository = mockk()
        useCase = BuscarPedidoPeloIdUseCase(pedidoRepository)
    }

    @Test
    fun retornaPedidoQuandoEncontrado() {
        val pedidoId = UUID.randomUUID()
        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.PAGAMENTO_APROVADO,
            clienteNome = "Cliente 1"
        )

        every { pedidoRepository.findById(pedidoId) } returns java.util.Optional.of(pedido)

        val resultado = useCase.execute(pedidoId)

        assertNotNull(resultado)
        assertEquals(pedidoId, resultado?.id)
        verify { pedidoRepository.findById(pedidoId) }
    }

    @Test
    fun retornaNullQuandoNaoEncontrado() {
        val pedidoId = UUID.randomUUID()

        every { pedidoRepository.findById(pedidoId) } returns java.util.Optional.empty()

        val resultado = useCase.execute(pedidoId)

        assertNull(resultado)
        verify { pedidoRepository.findById(pedidoId) }
    }
}