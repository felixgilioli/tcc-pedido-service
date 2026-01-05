package br.com.felixgilioli.pedido.dto

import java.math.BigDecimal
import java.util.UUID

data class Produto(
    val id: UUID,
    val preco: BigDecimal
)
