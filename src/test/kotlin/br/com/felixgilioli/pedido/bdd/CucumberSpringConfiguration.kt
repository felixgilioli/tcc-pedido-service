package br.com.felixgilioli.pedido.bdd

import br.com.felixgilioli.pedido.TccPedidoServiceApplication
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest

@CucumberContextConfiguration
@SpringBootTest(classes = [TccPedidoServiceApplication::class])
class CucumberSpringConfiguration {
}