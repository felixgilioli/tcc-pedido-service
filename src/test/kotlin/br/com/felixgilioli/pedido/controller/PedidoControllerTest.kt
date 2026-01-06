package br.com.felixgilioli.pedido.controller

import br.com.felixgilioli.pedido.dto.request.ConfirmarPedidoItemRequest
import br.com.felixgilioli.pedido.dto.request.ConfirmarPedidoRequest
import br.com.felixgilioli.pedido.dto.request.NovoPedidoRequest
import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.usecase.*
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.UUID

class PedidoControllerTest {

    private val novoPedidoUseCase: NovoPedidoUseCase = mockk()
    private val confirmarPedidoUseCase: ConfirmarPedidoUseCase = mockk()
    private val buscarPedidosAguardandoConfirmacaoCozinhaUseCase: BuscarPedidosAguardandoConfirmacaoCozinhaUseCase = mockk()
    private val confirmarPedidoCozinhaUseCase: ConfirmarPedidoCozinhaUseCase = mockk()
    private val buscarPedidoPeloIdUseCase: BuscarPedidoPeloIdUseCase = mockk()
    private val definirPedidoProntoUseCase: DefinirPedidoProntoUseCase = mockk()
    private val retirarPedidoUseCase: RetirarPedidoUseCase = mockk()
    private val listarPedidosUseCase: ListarPedidosUseCase = mockk()
    private val atualizarStatusPedidoUseCase: AtualizarStatusPedidoUseCase = mockk()

    private val objectMapper = ObjectMapper().findAndRegisterModules()

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(
            PedidoController(
                novoPedidoUseCase,
                confirmarPedidoUseCase,
                buscarPedidosAguardandoConfirmacaoCozinhaUseCase,
                confirmarPedidoCozinhaUseCase,
                buscarPedidoPeloIdUseCase,
                definirPedidoProntoUseCase,
                retirarPedidoUseCase,
                listarPedidosUseCase,
                atualizarStatusPedidoUseCase
            )
        )
        .build()

    @Test
    fun postNovoPedido_retorna200EBody() {
        val pedidoId = UUID.randomUUID()
        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.CRIADO,
            dataInicio = LocalDateTime.parse("2024-01-01T10:00:00"),
            clienteNome = "Cliente 1"
        )

        every { novoPedidoUseCase.execute(any()) } returns pedido

        mockMvc.perform(
            post("/v1/pedido/novo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(NovoPedidoRequest(clienteEmail = "a@b.com", clienteCPF = null)))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pedidoId").value(pedidoId.toString()))
            .andExpect(jsonPath("$.status").value("CRIADO"))
            .andExpect(jsonPath("$.clienteNome").value("Cliente 1"))

        verify(exactly = 1) { novoPedidoUseCase.execute(any()) }
    }

    @Test
    fun postConfirmarPedido_retorna200EBody() {
        val pedidoId = UUID.randomUUID()
        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.PAGAMENTO_SOLICITADO,
            dataInicio = LocalDateTime.parse("2024-01-01T10:00:00"),
            clienteNome = "Cliente 1"
        )

        every { confirmarPedidoUseCase.execute(any()) } returns pedido

        val request = ConfirmarPedidoRequest(
            pedidoId = pedidoId.toString(),
            itens = listOf(ConfirmarPedidoItemRequest(produtoId = UUID.randomUUID().toString(), quantidade = 1))
        )

        mockMvc.perform(
            post("/v1/pedido/confirmar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pedidoId").value(pedidoId.toString()))
            .andExpect(jsonPath("$.status").value("PAGAMENTO_SOLICITADO"))

        verify(exactly = 1) { confirmarPedidoUseCase.execute(any()) }
    }

    @Test
    fun getAguardandoConfirmacaoCozinha_retornaLista() {
        val pedidos = listOf(
            Pedido(id = UUID.randomUUID(), status = StatusPedido.PAGAMENTO_APROVADO, clienteNome = "Cliente 1"),
            Pedido(id = UUID.randomUUID(), status = StatusPedido.PAGAMENTO_APROVADO, clienteNome = "Cliente 2")
        )

        every { buscarPedidosAguardandoConfirmacaoCozinhaUseCase.execute() } returns pedidos

        mockMvc.perform(get("/v1/pedido/aguardando-confirmacao-cozinha"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].status").value("PAGAMENTO_APROVADO"))

        verify(exactly = 1) { buscarPedidosAguardandoConfirmacaoCozinhaUseCase.execute() }
    }

    @Test
    fun putConfirmarCozinha_retorna200EBody() {
        val pedidoId = UUID.randomUUID()
        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.EM_PREPARACAO,
            clienteNome = "Cliente 1"
        )

        every { confirmarPedidoCozinhaUseCase.execute(pedidoId) } returns pedido

        mockMvc.perform(put("/v1/pedido/$pedidoId/confirmar-cozinha"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pedidoId").value(pedidoId.toString()))
            .andExpect(jsonPath("$.status").value("EM_PREPARACAO"))

        verify(exactly = 1) { confirmarPedidoCozinhaUseCase.execute(pedidoId) }
    }

    @Test
    fun getAcompanharPedido_quandoExiste_retorna200() {
        val pedidoId = UUID.randomUUID()
        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.CRIADO,
            clienteNome = "Cliente 1"
        )

        every { buscarPedidoPeloIdUseCase.execute(pedidoId) } returns pedido

        mockMvc.perform(get("/v1/pedido/$pedidoId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pedidoId").value(pedidoId.toString()))

        verify(exactly = 1) { buscarPedidoPeloIdUseCase.execute(pedidoId) }
    }

    @Test
    fun getAcompanharPedido_quandoNaoExiste_retorna404() {
        val pedidoId = UUID.randomUUID()

        every { buscarPedidoPeloIdUseCase.execute(pedidoId) } returns null

        mockMvc.perform(get("/v1/pedido/$pedidoId"))
            .andExpect(status().isNotFound)

        verify(exactly = 1) { buscarPedidoPeloIdUseCase.execute(pedidoId) }
    }

    @Test
    fun putPedidoPronto_retorna200EBody() {
        val pedidoId = UUID.randomUUID()
        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.PRONTO,
            clienteNome = "Cliente 1"
        )

        every { definirPedidoProntoUseCase.execute(pedidoId) } returns pedido

        mockMvc.perform(put("/v1/pedido/$pedidoId/pronto"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pedidoId").value(pedidoId.toString()))
            .andExpect(jsonPath("$.status").value("PRONTO"))

        verify(exactly = 1) { definirPedidoProntoUseCase.execute(pedidoId) }
    }

    @Test
    fun putRetirarPedido_retorna200EBody() {
        val pedidoId = UUID.randomUUID()
        val pedido = Pedido(
            id = pedidoId,
            status = StatusPedido.FINALIZADO,
            clienteNome = "Cliente 1"
        )

        every { retirarPedidoUseCase.execute(pedidoId) } returns pedido

        mockMvc.perform(put("/v1/pedido/$pedidoId/retirar"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pedidoId").value(pedidoId.toString()))
            .andExpect(jsonPath("$.status").value("FINALIZADO"))

        verify(exactly = 1) { retirarPedidoUseCase.execute(pedidoId) }
    }

    @Test
    fun getListarPedidos_quandoVazio_retorna404() {
        every { listarPedidosUseCase.execute() } returns emptyList()

        mockMvc.perform(get("/v1/pedido"))
            .andExpect(status().isNotFound)

        verify(exactly = 1) { listarPedidosUseCase.execute() }
    }

    @Test
    fun getListarPedidos_quandoExistemPedidos_retorna200ELista() {
        val pedidos = listOf(
            Pedido(id = UUID.randomUUID(), status = StatusPedido.PAGAMENTO_APROVADO, clienteNome = "Cliente 1"),
            Pedido(id = UUID.randomUUID(), status = StatusPedido.EM_PREPARACAO, clienteNome = "Cliente 2")
        )
        every { listarPedidosUseCase.execute() } returns pedidos

        mockMvc.perform(get("/v1/pedido"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))

        verify(exactly = 1) { listarPedidosUseCase.execute() }
    }

    @Test
    fun putAtualizarStatusPedido_retorna200EChamaUseCase() {
        val pedidoId = UUID.randomUUID()

        every { atualizarStatusPedidoUseCase.execute(pedidoId, StatusPedido.EM_PREPARACAO) } just runs

        mockMvc.perform(
            put("/v1/pedido/$pedidoId/status")
                .queryParam("status", "EM_PREPARACAO")
        )
            .andExpect(status().isOk)

        verify(exactly = 1) { atualizarStatusPedidoUseCase.execute(pedidoId, StatusPedido.EM_PREPARACAO) }
    }
}

