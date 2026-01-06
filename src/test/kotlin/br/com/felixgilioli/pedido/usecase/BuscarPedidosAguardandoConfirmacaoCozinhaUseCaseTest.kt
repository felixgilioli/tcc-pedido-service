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

class BuscarPedidosAguardandoConfirmacaoCozinhaUseCaseTest {

    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var useCase: BuscarPedidosAguardandoConfirmacaoCozinhaUseCase

    @BeforeEach
    fun setUp() {
        pedidoRepository = mockk()
        useCase = BuscarPedidosAguardandoConfirmacaoCozinhaUseCase(pedidoRepository)
    }

    @Test
    fun retornaPedidosAguardandoConfirmacaoCozinha() {
        val pedidos = listOf(
            Pedido(id = UUID.randomUUID(), status = StatusPedido.PAGAMENTO_APROVADO, clienteNome = "Cliente 1"),
            Pedido(id = UUID.randomUUID(), status = StatusPedido.PAGAMENTO_APROVADO, clienteNome = "Cliente 2")
        )
        every { pedidoRepository.findByStatus(StatusPedido.PAGAMENTO_APROVADO) } returns pedidos

        val resultado = useCase.execute()

        assertEquals(2, resultado.size)
        assertEquals(StatusPedido.PAGAMENTO_APROVADO, resultado[0].status)
        verify { pedidoRepository.findByStatus(StatusPedido.PAGAMENTO_APROVADO) }
    }
}