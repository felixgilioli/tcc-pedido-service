package br.com.felixgilioli.pedido.bdd

import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.PagamentoRepository
import br.com.felixgilioli.pedido.repository.PedidoRepository
import br.com.felixgilioli.pedido.repository.ProdutoRepository
import br.com.felixgilioli.pedido.usecase.ConfirmarPedidoUseCase
import br.com.felixgilioli.pedido.usecase.command.ConfirmarPedidoCommand
import br.com.felixgilioli.pedido.usecase.command.ConfirmarPedidoItemCommand
import io.cucumber.datatable.DataTable
import io.cucumber.java.pt.Dado
import io.cucumber.java.pt.Entao
import io.cucumber.java.pt.Quando
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.util.UUID

class ConfirmarPedidoStepDefinitions {

    private val pedidoRepository: PedidoRepository = io.mockk.mockk(relaxed = true)
    private val produtoRepository: ProdutoRepository = io.mockk.mockk(relaxed = true)
    private val pagamentoRepository: PagamentoRepository = io.mockk.mockk(relaxed = true)

    private val useCase = ConfirmarPedidoUseCase(pedidoRepository, produtoRepository, pagamentoRepository)

    private lateinit var pedido: Pedido
    private lateinit var command: ConfirmarPedidoCommand
    private var resultado: Pedido? = null

    @Dado("que existe um pedido criado")
    fun queExisteUmPedidoCriado() {
        val pedidoId = UUID.randomUUID()
        pedido = Pedido(id = pedidoId, status = StatusPedido.CRIADO, clienteNome = "Cliente BDD")

        io.mockk.every { pedidoRepository.findByIdOrNull(pedidoId) } returns pedido

        io.mockk.every { pedidoRepository.save(any()) } answers { firstArg() }

        command = ConfirmarPedidoCommand(pedidoId = pedidoId, itens = emptyList())
    }

    @Dado("os seguintes produtos cadastrados:")
    fun osSeguintesProdutosCadastrados(dataTable: DataTable) {
        val produtos = dataTable.asMaps().map {
            br.com.felixgilioli.pedido.dto.Produto(
                id = UUID.fromString(it["id"]!!),
                preco = BigDecimal(it["preco"]!!)
            )
        }
        val ids = produtos.map { it.id }
        io.mockk.every { produtoRepository.findAllById(ids) } returns produtos
    }

    @Dado("o pedido possui os seguintes itens:")
    fun oPedidoPossuiOsSeguintesItens(dataTable: DataTable) {
        val itens = dataTable.asMaps().map {
            ConfirmarPedidoItemCommand(
                produtoId = UUID.fromString(it["produtoId"]!!),
                quantidade = it["quantidade"]!!.toInt()
            )
        }
        command = command.copy(itens = itens)
    }

    @Quando("eu confirmar o pedido")
    fun euConfirmarOPedido() {
        resultado = useCase.execute(command)
    }

    @Entao("o status do pedido deve ser {string}")
    fun oStatusDoPedidoDeveSer(statusEsperado: String) {
        assertEquals(StatusPedido.valueOf(statusEsperado), resultado!!.status)
    }

    @Entao("o total do pedido deve ser {string}")
    fun oTotalDoPedidoDeveSer(totalEsperado: String) {
        val esperado = BigDecimal(totalEsperado).setScale(2)
        assertEquals(esperado, resultado!!.total)
    }

    @Entao("o pagamento deve ser solicitado")
    fun oPagamentoDeveSerSolicitado() {
        io.mockk.verify(exactly = 1) { pagamentoRepository.solicitarPagamento(resultado!!) }
    }
}
