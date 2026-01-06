package br.com.felixgilioli.pedido.usecase

import br.com.felixgilioli.pedido.entity.CPF
import br.com.felixgilioli.pedido.entity.Cliente
import br.com.felixgilioli.pedido.entity.StatusPedido
import br.com.felixgilioli.pedido.repository.ClienteRepository
import br.com.felixgilioli.pedido.repository.PedidoRepository
import br.com.felixgilioli.pedido.usecase.command.NovoPedidoCommand
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NovoPedidoUseCaseTest {

    private lateinit var clienteRepository: ClienteRepository
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var useCase: NovoPedidoUseCase

    @BeforeEach
    fun setUp() {
        clienteRepository = mockk()
        pedidoRepository = mockk()
        useCase = NovoPedidoUseCase(clienteRepository, pedidoRepository)
    }

    @Test
    fun criaPedidoComClienteNomeQuandoEmailInformadoEClienteExiste() {
        val command = NovoPedidoCommand(clienteEmail = "cliente@teste.com", clienteCPF = null)
        val cliente = Cliente(nomeCompleto = "Cliente 1", email = "cliente@teste.com")

        every { clienteRepository.findByEmail("cliente@teste.com") } returns cliente
        every { pedidoRepository.save(any()) } answers { firstArg() }

        val resultado = useCase.execute(command)

        assertEquals(StatusPedido.CRIADO, resultado.status)
        assertEquals("Cliente 1", resultado.clienteNome)
        assertEquals(cliente, resultado.cliente)
        verify { clienteRepository.findByEmail("cliente@teste.com") }
        verify { pedidoRepository.save(any()) }
    }

    @Test
    fun lancaExcecaoQuandoEmailInformadoEClienteNaoExiste() {
        val command = NovoPedidoCommand(clienteEmail = "cliente@teste.com", clienteCPF = null)

        every { clienteRepository.findByEmail("cliente@teste.com") } returns null

        val exception = assertThrows(IllegalArgumentException::class.java) {
            useCase.execute(command)
        }

        assertEquals("Cliente não encontrado", exception.message)
        verify { clienteRepository.findByEmail("cliente@teste.com") }
        verify(exactly = 0) { pedidoRepository.save(any()) }
    }

    @Test
    fun criaPedidoComNomeIgualCpfQuandoCpfInformadoESemEmail() {
        val cpf = CPF("52998224725")
        val command = NovoPedidoCommand(clienteEmail = null, clienteCPF = cpf)

        every { pedidoRepository.save(any()) } answers { firstArg() }

        val resultado = useCase.execute(command)

        assertEquals(StatusPedido.CRIADO, resultado.status)
        assertEquals(cpf.value, resultado.clienteNome)
        assertNull(resultado.cliente)
        verify(exactly = 0) { clienteRepository.findByEmail(any()) }
        verify { pedidoRepository.save(any()) }
    }

    @Test
    fun criaPedidoComNomeRandomQuandoNaoInformaEmailNemCpf() {
        val command = NovoPedidoCommand(clienteEmail = null, clienteCPF = null)

        every { pedidoRepository.save(any()) } answers { firstArg() }

        val resultado = useCase.execute(command)

        assertEquals(StatusPedido.CRIADO, resultado.status)
        assertNotNull(resultado.clienteNome)
        assertTrue(resultado.clienteNome.matches(Regex("\\d{5}")))
        assertNull(resultado.cliente)
        verify(exactly = 0) { clienteRepository.findByEmail(any()) }
        verify { pedidoRepository.save(any()) }
    }
}

