package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PedidoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class ListarPedidosUseCaseTest {

    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var useCase: ListarPedidosUseCase

    @BeforeEach
    fun setUp() {
        pedidoRepository = mockk()
        useCase = ListarPedidosUseCase(pedidoRepository)
    }

    @Test
    fun retornaPedidosFiltraStatusEmAndamentoEOrdenaPorEtapaEDataInicio() {
        val t0 = LocalDateTime.parse("2024-01-01T10:00:00")
        val t1 = LocalDateTime.parse("2024-01-01T10:05:00")
        val t2 = LocalDateTime.parse("2024-01-01T10:10:00")

        val pedidoProntoMaisAntigo = Pedido(
            id = UUID.randomUUID(),
            status = StatusPedido.PRONTO,
            dataInicio = t0,
            clienteNome = "Cliente 1"
        )
        val pedidoPagamentoAprovadoMaisRecente = Pedido(
            id = UUID.randomUUID(),
            status = StatusPedido.PAGAMENTO_APROVADO,
            dataInicio = t2,
            clienteNome = "Cliente 2"
        )
        val pedidoEmPreparacaoMaisAntigo = Pedido(
            id = UUID.randomUUID(),
            status = StatusPedido.EM_PREPARACAO,
            dataInicio = t0,
            clienteNome = "Cliente 3"
        )
        val pedidoPagamentoAprovadoMaisAntigo = Pedido(
            id = UUID.randomUUID(),
            status = StatusPedido.PAGAMENTO_APROVADO,
            dataInicio = t1,
            clienteNome = "Cliente 4"
        )

        every {
            pedidoRepository.findByStatusIn(ListarPedidosUseCase.ETAPAS_ANDAMENTO_PEDIDO)
        } returns listOf(
            pedidoProntoMaisAntigo,
            pedidoPagamentoAprovadoMaisRecente,
            pedidoEmPreparacaoMaisAntigo,
            pedidoPagamentoAprovadoMaisAntigo
        )

        val resultado = useCase.execute()

        assertEquals(4, resultado.size)
        // Ordem esperada: PAGAMENTO_APROVADO (mais antigo -> mais recente), EM_PREPARACAO, PRONTO
        assertEquals(pedidoPagamentoAprovadoMaisAntigo.id, resultado[0].id)
        assertEquals(pedidoPagamentoAprovadoMaisRecente.id, resultado[1].id)
        assertEquals(pedidoEmPreparacaoMaisAntigo.id, resultado[2].id)
        assertEquals(pedidoProntoMaisAntigo.id, resultado[3].id)

        verify { pedidoRepository.findByStatusIn(ListarPedidosUseCase.ETAPAS_ANDAMENTO_PEDIDO) }
    }
}

