package br.com.felixgilioli.pedido.repository.http

import br.com.felixgilioli.pedido.dto.Produto
import br.com.felixgilioli.pedido.repository.ProdutoRepository
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@Component
class ProdutoRepositoryImpl(
    private val restTemplate: RestTemplate = RestTemplate()
) : ProdutoRepository {

    override fun findAllById(ids: List<UUID>): List<Produto> {
        if (ids.isEmpty()) return emptyList()

        val uri = UriComponentsBuilder
            .fromUriString("http://fastfood-service.tcc.svc.cluster.local/v1/produto")
            .queryParam("ids", *ids.map { it.toString() }.toTypedArray())
            .build(true)
            .toUri()

        val response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            HttpEntity.EMPTY,
            object : ParameterizedTypeReference<List<Produto>>() {}
        )

        return response.body.orEmpty()
    }
}