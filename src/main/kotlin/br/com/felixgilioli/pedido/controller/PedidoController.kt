package br.com.felixgilioli.pedido.controller

import br.com.felixgilioli.pedido.dto.request.ConfirmarPedidoRequest
import br.com.felixgilioli.pedido.dto.request.NovoPedidoRequest
import br.com.felixgilioli.pedido.dto.response.toResponse
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.usecase.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/pedido")
@Tag(name = "Pedido API", description = "Gerenciamento de pedidos")
class PedidoController(
    private val novoPedidoUseCase: NovoPedidoUseCase,
    private val confirmarPedidoUseCase: ConfirmarPedidoUseCase,
    private val buscarPedidosAguardandoConfirmacaoCozinhaUseCase: BuscarPedidosAguardandoConfirmacaoCozinhaUseCase,
    private val confirmarPedidoCozinhaUseCase: ConfirmarPedidoCozinhaUseCase,
    private val buscarPedidoPeloIdUseCase: BuscarPedidoPeloIdUseCase,
    private val definirPedidoProntoUseCase: DefinirPedidoProntoUseCase,
    private val retirarPedidoUseCase: RetirarPedidoUseCase,
    private val listarPedidosUseCase: ListarPedidosUseCase,
    private val atualizarStatusPedidoUseCase: AtualizarStatusPedidoUseCase
) {

    @PostMapping("/novo")
    @Operation(
        summary = "Novo pedido",
        description = "Cria um novo pedido no sistema sem nenhum item adicionado."
    )
    fun novoPedido(@RequestBody request: NovoPedidoRequest) = novoPedidoUseCase
        .execute(request.toCommand()).toResponse()

    @PostMapping("/confirmar")
    @Operation(
        summary = "Confirmar pedido",
        description = "Confirma um pedido com os itens especificados."
    )
    fun confirmarPedido(@RequestBody request: ConfirmarPedidoRequest) = confirmarPedidoUseCase
        .execute(request.toCommand()).toResponse()

    @GetMapping("/aguardando-confirmacao-cozinha")
    @Operation(
        summary = "Pedidos aguardando confirmação da cozinha",
        description = "Lista todos os pedidos que estão aguardando confirmação na cozinha."
    )
    fun findPedidosAguardandoConfirmacaoCozinha() = buscarPedidosAguardandoConfirmacaoCozinhaUseCase.execute()
        .map { it.toResponse() }

    @PutMapping("/{pedidoId}/confirmar-cozinha")
    @Operation(
        summary = "Confirmar pedido na cozinha",
        description = "Confirma um pedido na cozinha para iniciar o preparo."
    )
    fun confirmarPedidoCozinha(@PathVariable pedidoId: String) = confirmarPedidoCozinhaUseCase
        .execute(UUID.fromString(pedidoId)).toResponse()

    @GetMapping("/{pedidoId}")
    @Operation(
        summary = "Acompanhar pedido",
        description = "Permite acompanhar o status de um pedido específico."
    )
    fun acompanharPedido(@PathVariable pedidoId: String) =
        buscarPedidoPeloIdUseCase.execute(UUID.fromString(pedidoId))?.toResponse()
            ?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    @PutMapping("/{pedidoId}/pronto")
    @Operation(
        summary = "Pedido pronto",
        description = "Marca um pedido como pronto para retirada."
    )
    fun pedidoPronto(@PathVariable pedidoId: String) = definirPedidoProntoUseCase
        .execute(UUID.fromString(pedidoId)).toResponse()

    @PutMapping("/{pedidoId}/retirar")
    @Operation(
        summary = "Retirar pedido",
        description = "Marca um pedido como retirado pelo cliente."
    )
    fun retirarPedido(@PathVariable pedidoId: String) = retirarPedidoUseCase
        .execute(UUID.fromString(pedidoId)).toResponse()

    @GetMapping
    @Operation(
        summary = "Listar pedidos",
        description = "Lista todos os pedidos ordenando por status e data de criação."
    )
    fun listarPedidos() = listarPedidosUseCase.execute()
        .map { it.toResponse() }
        .let { if (it.isEmpty()) ResponseEntity.notFound().build() else ResponseEntity.ok(it) }

    @PutMapping("/{pedidoId}/status")
    @Operation(
        summary = "Atualizar status do pedido",
        description = "Atualiza o status de um pedido específico."
    )
    fun atualizarStatusPedido(
        @PathVariable pedidoId: String,
        @RequestParam status: String
    ) = atualizarStatusPedidoUseCase.execute(UUID.fromString(pedidoId), StatusPedido.valueOf(status))
}