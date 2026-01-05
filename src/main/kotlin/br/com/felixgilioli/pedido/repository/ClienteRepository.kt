package br.com.felixgilioli.pedido.repository

import br.com.felixgilioli.pedido.entity.Cliente
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface ClienteRepository : MongoRepository<Cliente, UUID> {

    fun findByEmail(email: String): Cliente?
}