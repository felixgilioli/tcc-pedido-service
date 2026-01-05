package br.com.felixgilioli.pedido.entity

enum class StatusPedido {
    CRIADO,
    PEDIDO_CONFIRMADO,
    PAGAMENTO_SOLICITADO,
    PAGAMENTO_APROVADO,
    PAGAMENTO_RECUSADO,
    EM_PREPARACAO,
    PRONTO,
    FINALIZADO
}