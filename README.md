# tcc-pedido-service

O **tcc-pedido-service** é um microsserviço responsável pelo fluxo de **pedidos** do sistema de autoatendimento para lanchonetes. Ele faz parte de uma arquitetura de microsserviços.

Este serviço gerencia o ciclo de vida dos pedidos, desde a criação até a finalização, permitindo que outros componentes do sistema acompanhem e atualizem o status de cada pedido.

## Funcionalidades

- Criação de pedidos
- Confirmação de pedidos
- Atualização de status do pedido (criado, confirmado, em preparação, pronto, finalizado, cancelado)
- Consulta de pedidos por ID e por cliente
- Integração HTTP com serviços externos (ex.: pagamento, catálogo, produção)
- Emissão/consumo de eventos para atualização de status em outros serviços

> Observação: este repositório é focado **exclusivamente** na parte de **pedidos**. O cadastro de produtos, clientes, pagamentos, etc., é responsabilidade de outros serviços da solução.

## Arquitetura do Projeto

### Tecnologias Utilizadas

- Kotlin
- Spring Boot
- Gradle
- JUnit 5 e MockK (testes unitários)
- Cucumber (testes BDD)
- Swagger / OpenAPI (documentação da API)
- Integrações HTTP (REST) com outros serviços
- Docker e Docker Compose
- Kubernetes (manifests em `k8s/`)
- GitHub Actions (CI/CD)
- MongoDB (banco de dados de pedidos)

### Estrutura do Projeto

O projeto segue princípios de **Clean Architecture**, promovendo separação de responsabilidades, independência de frameworks e facilidade de manutenção.

De forma geral, a estrutura está organizada em pacotes Kotlin sob `src/main/kotlin`:

- `br.com.felixgilioli.pedido.entity`  
  Entidades de domínio do pedido (ex.: `Pedido`, `ItemPedido`, `StatusPedido`).

- `br.com.felixgilioli.pedido.usecase`  
  Casos de uso / regras de negócio (ex.: criar pedido, confirmar pedido, atualizar status).

- `br.com.felixgilioli.pedido.controller`  
  Entrypoints HTTP (REST Controllers) que expõem as APIs de pedidos.

- `br.com.felixgilioli.pedido.dto.request` e `br.com.felixgilioli.pedido.dto.response`  
  Objetos de entrada e saída da API (contratos REST).

- `br.com.felixgilioli.pedido.repository.http`  
  Clientes HTTP para comunicação com outros serviços (pagamento, catálogo, etc.).

A comunicação entre as partes internas é feita via interfaces e DTOs específicos, promovendo baixo acoplamento e alta testabilidade.

### Documentação da API

A documentação da API (Swagger / OpenAPI) é exposta via endpoint HTTP quando a aplicação está em execução, por exemplo:

- `http://localhost:8080/swagger-ui/index.html`

(O caminho exato pode variar conforme a configuração do Springdoc.)

## Como executar localmente

### Requisitos

- Java 21 instalado
- Docker + Docker Compose (para dependências, se necessário)
- Acesso à internet (para baixar dependências do Gradle)

### Passo a passo

1. Clone o repositório:

   ```sh
   git clone https://github.com/felixgilioli/tcc-pedido-service.git
   cd tcc-pedido-service
   ```

2. (Opcional, mas recomendado) Suba as dependências com Docker Compose:

   ```sh
   cd local
   docker-compose up -d
   cd ..
   ```

3. Execute o projeto informando o profile e as variáveis de ambiente necessárias.

   #### Windows (PowerShell)

   ```powershell
   # Exemplos de variáveis, ajuste para o que estiver em application.properties
   $env:PAGAMENTO_SERVICE_URL="http://localhost:8081"
   $env:CATALOGO_SERVICE_URL="http://localhost:8082"
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

   #### Linux / macOS

   ```bash
   export PAGAMENTO_SERVICE_URL=http://localhost:8081
   export CATALOGO_SERVICE_URL=http://localhost:8082
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

4. Acesse:

   - API: `http://localhost:8080`
   - Swagger: `http://localhost:8080/swagger-ui/index.html`

## Como testar a aplicação

Os testes automatizados incluem:

- **Testes de unidade** (JUnit + MockK)
- **Testes BDD com Cucumber**, localizados em `src/test/resources/features` (por exemplo, `confirmar_pedido.feature`).

Para rodar todos os testes:

```sh
./gradlew test
```

Os relatórios de testes e cobertura (JaCoCo) podem ser encontrados em:

- `build/reports/tests/test/index.html`
- `build/reports/jacoco/test/html/index.html`

Se desejar, você pode criar uma coleção no Postman apontando para os endpoints expostos pelos controllers de pedido.

## Infraestrutura

### Kubernetes

Na pasta `k8s/` estão os manifests para deploy em Kubernetes:

- `deployment.yaml` – Deployment do `tcc-pedido-service`
- `service.yaml` – Service (exposição interna/externa no cluster)
- `hpa.yaml` – Horizontal Pod Autoscaler
- `namespace.yaml` – Namespace utilizado
- `secrets.yaml` – Definição de Secrets (credenciais/URLs)

Esses arquivos podem ser aplicados em um cluster Kubernetes (local ou em cloud), por exemplo:

```sh
kubectl apply -k k8s/
```

### CI/CD

A esteira de CI/CD (usualmente configurada via GitHub Actions) é responsável por:

- Executar testes automatizados
- Construir o artefato (JAR)
- Montar e publicar a imagem Docker
- Atualizar o deployment em um cluster Kubernetes (por exemplo, EKS)

### Cloud e Infraestrutura como Código

A infraestrutura da solução como um todo (cluster, banco de dados, rede etc.) é gerenciada em um repositório separado via Terraform, por exemplo:

```text
https://github.com/felixgilioli/tcc-infrastructure-tf
```

O Terraform é responsável por:

- Criar o cluster Kubernetes (ex.: EKS)
- Criar e configurar instâncias de banco de dados (ex.: PostgreSQL/RDS)
- Configurar rede (VPC, subnets, security groups)
- Armazenar o estado em um bucket S3 (ou equivalente)

---

Este README descreve especificamente o **tcc-pedido-service**, focando no ciclo de vida de **pedidos** dentro da arquitetura de microsserviços do projeto de TCC.
