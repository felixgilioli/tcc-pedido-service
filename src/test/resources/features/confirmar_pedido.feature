# language: pt
Funcionalidade: Confirmar pedido
  Como cliente do sistema de pedidos
  Quero confirmar um pedido criado com itens válidos
  Para que o pagamento seja solicitado e o pedido siga o fluxo normal

  Cenario: Confirmar um pedido com dois itens validos
    Dado que existe um pedido criado
    E os seguintes produtos cadastrados:
      | id                            | preco  |
      | 00000000-0000-0000-0000-000000000001 | 10.00 |
      | 00000000-0000-0000-0000-000000000002 | 5.50  |
    E o pedido possui os seguintes itens:
      | produtoId                     | quantidade |
      | 00000000-0000-0000-0000-000000000001 | 2         |
      | 00000000-0000-0000-0000-000000000002 | 1         |
    Quando eu confirmar o pedido
    Entao o status do pedido deve ser "PAGAMENTO_SOLICITADO"
    E o total do pedido deve ser "25.50"
    E o pagamento deve ser solicitado
