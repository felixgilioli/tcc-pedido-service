package br.com.felixgilioli.pedido.repository;

import br.com.felixgilioli.pedido.entity.Pedido
import br.com.felixgilioli.pedido.entity.StatusPedido
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface PedidoRepository : MongoRepository<Pedido, UUID> {

    fun findByStatus(statusPedido: StatusPedido): List<Pedido>

    fun findByStatusIn(statusList: List<StatusPedido>): List<Pedido>
}
