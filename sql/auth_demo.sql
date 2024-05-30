/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 5.7.38 : Database - demo
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`demo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `demo`;

/*Table structure for table `t_menus` */

DROP TABLE IF EXISTS `t_menus`;

CREATE TABLE `t_menus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父菜单ID',
  `cover_path` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图路径',
  `name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '菜单名',
  `title` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '标题',
  `describe1` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '第一行描述',
  `describe2` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '第二行描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

/*Data for the table `t_menus` */

/*Table structure for table `t_system_menu` */

DROP TABLE IF EXISTS `t_system_menu`;

CREATE TABLE `t_system_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `permission` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '权限标识',
  `type` tinyint(4) NOT NULL COMMENT '菜单类型',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父菜单ID',
  `path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '路由地址',
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '#' COMMENT '菜单图标',
  `component` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件路径',
  `component_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件名',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '菜单状态',
  `visible` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否可见',
  `keep_alive` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否缓存',
  `always_show` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否总是显示',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

/*Data for the table `t_system_menu` */

insert  into `t_system_menu`(`id`,`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`created_at`,`updated_at`,`deleted_at`) values 
(1,'系统管理','',1,300,0,'/system','fa:gavel',NULL,NULL,0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(100,'用户管理','system:user:list',2,1,1,'user','user','system/user/index','SystemUser',0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(101,'角色管理','',2,2,1,'role','peoples','system/role/index','SystemRole',0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(102,'菜单管理','',2,3,1,'menu','tree-table','system/menu/index','SystemMenu',0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(103,'部门管理','',2,4,1,'dept','tree','system/dept/index','SystemDept',0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(104,'岗位管理','',2,5,1,'post','post','system/post/index','SystemPost',0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(105,'字典管理','',2,6,1,'dict','dict','system/dict/index','SystemDictType',0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(107,'通知公告','',2,8,1,'notice','message','system/notice/index','SystemNotice',0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(108,'审计日志','',1,9,1,'log','log','',NULL,0,'','','','2024-05-21 11:31:53','2024-05-21 11:31:53',NULL),
(1093,'短信管理','',1,11,1,'sms','validCode',NULL,NULL,0,'','','','2024-05-21 11:31:54','2024-05-21 11:31:54',NULL),
(1110,'错误码管理','',2,12,1,'error-code','code','system/errorCode/index','SystemErrorCode',0,'','','','2024-05-21 11:31:54','2024-05-21 11:31:54',NULL),
(1224,'租户管理','',2,0,1,'tenant','peoples',NULL,NULL,0,'','','','2024-05-21 11:31:54','2024-05-21 11:31:54',NULL),
(1247,'敏感词管理','',2,13,1,'sensitive-word','education','system/sensitiveWord/index','SystemSensitiveWord',0,'','','','2024-05-21 11:31:54','2024-05-21 11:31:54',NULL),
(1261,'OAuth 2.0','',1,10,1,'oauth2','people',NULL,NULL,0,'','','','2024-05-21 11:31:54','2024-05-21 11:31:54',NULL),
(2083,'地区管理','',2,14,1,'area','row','system/area/index','SystemArea',0,'','','','2024-05-21 11:31:54','2024-05-21 11:31:54',NULL),
(2130,'邮箱管理','',2,11,1,'mail','email',NULL,NULL,0,'','','','2024-05-21 11:31:54','2024-05-21 11:31:54',NULL),
(2144,'站内信管理','',1,11,1,'notify','message',NULL,NULL,0,'','','','2024-05-21 11:31:54','2024-05-21 11:31:54',NULL);

/*Table structure for table `t_system_role_menu` */

DROP TABLE IF EXISTS `t_system_role_menu`;

CREATE TABLE `t_system_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增编号',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1516 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';

/*Data for the table `t_system_role_menu` */

insert  into `t_system_role_menu`(`id`,`role_id`,`menu_id`,`created_at`,`updated_at`,`deleted_at`) values 
(1489,2,1,'2024-05-21 11:36:05','2024-05-21 11:36:05',NULL),
(1499,2,1093,'2024-05-21 11:36:05','2024-05-21 11:36:05',NULL),
(1503,2,1110,'2024-05-21 11:36:05','2024-05-21 11:36:05',NULL),
(1506,2,100,'2024-05-21 11:36:05','2024-05-21 11:36:05',NULL),
(1507,2,101,'2024-05-21 11:36:05','2024-05-21 11:36:05',NULL),
(1508,2,102,'2024-05-21 11:36:05','2024-05-21 11:36:05',NULL),
(1510,2,103,'2024-05-21 11:36:06','2024-05-21 11:36:06',NULL),
(1511,2,104,'2024-05-21 11:36:06','2024-05-21 11:36:06',NULL),
(1512,2,105,'2024-05-21 11:36:06','2024-05-21 11:36:06',NULL),
(1514,2,107,'2024-05-21 11:36:06','2024-05-21 11:36:06',NULL),
(1515,2,108,'2024-05-21 11:36:06','2024-05-21 11:36:06',NULL);

/*Table structure for table `t_system_role_users` */

DROP TABLE IF EXISTS `t_system_role_users`;

CREATE TABLE `t_system_role_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

/*Data for the table `t_system_role_users` */

insert  into `t_system_role_users`(`id`,`user_id`,`role_id`,`created_at`,`updated_at`,`deleted_at`) values 
(43,509,2,'2024-05-21 11:37:08','2024-05-21 11:37:08',NULL),
(44,1,1,'2024-05-23 15:14:56','2024-05-23 15:14:56',NULL);

/*Table structure for table `t_system_roles` */

DROP TABLE IF EXISTS `t_system_roles`;

CREATE TABLE `t_system_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_tag` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色标识',
  `sort` int(11) NOT NULL COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '角色状态（1正常 0停用）',
  `type` tinyint(4) NOT NULL DEFAULT '2' COMMENT '角色类型',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色信息表';

/*Data for the table `t_system_roles` */

insert  into `t_system_roles`(`id`,`name`,`role_tag`,`sort`,`status`,`type`,`description`,`created_at`,`updated_at`,`deleted_at`) values 
(1,'超级管理员','super_admin',1,1,1,'超级管理员','2024-05-21 11:29:29','2024-05-21 11:29:29',NULL),
(2,'普通用户','common',2,1,3,'普通用户','2024-05-21 11:34:48','2024-05-21 11:34:48',NULL);

/*Table structure for table `t_system_users` */

DROP TABLE IF EXISTS `t_system_users`;

CREATE TABLE `t_system_users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号名称',
  `password` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名称',
  `phone` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '手机号',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '邮箱',
  `avatar` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '头像',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=511 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统用户表';

/*Data for the table `t_system_users` */

insert  into `t_system_users`(`id`,`username`,`password`,`name`,`phone`,`email`,`avatar`,`created_at`,`updated_at`,`deleted_at`) values 
(1,'admin','$2a$10$6FTF2w6YR6H2k65rQX0Ee.Jw68D5EC/eO7i40NhUoje0Lt6wusFsu','admin','','','','2021-05-13 16:11:48','2023-08-22 17:39:32',NULL),
(509,'root','$2a$10$6FTF2w6YR6H2k65rQX0Ee.Jw68D5EC/eO7i40NhUoje0Lt6wusFsu','root','','','','2024-05-21 11:36:39','2024-05-21 11:36:39',NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
