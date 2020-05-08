-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 08, 2020 at 07:45 AM
-- Server version: 10.4.11-MariaDB
-- PHP Version: 7.4.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `probenapi`
--

-- --------------------------------------------------------

--
-- Table structure for table `probe`
--

CREATE TABLE `probe` (
  `id` bigint(20) NOT NULL,
  `zeitpunkt` timestamp NOT NULL DEFAULT current_timestamp(),
  `messwert` int(11) DEFAULT NULL,
  `ergebnis` varchar(40) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `probe`
  ADD PRIMARY KEY (`id`);
COMMIT;

