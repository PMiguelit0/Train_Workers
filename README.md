# Train Workers

Este é um projeto da cadeira de Sistemas Operacionais que demonstra o uso de threads e semáforos em Java.

## Requisitos

* [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/#java21) ou superior
* Não é necessário instalar o Maven (o projeto usa Maven Wrapper)

## Como executar o projeto

### Opção 1: Executar diretamente com Maven Wrapper
1. Clone o repositório:
```bash
git clone https://github.com/PMiguelit0/Train_Workers.git
cd Train_Workers
```

2. Execute o projeto:
```bash
# No Windows:
.\mvnw.cmd clean javafx:run

# No Linux/Mac:
./mvnw clean javafx:run
```

### Opção 2: Criar e executar JAR
1. Clone o repositório:
```bash
git clone https://github.com/PMiguelit0/Train_Workers.git
cd Train_Workers
```

2. Crie o arquivo JAR:
```bash
# No Windows:
.\mvnw.cmd clean package

# No Linux/Mac:
./mvnw clean package
```

3. Execute o JAR:
```bash
java -jar target/Train_Workers-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Observações
- O Maven Wrapper (mvnw) baixará automaticamente todas as dependências necessárias na primeira execução
- Certifique-se de ter o Java 21 instalado e configurado corretamente em sua máquina

## Tecnologias Utilizadas

* Java 21 - Linguagem de programação
* JavaFX - Framework para interface gráfica
* Maven - Gerenciador de dependências (via Maven Wrapper)
