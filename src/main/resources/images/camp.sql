-- phpMyAdmin SQL Dump
-- version 5.1.1deb5ubuntu1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Tempo de geração: 21/10/2025 às 16:55
-- Versão do servidor: 8.0.43-0ubuntu0.22.04.1
-- Versão do PHP: 8.1.2-1ubuntu2.22

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `camp`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `jogos`
--

CREATE TABLE `jogos` (
  `id` int NOT NULL,
  `time_a` int NOT NULL,
  `time_b` int NOT NULL,
  `gols_a` int NOT NULL,
  `gols_b` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `jogos`
--

INSERT INTO `jogos` (`id`, `time_a`, `time_b`, `gols_a`, `gols_b`) VALUES
(1, 2, 1, 0, 2),
(2, 3, 4, 1, 1),
(3, 1, 4, 1, 0),
(4, 3, 2, 5, 1),
(5, 5, 6, 4, 0),
(6, 5, 8, 2, 1);

-- --------------------------------------------------------

--
-- Estrutura stand-in para view `ponto`
-- (Veja abaixo para a visão atual)
--
CREATE TABLE `ponto` (
`nome` varchar(100)
,`pontos` bigint
);

-- --------------------------------------------------------

--
-- Estrutura stand-in para view `pontos`
-- (Veja abaixo para a visão atual)
--
CREATE TABLE `pontos` (
`nome` varchar(100)
,`pontuacao` decimal(41,0)
);

-- --------------------------------------------------------

--
-- Estrutura para tabela `times`
--

CREATE TABLE `times` (
  `id` int NOT NULL,
  `nome` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `times`
--

INSERT INTO `times` (`id`, `nome`) VALUES
(1, 'Floresta'),
(2, 'Atletico'),
(3, 'Real'),
(4, 'Regional'),
(5, 'IFCE'),
(6, 'UFC'),
(7, 'UECE'),
(8, 'Unifor');

-- --------------------------------------------------------

--
-- Estrutura para view `ponto`
--
DROP TABLE IF EXISTS `ponto`;

CREATE ALGORITHM=UNDEFINED DEFINER=`mauricio`@`%` SQL SECURITY DEFINER VIEW `ponto`  AS SELECT `t`.`nome` AS `nome`, (count(`t`.`nome`) * 3) AS `pontos` FROM (`jogos` `j` join `times` `t`) WHERE ((`j`.`time_a` = `t`.`id`) AND (`j`.`gols_a` > `j`.`gols_b`)) GROUP BY `t`.`nome` ;

-- --------------------------------------------------------

--
-- Estrutura para view `pontos`
--
DROP TABLE IF EXISTS `pontos`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `pontos`  AS SELECT `t1`.`nome` AS `nome`, sum(`t1`.`pontos`) AS `pontuacao` FROM (select `j`.`id` AS `id`,`t`.`nome` AS `nome`,3 AS `pontos` from (`jogos` `j` join `times` `t`) where ((`j`.`time_a` = `t`.`id`) and (`j`.`gols_a` > `j`.`gols_b`)) union select `j`.`id` AS `id`,`t`.`nome` AS `nome`,3 AS `pontos` from (`jogos` `j` join `times` `t`) where ((`j`.`time_b` = `t`.`id`) and (`j`.`gols_a` < `j`.`gols_b`)) union select `j`.`id` AS `id`,`t`.`nome` AS `nome`,1 AS `pontos` from (`jogos` `j` join `times` `t`) where ((`j`.`time_a` = `t`.`id`) and (`j`.`gols_a` = `j`.`gols_b`)) union select `j`.`id` AS `id`,`t`.`nome` AS `nome`,1 AS `pontos` from (`jogos` `j` join `times` `t`) where ((`j`.`time_b` = `t`.`id`) and (`j`.`gols_a` = `j`.`gols_b`))) AS `t1` GROUP BY `t1`.`nome` ORDER BY `pontuacao` DESC ;

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `jogos`
--
ALTER TABLE `jogos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `time_b` (`time_b`),
  ADD KEY `time_a` (`time_a`);

--
-- Índices de tabela `times`
--
ALTER TABLE `times`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `jogos`
--
ALTER TABLE `jogos`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de tabela `times`
--
ALTER TABLE `times`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `jogos`
--
ALTER TABLE `jogos`
  ADD CONSTRAINT `jogos_ibfk_1` FOREIGN KEY (`time_a`) REFERENCES `times` (`id`),
  ADD CONSTRAINT `jogos_ibfk_2` FOREIGN KEY (`time_b`) REFERENCES `times` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
