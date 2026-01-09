package br.com.felixgilioli.pedido.repository.http

import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.repository.PagamentoRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class PagamentoRepositoryImpl(
    private val restTemplate: RestTemplate = RestTemplate()
) : PagamentoRepository {

    override fun solicitarPagamento(pedido: Pedido) {
        val pedidoId = requireNotNull(pedido.id) { "pedido.id não pode ser null para solicitar pagamento" }
        val valorTotal = requireNotNull(pedido.total) { "pedido.total não pode ser null para solicitar pagamento" }

        val uri = UriComponentsBuilder
            .fromUriString("http://pagamento-service.tcc.svc.cluster.local/v1/pagamento/solicitar")
            .queryParam("pedidoId", pedidoId.toString())
            .queryParam("valorTotal", valorTotal.toPlainString())
            .build(true)
            .toUri()

        restTemplate.exchange(
            uri,
            HttpMethod.POST,
            HttpEntity.EMPTY,
            Void::class.java
        )
    }
}