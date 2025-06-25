  

# Sistema Distribuído de E-commerce com Apache Kafka

  

Um sistema distribuído de microsserviços construído com Java Spring Boot e Apache Kafka para uma plataforma de e-commerce.

  

## Integrantes da Equipe

  

- [Leonardo Moreira] - [202201700]

- [Samuel Jose Alves] - [202201712]

- [Vitor Martins Castanheira] - [202201717]

  

## Visão Geral da Arquitetura

  

Este projeto implementa um sistema distribuído com três microsserviços:

  

1.  **Order-Service** (Produtor) - API REST para criação de pedidos

2.  **Inventory-Service** (Consumidor + Produtor) - Processa pedidos e gerencia o estoque

3.  **Notification-Service** (Consumidor) - Envia notificações com base em eventos de estoque

### Diagramas de Classes dos Serviços

![Diagrama Inventory Service](inventory-service-class-diagram.png)
![Diagrama Notification Service](notification-service-class-diagram.png)
![Diagrama Order Service](order-service-class-diagram.png)

### Fluxo de Mensagens

  

```

Order-Service → [tópico orders] → Inventory-Service → [tópico inventory-events] → Notification-Service

```

  

## Tecnologias Utilizadas

  

-  **Java 17**

-  **Spring Boot 3.1.5**

-  **Apache Kafka** (com Zookeeper)

-  **PostgreSQL**

-  **Docker & Docker Compose**

-  **Maven**

- **Dbeaver**

  

## Estrutura do Projeto

  

```

├── docker-compose.yml # Configuração da infraestrutura
├── sql/init.sql # Script de inicialização do banco de dados
├── create-topics.sh # Script de criação dos tópicos Kafka
├── order-service/ # Microsserviço de pedidos
├── inventory-service/ # Microsserviço de estoque
├── notification-service/ # Microsserviço de notificações
└── README.md # Este arquivo

```

  

## Esquema do Banco de Dados

  

### Tabela `orders`

  

```sql

CREATE  TABLE  orders (

id UUID PRIMARY KEY,

created_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,

items JSONB NOT NULL

);

```

  

### Tabela `inventory`

  

```sql

CREATE  TABLE  inventory (

id SERIAL  PRIMARY KEY,

item_name VARCHAR(255) UNIQUE  NOT NULL,

quantity INTEGER  NOT NULL  DEFAULT  0,

created_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,

updated_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP

);

```

  

## Primeiros Passos

  

### Pré-requisitos

  

- Docker e Docker Compose

- Java 17

  

### Executando o Sistema

  

1.  **Clonar o repositório**

  

```bash

git  clone <url-do-repositorio>

cd  teste-elias

```

  

2.  **Subir a infraestrutura**

  

```bash

docker-compose  up  -d  zookeeper  kafka  postgres-ecommerce

```

  

3.  **Aguardar 30 segundos e criar os tópicos Kafka**

  

```bash

chmod  +x  create-topics.sh

./create-topics.sh

```

  

4.  **Iniciar os microsserviços**

  

```bash

docker-compose  up  -d  order-service  inventory-service  notification-service

```

  

5.  **Verificar se os serviços estão rodando**

  

```bash

docker-compose  ps

```

  

### Testando o Sistema

  

**Criar um pedido:**

  

```bash

curl  -X  POST  http://localhost:8080/orders  -H  "Content-Type: application/json"  -d  '[

{"itemName": "Laptop", "quantity": 2},

{"itemName": "Mouse", "quantity": 1}

]'

```

  

**Acompanhar logs:**

  

```bash

docker  logs  -f  inventory-service

docker  logs  -f  notification-service

```

  

**Ver todos os pedidos:**

  

```bash

curl  http://localhost:8080/orders

```

  

### Exemplos de Requisições

  

**Pedido com sucesso:**

  

```bash

curl  -X  POST  http://localhost:8080/orders  -H  "Content-Type: application/json"  -d  '[{"itemName": "Mouse", "quantity": 5}]'

```

  

**Pedido com estoque insuficiente:**

  

```bash

curl  -X  POST  http://localhost:8080/orders  -H  "Content-Type: application/json"  -d  '[{"itemName": "Laptop", "quantity": 100}]'

```

  

## Funcionalidades do Sistema

  

### Requisitos Funcionais ✅

  

-  **RF-1**: Criação automática dos tópicos Kafka (`orders` e `inventory-events`)

-  **RF-2**: Order-Service fornece API REST (`POST /orders`) com UUID e timestamp

-  **RF-3**: Inventory-Service processa pedidos e publica eventos de sucesso/falha

-  **RF-4**: Notification-Service imprime notificações no console

  

### Requisitos Não Funcionais

  


### 1️⃣ **Escalabilidade**

**Definição**: Escalabilidade é a capacidade de um sistema de lidar com o aumento de carga, mantendo desempenho e estabilidade.

#### 🧩 **Como o Kafka permite escalar:**

-   **Partições**: cada tópico pode ser dividido em múltiplas partições, permitindo processamento paralelo. Cada partição pode ser consumida por uma instância diferente do serviço.
    
-   **Grupos de consumidores**: múltiplas instâncias de um mesmo serviço consumidor (como o `Inventory-Service`) podem trabalhar simultaneamente em um mesmo tópico, contanto que pertençam ao mesmo grupo.
    
-   **Cluster de brokers**: ao adicionar mais brokers no cluster, o Kafka distribui as partições entre eles, aumentando a capacidade total de processamento.
    

#### ✅ **Aplicação no projeto:**

Para escalar o processamento de pedidos:

-   Aumentamos o número de partições no tópico `orders`.
    
-   Executamos múltiplas instâncias do `Inventory-Service`, cada uma processando diferentes partições em paralelo.
    

----------

### 2️⃣ **Tolerância à Falha**

**Definição**: Tolerância à falha é a capacidade de continuar funcionando corretamente mesmo após a falha de algum componente do sistema.

#### 🛡️ **Como o Kafka lida com falhas:**

-   **Replicação de partições**: cada partição pode ter réplicas em outros brokers (usando `replication-factor`). Se o broker líder falhar, uma réplica assume automaticamente.
    
-   **Gerenciamento de offsets**: os consumidores Kafka registram a posição de leitura (offset). Em caso de falha, retomam do ponto exato onde pararam.
    
-   **Retries e dead-letter topics**: podem ser configurados para garantir a reprocessamento ou descarte controlado de mensagens com erro.
    

#### ✅ **Exemplo no projeto:**

Se o broker responsável pelo tópico `inventory-events` falhar:

-   Outro broker com a réplica da partição assume automaticamente como líder.
    
-   O `Notification-Service` continua consumindo sem perda de dados, garantindo disponibilidade do sistema.
    

----------

### 3️⃣ **Idempotência**

**Definição**: Uma operação é idempotente se, mesmo que executada mais de uma vez com os mesmos dados, o resultado final seja o mesmo.

#### 🎯 **Como garantir idempotência no Kafka:**

-   **Producer idempotente**: o Kafka pode ser configurado para impedir duplicações no envio de mensagens, com a opção `enable.idempotence=true`.
    
-   **Chave única no payload**: todas as mensagens devem conter um identificador único (`UUID`). O consumidor verifica se aquela mensagem já foi processada.
    
-   **Controle no banco de dados**: usando o campo `id` como chave primária. Tentativas de inserção duplicadas são rejeitadas naturalmente pelo banco.
    

#### ✅ **Aplicação no projeto:**

-   O `Order-Service` gera um `UUID` único para cada pedido.
    
-   O `Inventory-Service` verifica se já processou aquele ID antes de executar a lógica de reserva de estoque.
    
-   Isso evita o processamento duplicado, mesmo que a mesma mensagem seja recebida duas vezes.
    

----------

### ✅ **Resumo Geral**

Conceito

Mecanismo no Kafka

Aplicação no Projeto

**Escalabilidade**

Partições + múltiplos consumidores

Escala `Inventory-Service` com mais partições e instâncias

**Tolerância à falha**

Replicação + reeleição de líderes + offset

Kafka mantém disponibilidade mesmo com falhas de broker

**Idempotência**

Producer idempotente + chave única

Impede duplicação de pedidos ou notificações

## Monitoramento e Logs

  

**Todos os serviços:**

  

```bash

docker-compose  logs  -f

```

  

**Serviço específico:**

  

```bash

docker  logs  -f  order-service

docker  logs  -f  inventory-service

docker  logs  -f  notification-service

```

  

## Solução de Problemas

  

### Problemas Comuns

  

1. Serviços não iniciam → verifique portas 5432, 9092, 8080

2. Kafka não responde → aguarde até 60 segundos para inicializar

3. Banco inacessível → verifique se o PostgreSQL está rodando

4. Tópico não encontrado → execute `./create-topics.sh`

  

### Comandos Úteis

  

```bash

# Reiniciar tudo

docker-compose  down && docker-compose  up  -d

  

# Listar tópicos Kafka

docker  exec  kafka  kafka-topics  --list  --bootstrap-server  localhost:9092

  

# Consumir mensagens Kafka

docker  exec  kafka  kafka-console-consumer  --topic  orders  --bootstrap-server  localhost:9092  --from-beginning

  

# Consultar banco

docker  exec  -it  postgres-ecommerce  psql  -U  postgres  -d  ecommerce  -c  "SELECT * FROM orders;"

docker  exec  -it  postgres-ecommerce  psql  -U  postgres  -d  ecommerce  -c  "SELECT * FROM inventory;"

```

  

## Desenvolvimento

  

### Rodar localmente

  

```bash

cd  order-service

./mvnw  spring-boot:run

  

cd  inventory-service

./mvnw  spring-boot:run

  

cd  notification-service

./mvnw  spring-boot:run

```

  

**Antes, inicie a infraestrutura:**

  

```bash

docker-compose  up  -d  zookeeper  kafka  postgres-ecommerce

```

  

## Destaques do Projeto

  

-  **Arquitetura orientada a eventos**

-  **Padrão de microsserviços**

-  **Banco de dados por serviço**

-  **Orquestração com Docker Compose**

-  **Alta tolerância a falhas com Kafka**

-  **Escalabilidade horizontal por partições e grupos de consumidores**

  

---

  

_Este projeto demonstra os conceitos principais de sistemas distribuídos, eventos e microsserviços com ferramentas padrão da indústria._

  

## Resumo Rápido

  

**Rodar o sistema:**

  

```bash

docker  compose  up  -d  kafka  inventory-service

# espere alguns segundos

docker  compose  up

```

  

**Banco de dados acessível.**

  

**Ver logs:**

  

```bash

docker  compose  logs  -f  order-service

docker  compose  logs  -f  inventory-service

docker  compose  logs  -f  notification-service

```
