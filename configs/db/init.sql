-- phpMyAdmin SQL Dump
-- version 4.6.6
-- https://www.phpmyadmin.net/
--
-- Máy chủ: localhost
-- Thời gian đã tạo: Th9 23, 2021 lúc 03:40 CH
-- Phiên bản máy phục vụ: 5.7.17-log
-- Phiên bản PHP: 5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `ninja`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `clan`
--

CREATE TABLE `clan` (
  `id` int(11) NOT NULL,
  `name` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '',
  `exp` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '1',
  `itemLevel` int(11) NOT NULL DEFAULT '0',
  `coin` int(11) NOT NULL DEFAULT '1000000',
  `reg_date` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '28/05/2003 16:05:22',
  `log` varchar(5000) COLLATE utf8_bin NOT NULL,
  `alert` varchar(200) COLLATE utf8_bin NOT NULL DEFAULT '',
  `use_card` tinyint(4) NOT NULL DEFAULT '4',
  `openDun` tinyint(4) NOT NULL DEFAULT '3',
  `debt` tinyint(4) NOT NULL DEFAULT '0',
  `members` longtext COLLATE utf8_bin NOT NULL,
  `items` varchar(5000) COLLATE utf8_bin NOT NULL DEFAULT '[]',
  `week` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '2003-05-28 22:22:1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Đang đổ dữ liệu cho bảng `clan`
--

INSERT INTO `clan` (`id`, `name`, `exp`, `level`, `itemLevel`, `coin`, `reg_date`, `log`, `alert`, `use_card`, `openDun`, `debt`, `members`, `items`, `week`) VALUES
(1, 'pkoolvn', 0, 1, 0, 1000000, '2021-09-11 00:11:23', ',0,1000000,2021-09-11 00:11:23\n\n', '', 4, 3, 0, '[[1,\"admin\",\"pkoolvn\",4,130,1,0,0]]', '[]', '2021-09-11 00:11:23'),
(2, 'socbay', 0, 1, 0, 1000000, '2021-09-11 00:23:40', ',0,1000000,2021-09-11 00:23:40\n\n', '', 4, 3, 0, '[[2,\"socbay\",\"socbay\",4,130,2,0,0]]', '[]', '2021-09-11 00:23:40');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `clone_ninja`
--

CREATE TABLE `clone_ninja` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL DEFAULT '',
  `speed` tinyint(4) NOT NULL DEFAULT '4',
  `level` smallint(6) NOT NULL DEFAULT '1',
  `exp` bigint(20) NOT NULL DEFAULT '0',
  `expdown` bigint(20) NOT NULL DEFAULT '0',
  `pk` tinyint(4) NOT NULL DEFAULT '0',
  `ppoint` int(11) NOT NULL DEFAULT '0',
  `potential0` int(11) NOT NULL DEFAULT '15',
  `potential1` int(11) NOT NULL DEFAULT '5',
  `potential2` int(11) NOT NULL DEFAULT '5',
  `potential3` int(11) NOT NULL DEFAULT '5',
  `spoint` int(11) NOT NULL DEFAULT '0',
  `class` tinyint(4) NOT NULL DEFAULT '1',
  `skill` varchar(5000) NOT NULL DEFAULT '[]',
  `KSkill` varchar(100) NOT NULL DEFAULT '[-1,-1,-1]',
  `OSkill` varchar(100) NOT NULL DEFAULT '[-1,-1,-1,-1,-1]',
  `CSkill` smallint(6) NOT NULL DEFAULT '-1',
  `ItemBody` longtext NOT NULL,
  `ItemMounts` longtext NOT NULL,
  `effect` varchar(1000) NOT NULL DEFAULT '[]'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Đang đổ dữ liệu cho bảng `clone_ninja`
--

INSERT INTO `clone_ninja` (`id`, `name`, `speed`, `level`, `exp`, `expdown`, `pk`, `ppoint`, `potential0`, `potential1`, `potential2`, `potential3`, `spoint`, `class`, `skill`, `KSkill`, `OSkill`, `CSkill`, `ItemBody`, `ItemMounts`, `effect`) VALUES
(-10000002, 'socbay', 4, 11, 116500, 0, 0, 0, 15, 5, 5, 5, 0, 0, '[{\"id\":0,\"point\":0}]', '[-1,-1,-1]', '[-1,-1,-1,-1,-1]', -1, '[{\"isLock\":false,\"sale\":0,\"quantity\":1,\"upgrade\":0,\"index\":1,\"id\":194,\"sys\":0,\"isExpires\":false,\"option\":[]}]', '[]', '[]'),
(-10000001, 'admin', 4, 1, 0, 0, 0, 0, 15, 5, 5, 5, 0, 1, '[]', '[-1,-1,-1]', '[-1,-1,-1,-1,-1]', -1, '[]', '[]', '[]');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `naptien`
--

CREATE TABLE `naptien` (
  `id` int(11) NOT NULL,
  `type` varchar(999) NOT NULL,
  `username` varchar(999) NOT NULL,
  `server` varchar(999) NOT NULL,
  `cardType` varchar(999) NOT NULL,
  `cardAmount` varchar(999) NOT NULL,
  `cardCode` varchar(999) NOT NULL,
  `cardSerial` varchar(999) NOT NULL,
  `tranID` varchar(999) NOT NULL,
  `status` int(11) NOT NULL,
  `date` varchar(999) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `ninja`
--
DROP TABLE if EXISTS `ninja`;
CREATE TABLE `ninja` (
  `id` int(11) NOT NULL default 0,
  `name` varchar(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '-1',
  `taskId` smallint(6) NOT NULL DEFAULT '0',
  `head` tinyint(4) NOT NULL DEFAULT '-1',
  `speed` tinyint(4) NOT NULL DEFAULT '4',
  `level` smallint(6) NOT NULL DEFAULT '1',
  `exp` bigint(11) NOT NULL DEFAULT '0',
  `expdown` bigint(20) NOT NULL DEFAULT '0',
  `pk` tinyint(4) NOT NULL DEFAULT '0',
  `ppoint` int(11) NOT NULL DEFAULT '0',
  `potential0` int(11) NOT NULL DEFAULT '15',
  `potential1` int(11) NOT NULL DEFAULT '5',
  `potential2` int(11) NOT NULL DEFAULT '5',
  `potential3` int(11) NOT NULL DEFAULT '5',
  `spoint` int(11) NOT NULL DEFAULT '0',
  `class` tinyint(4) NOT NULL DEFAULT '0',
  `skill` varchar(5000) NOT NULL DEFAULT '[]',
  `KSkill` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '[-1,-1,-1]',
  `OSkill` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '[-1,-1,-1,-1,-1]',
  `CSkill` smallint(6) NOT NULL DEFAULT '0',
  `xu` int(11) NOT NULL DEFAULT '0',
  `xuBox` int(11) NOT NULL DEFAULT '0',
  `yen` int(11) NOT NULL DEFAULT '0',
  `site` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '[22,1678,264,22,0]',
  `maxluggage` tinyint(4) NOT NULL DEFAULT '30',
  `levelBag` smallint(6) NOT NULL DEFAULT '0',
  `ItemBag` longtext CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `ItemBox` longtext NOT NULL,
  `ItemBody` longtext NOT NULL,
  `ItemMounts` longtext NOT NULL,
  `friend` varchar(5000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '[]',
  `effect` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '[]',
  `clan` varchar(200) NOT NULL DEFAULT '["",0]',
  `newlogin` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ddClan` tinyint(1) NOT NULL DEFAULT '0',
  `caveID` int(11) NOT NULL DEFAULT '-1',
  `nCave` int(11) NOT NULL DEFAULT '1',
  `pointCave` int(11) NOT NULL DEFAULT '0',
  `useCave` int(11) NOT NULL DEFAULT '1',
  `bagCaveMax` int(11) NOT NULL DEFAULT '0',
  `itemIDCaveMax` smallint(6) NOT NULL DEFAULT '-1',
  `denbu` tinyint(11) NOT NULL DEFAULT '0',
  `exptype` tinyint(4) NOT NULL DEFAULT '1',
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- AUTO_INCREMENT cho bảng `ninja`
--
ALTER TABLE `ninja`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `player`
--

CREATE TABLE `player` (
  `id` int(11) NOT NULL,
  `username` char(15) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `password` char(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `lock` tinyint(11) NOT NULL DEFAULT '0',
  `email` varchar(999) DEFAULT NULL,
  `otp` int(11) DEFAULT NULL,
  `luong` int(11) NOT NULL DEFAULT '0',
  `ninja` varchar(500) NOT NULL DEFAULT '[]',
  `coin` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `server`
--

CREATE TABLE `server` (
  `id` int(11) NOT NULL,
  `server` varchar(999) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Đang đổ dữ liệu cho bảng `server`
--

INSERT INTO `server` (`id`, `server`) VALUES
(1, 'server1');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `clan`
--
ALTER TABLE `clan`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `clone_ninja`
--
ALTER TABLE `clone_ninja`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `naptien`
--
ALTER TABLE `naptien`
  ADD PRIMARY KEY (`id`);

-- --
-- -- Chỉ mục cho bảng `ninja`
-- --
-- ALTER TABLE `ninja`
--   ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `player`
--
ALTER TABLE `player`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`,`username`);

--
-- Chỉ mục cho bảng `server`
--
ALTER TABLE `server`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `clan`
--
ALTER TABLE `clan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT cho bảng `naptien`
--
ALTER TABLE `naptien`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT cho bảng `ninja`
--
ALTER TABLE `ninja`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT cho bảng `player`
--
ALTER TABLE `player`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT cho bảng `server`
--
ALTER TABLE `server`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
