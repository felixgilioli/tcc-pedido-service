package br.com.felixgilioli.pedido.repository

import br.com.felixgilioli.pedido.dto.Produto
import java.util.*

interface ProdutoRepository {

    fun findAllById(ids: List<UUID>): List<Produto>

}