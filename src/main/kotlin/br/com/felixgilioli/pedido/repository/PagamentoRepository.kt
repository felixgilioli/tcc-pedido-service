package br.com.felixgilioli.pedido.repository

import br.com.felixgilioli.pedido.entity.Pedido

interface PagamentoRepository {

    fun solicitarPagamento(pedido: Pedido)
}