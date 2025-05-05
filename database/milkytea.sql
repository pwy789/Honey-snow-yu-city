/*
 Navicat Premium Dump SQL

 Source Server         : 本机MySQL
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : milkytea

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 05/05/2025 22:53:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `user_id` varchar(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
  `gender` tinyint UNSIGNED NOT NULL COMMENT '性别 0 男 1 女',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `detail` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址,如门牌号等',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(9, 6) NULL DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES ('1913112300781305857', 'oo__868CYhei8IqGvhFELutyBe20', '彭文宇', 0, '18508459785', '和馨园B区2期4栋', '3单元1309', 112.858562, 28.211240);
INSERT INTO `address` VALUES ('1913112852638466049', 'oo__868CYhei8IqGvhFELutyBe20', '彭加余', 0, '13548972192', '麓谷·和馨园2期B区2栋', '1107', 112.858251, 28.211715);

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `user_id` char(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `goods_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品id',
  `sku_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品选中的sku',
  `count` int NOT NULL COMMENT '商品数量',
  `price` int NULL DEFAULT NULL COMMENT '价格',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------

-- ----------------------------
-- Table structure for dimension
-- ----------------------------
DROP TABLE IF EXISTS `dimension`;
CREATE TABLE `dimension`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `sort` int NOT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品维度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dimension
-- ----------------------------
INSERT INTO `dimension` VALUES (1, '杯型', 1);
INSERT INTO `dimension` VALUES (2, '糖度', 3);
INSERT INTO `dimension` VALUES (3, '加料', 4);
INSERT INTO `dimension` VALUES (4, '温度', 2);

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '员工姓名',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `shop_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属的店铺id',
  `identify` tinyint NOT NULL DEFAULT 1 COMMENT '员工身份 0 管理员 1 普通员工',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES ('1', '彭文宇', '18508459785', 'mxycpwy', '$2a$10$id0Qg7kCddon0U2g4lFZSuogAdiUV.bXw4grLbgCKt7GVF5it67zq', '1911681644323377154', 1);
INSERT INTO `employee` VALUES ('1918571828058566657', '彭加余', '13548972192', 'mxycpjy', '$2a$10$KEuz32t6jSAMKp5/Xjwtd.APw6OzjcCDALGQxJoV6ntIcHMFh5aty', '1911681644323377154', 1);
INSERT INTO `employee` VALUES ('2', '管理员', '13755088660', 'admin', '$2a$10$6c9UzPn5mzjsWemEZYJfde4nWIgjKKcoq7TV69AI4Rs6LclWktNmi', '1911681644323377154', 0);

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键Id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `category_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品分类id',
  `price` int NULL DEFAULT NULL COMMENT '商品价格',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品描述信息',
  `balance` int UNSIGNED NULL DEFAULT 0 COMMENT '库存',
  `status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '状态 0 停售 1 起售',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '封面图',
  `introduction` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品简介',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('1905606593278390273', '满杯百香果', '1904458974074347522', 8, '满杯橙柚，清新果香满溢杯间\n主辅料：鲜榨橙汁、柚子果肉、Q 弹椰果、茉莉茶汤\n注：热饮版本不含茉莉茶汤\n【杯型容量】\n中杯 500mL\n大杯 650mL\n【杯型容量说明】：此为杯内容积参考数据，用于预估出餐量，并非实际饮品含量。为防止饮品过满导致无法封杯或撒漏，可能存在未满杯情况，实际饮品含量以门店出餐为准。', 122, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/cea22ef790bd476a8c56638c0c4d28bf.png', '满杯都是吗？有点意思。');
INSERT INTO `goods` VALUES ('1905916961678798850', '酷炫冰淇淋', '1904458164477206530', 2, '主辅料：优质鲜牛奶、特制冰淇淋块、Q 弹椰果、醇香红茶汤\n注：冷饮限定，热饮无冰淇淋块\n【杯型容量】\n中杯 480mL\n大杯 630mL\n【杯型容量说明】：此为杯内容积参考数据，用于预估出餐量，并非实际饮品含量。为防止饮品过满导致无法封杯或撒漏，可能存在未满杯情况，实际饮品含量以门店出餐为准。\n注：杯型容量数据因遵循相关国家标准，存在合理误差范围。', 221, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/b0ab68dfb209425d9a010607d334cb9b.png', '多彩酷炫，冰爽舌尖新体验');
INSERT INTO `goods` VALUES ('1905917857259171842', '椰果奶茶', '1904457532286541825', 6, '主辅料：精选红茶、浓郁牛奶、饱满椰果\n注：热饮冷饮均可，风味各有千秋\n【杯型容量】\n中杯 500mL\n大杯 650mL\n【杯型容量说明】：此为杯内容积参考数据，用于预估出餐量，并非实际饮品含量。为防止饮品过满导致无法封杯或撒漏，可能存在未满杯情况，实际饮品含量以门店出餐为准。\n注：杯型容量数据因遵循相关国家标准，存在合理误差范围。', 114, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/94d5338f253e4746879c343cdc82610b.png', 'Q 弹椰果配奶茶，超好喝');
INSERT INTO `goods` VALUES ('1905918546202963970', '拿铁咖啡', '1904458050052399105', 7, '主辅料：阿拉比卡咖啡豆萃取浓缩咖啡、新鲜纯牛奶\n注：热饮口感醇厚，冷饮清爽提神\n【杯型容量】\n中杯 450mL\n大杯 600mL\n【杯型容量说明】：此为杯内容积参考数据，用于预估出餐量，并非实际饮品含量。为防止饮品过满导致无法封杯或撒漏，可能存在未满杯情况，实际饮品含量以门店出餐为准。\n注：杯型容量数据因遵循相关国家标准，存在合理误差范围。', 212, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/5856e0f0ac7c42f2ad8080a6c30903da.png', '奶香咖啡交融，浓郁香醇');
INSERT INTO `goods` VALUES ('1905919339446513665', '超值柠檬水', '1904458974074347522', 4, '主辅料：新鲜柠檬切片、冰糖、纯净水\n注：冷饮消暑解渴，热饮可舒缓咽喉\n【杯型容量】\n中杯 550mL\n大杯 700mL\n【杯型容量说明】：此为杯内容积参考数据，用于预估出餐量，并非实际饮品含量。为防止饮品过满导致无法封杯或撒漏，可能存在未满杯情况，实际饮品含量以门店出餐为准。\n注：杯型容量数据因遵循相关国家标准，存在合理误差范围。', 51, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/d82333662b664918b8194c35d6a0b577.png', '清爽酸甜，超高性价比');
INSERT INTO `goods` VALUES ('1905920946414399490', '草莓芝士', '1904460273163542530', 7, '主辅料：新鲜草莓果泥、细腻芝士奶盖、茉莉茶底\n注：冷饮最佳赏味，芝士奶盖遇热易融化\n【杯型容量】\n中杯 520mL\n大杯 670mL\n【杯型容量说明】：此为杯内容积参考数据，用于预估出餐量，并非实际饮品含量。为防止饮品过满导致无法封杯或撒漏，可能存在未满杯情况，实际饮品含量以门店出餐为准。\n注：杯型容量数据因遵循相关国家标准，存在合理误差范围。', 65, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/1286b2e3a2b04e3f82c5071eecba9401.png', '草莓邂逅芝士，甜蜜诱惑');
INSERT INTO `goods` VALUES ('1905923076688818177', '清新绿茶', '1904459836045762561', 5, '主辅料：优质绿茶茶叶、适量冰糖（可选）\n注：冷饮沁人心脾，热饮感受茶香本味\n【杯型容量】\n中杯 500mL\n大杯 650mL\n【杯型容量说明】：此为杯内容积参考数据，用于预估出餐量，并非实际饮品含量。为防止饮品过满导致无法封杯或撒漏，可能存在未满杯情况，实际饮品含量以门店出餐为准。\n注：杯型容量数据因遵循相关国家标准，存在合理误差范围。', 37, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/614fd70c6ce14309bad778e52559a0a2.png', '茶香清新，舒缓身心疲惫');
INSERT INTO `goods` VALUES ('1907373192960376834', '经典轻乳', '1907371970543058945', 6, '主辅料：轻柔乳底、精选红茶汤\n注：热饮暖身暖心，冷饮别具风味\n【杯型容量】\n中杯 480mL\n大杯 630mL\n【杯型容量说明】：此为杯内容积参考数据，用于预估出餐量，并非实际饮品含量。为防止饮品过满导致无法封杯或撒漏，可能存在未满杯情况，实际饮品含量以门店出餐为准。\n注：杯型容量数据因遵循相关国家标准，存在合理误差范围。\n', 58, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/02/37153fb210674265b5560bf73ffd81e2.png', '经典风味轻乳，口感超赞');
INSERT INTO `goods` VALUES ('1917163601848791042', '素毛肚', '1907377442608218114', 1, '片片饱满，入口脆弹\n精选优质魔芋，口感好\n甄选品质辣椒，辣香醇厚，直击味蕾\n便携小包装，随吃随拿\n规格：20g/袋\n保质期：9个月', 841, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/d5f25d8ee7694c138dfd4fa941ce42b5.png', '片片饱满，入口脆弹');
INSERT INTO `goods` VALUES ('1917164495524950017', '蓝莓果粒茶', '1907408184306401281', 7, '蓝莓养眼，多喝一点\n养眼：仅指本产品好看高颜值，不涉及任何有关改善视力或对眼睛健康有积极\n作用的含义。\n主辅料：冷冻蓝莓果浆、绿茶汤\n【杯型容量：中杯505mL】\n【杯型容量】即杯容积，非实际含量。为避免过满无法封杯或撒漏，可能存在\n不完全满杯情况，克重为预估出餐量参考数据，实际含量请以门店出餐为准\n注：杯型容量数据基于相关国家标准存在合理偏差。', 424, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/f8f300ff49f442ad887497bf8ef817dd.png', '蓝莓养眼，多喝一点');
INSERT INTO `goods` VALUES ('1917165661201723393', '椰椰拿铁', '1904458050052399105', 8, '雪王咖啡，奋斗一杯\n主辅料：鲜萃咖啡液、鲜牛乳奶基底、厚椰乳\n【杯型容量：中杯：杯容量505mL】\n【杯型容量】即杯容积，非实际含量。为避免过满无法封杯或撒漏，可能存在\n不完全满杯情况，克重为预估出餐量参考数据，实际含量请以门店出餐为准。\n注：杯型容量数据基于相关国家标准存在合理偏差。', 245, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/d1edff83d61048f5aa61473e6528348c.png', '雪王咖啡，奋斗一杯');
INSERT INTO `goods` VALUES ('1917166485810278402', '蜜桃四季春', '1904458974074347522', 7, '蜜桃四季春，四季桃花运\n主辅料：冷冻水蜜桃果浆、四季春茶、晶球\n【杯型容量：大杯660mL】\n【杯型容量】即杯容积/杯容量，为预估出餐量参考数据，非实际含量。\n为避免饮品过满无法封杯或撒漏，可能存在不完全满杯情况，实际含量请以门\n店出餐为准。\n注：杯型容量数据基于相关国家标准存在合理偏差。', 432, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/7662608f1acd4841a020868604b0b805.png', '蜜桃四季春，四季桃花运');
INSERT INTO `goods` VALUES ('1917168182657904641', '棒打鲜橙', '1904458974074347522', 6, '主辅料：鲜橙、橙柚酱、绿茶\n【杯型容量：大杯660mL】\n【杯型容量】即杯容积/杯容量，为预估出餐量参考数据，非实际含量\n为避免饮品过满无法封杯或撒漏，可能存在不完全满杯情况，实际含量请以门\n店出餐为准。\n注：杯型容量数据基于相关国家标准存在合理偏差。', 256, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/f5ff6fdbb70a4bd3a6406787372f933b.png', '大片橙子真给力，一杯满满有维c');
INSERT INTO `goods` VALUES ('1917172414098272258', '芋圆葡萄', '1904458974074347522', 8, '大颗葡萄肉，酸甜嚼不够\n*芋圆葡萄原料升级切换期间，存在带葡萄肉和不带葡萄肉区别，具体以\n门店实际售卖为准\n主辅料：芋圆、冷冻葡萄果肉、冷冻红葡萄汁\n【杯型容量：大杯：杯容量660mL】\n【杯型容量】即杯容积，非实际含量。为避免过满无法封杯或撒漏，可能\n存在不完全满杯情况，克重为预估出餐量参考数据，实际含量请以门店出\n餐为准。\n注：杯型容量数据基于相关国家标准存在合理偏差。', 321, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/b29ce18968844642958daeeef19c8a0e.png', '大颗葡萄肉，酸甜嚼不够');

-- ----------------------------
-- Table structure for goods_category
-- ----------------------------
DROP TABLE IF EXISTS `goods_category`;
CREATE TABLE `goods_category`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类名称',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述信息',
  `status` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '状态:0 停售 1 起售',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods_category
-- ----------------------------
INSERT INTO `goods_category` VALUES ('1904457532286541825', '经典奶茶', 1, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/7936540a07b34458a0ad0669fce97392.png', '最正宗最纯粹的分类，犹豫不决就选这个。', 1);
INSERT INTO `goods_category` VALUES ('1904458050052399105', '鲜萃咖啡', 3, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/4a7de6f99b874844831005c2239442dc.png', '冰块占了一半。', 1);
INSERT INTO `goods_category` VALUES ('1904458164477206530', '暴爽冰淇淋', 2, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/e580f90bda194c1c9dcbf7efecaca74f.png', '夏天到了，不来一口暴爽暴爽的冰淇淋吗？', 1);
INSERT INTO `goods_category` VALUES ('1904458974074347522', '新鲜果茶', 5, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/8bd9655d9b914a7d810b3ca6bdbdbf13.png', '不是我的柠檬隔夜了，而是你来晚了。', 1);
INSERT INTO `goods_category` VALUES ('1904459836045762561', '香味纯茶', 4, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/6adee8e237404d4eae7e4ec83e733fd1.png', '不喜欢喝茶的要接收大调查了。', 1);
INSERT INTO `goods_category` VALUES ('1904460273163542530', '醇香芝士', 11, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/7f6da63c1eaf4c8f908124edebdc5a3e.png', '喝了涨芝士。', 1);
INSERT INTO `goods_category` VALUES ('1907371970543058945', '魅力轻乳', 6, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/7f78e3487d4541f8a3f89f96fdc7fc33.png', '好喝到爆炸', 1);
INSERT INTO `goods_category` VALUES ('1907377442608218114', '  雪王零食', 9, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/47abd5fd0bc74d6b971dee3de0be43a1.png', NULL, 1);
INSERT INTO `goods_category` VALUES ('1907408184306401281', '新品尝鲜', 10, 'http://192.168.204.133:9000/milky-tea/goodsCategory/2025/04/02/447a9eb7a04c4011bbe5b43c4c213dea.png', NULL, 1);

-- ----------------------------
-- Table structure for goods_pictures
-- ----------------------------
DROP TABLE IF EXISTS `goods_pictures`;
CREATE TABLE `goods_pictures`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `goods_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品id',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods_pictures
-- ----------------------------
INSERT INTO `goods_pictures` VALUES (9, '1905606593278390273', 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/cea22ef790bd476a8c56638c0c4d28bf.png');
INSERT INTO `goods_pictures` VALUES (12, '1905916961678798850', 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/b0ab68dfb209425d9a010607d334cb9b.png');
INSERT INTO `goods_pictures` VALUES (13, '1905917857259171842', 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/94d5338f253e4746879c343cdc82610b.png');
INSERT INTO `goods_pictures` VALUES (14, '1905918546202963970', 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/5856e0f0ac7c42f2ad8080a6c30903da.png');
INSERT INTO `goods_pictures` VALUES (15, '1905919339446513665', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/d82333662b664918b8194c35d6a0b577.png');
INSERT INTO `goods_pictures` VALUES (17, '1905923076688818177', 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/614fd70c6ce14309bad778e52559a0a2.png');
INSERT INTO `goods_pictures` VALUES (19, '1907373192960376834', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/02/37153fb210674265b5560bf73ffd81e2.png');
INSERT INTO `goods_pictures` VALUES (20, '1905920946414399490', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/1286b2e3a2b04e3f82c5071eecba9401.png');
INSERT INTO `goods_pictures` VALUES (21, '1905920946414399490', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/24138f0ae60e4b05baf7e5b133842595.png');
INSERT INTO `goods_pictures` VALUES (22, '1905606593278390273', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/ef92887b293745f9b6e99fda54a5ed4c.png');
INSERT INTO `goods_pictures` VALUES (23, '1905606593278390273', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/48ae37c887aa4ba6bf622198f76ae1c8.png');
INSERT INTO `goods_pictures` VALUES (24, '1917163601848791042', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/d5f25d8ee7694c138dfd4fa941ce42b5.png');
INSERT INTO `goods_pictures` VALUES (25, '1917164495524950017', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/f8f300ff49f442ad887497bf8ef817dd.png');
INSERT INTO `goods_pictures` VALUES (26, '1917165661201723393', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/d1edff83d61048f5aa61473e6528348c.png');
INSERT INTO `goods_pictures` VALUES (27, '1917166485810278402', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/7662608f1acd4841a020868604b0b805.png');
INSERT INTO `goods_pictures` VALUES (28, '1917168182657904641', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/f5ff6fdbb70a4bd3a6406787372f933b.png');
INSERT INTO `goods_pictures` VALUES (29, '1917172414098272258', 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/b29ce18968844642958daeeef19c8a0e.png');

-- ----------------------------
-- Table structure for goods_sku
-- ----------------------------
DROP TABLE IF EXISTS `goods_sku`;
CREATE TABLE `goods_sku`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `goods_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品id',
  `sku_id` int NOT NULL COMMENT 'skuid',
  `price` int NULL DEFAULT NULL COMMENT '价格',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 137 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品-sku表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods_sku
-- ----------------------------
INSERT INTO `goods_sku` VALUES (4, '1905606593278390273', 5, NULL);
INSERT INTO `goods_sku` VALUES (5, '1905606593278390273', 6, NULL);
INSERT INTO `goods_sku` VALUES (9, '1905606593278390273', 13, 1);
INSERT INTO `goods_sku` VALUES (10, '1905606593278390273', 7, NULL);
INSERT INTO `goods_sku` VALUES (11, '1905606593278390273', 16, NULL);
INSERT INTO `goods_sku` VALUES (12, '1905606593278390273', 15, NULL);
INSERT INTO `goods_sku` VALUES (13, '1905606593278390273', 14, NULL);
INSERT INTO `goods_sku` VALUES (15, '1905606593278390273', 9, 1);
INSERT INTO `goods_sku` VALUES (19, '1905916961678798850', 28, NULL);
INSERT INTO `goods_sku` VALUES (22, '1905606593278390273', 4, NULL);
INSERT INTO `goods_sku` VALUES (34, '1905606593278390273', 12, 1);
INSERT INTO `goods_sku` VALUES (35, '1905606593278390273', 11, 2);
INSERT INTO `goods_sku` VALUES (36, '1905606593278390273', 10, 2);
INSERT INTO `goods_sku` VALUES (39, '1905917857259171842', 16, NULL);
INSERT INTO `goods_sku` VALUES (40, '1905917857259171842', 15, NULL);
INSERT INTO `goods_sku` VALUES (41, '1905917857259171842', 14, NULL);
INSERT INTO `goods_sku` VALUES (42, '1905917857259171842', 8, NULL);
INSERT INTO `goods_sku` VALUES (43, '1905917857259171842', 5, NULL);
INSERT INTO `goods_sku` VALUES (44, '1905917857259171842', 4, NULL);
INSERT INTO `goods_sku` VALUES (45, '1905917857259171842', 13, 1);
INSERT INTO `goods_sku` VALUES (46, '1905917857259171842', 11, 2);
INSERT INTO `goods_sku` VALUES (47, '1905917857259171842', 9, 1);
INSERT INTO `goods_sku` VALUES (57, '1905919339446513665', 39, NULL);
INSERT INTO `goods_sku` VALUES (58, '1905919339446513665', 15, NULL);
INSERT INTO `goods_sku` VALUES (59, '1905919339446513665', 14, NULL);
INSERT INTO `goods_sku` VALUES (60, '1905919339446513665', 7, NULL);
INSERT INTO `goods_sku` VALUES (61, '1905919339446513665', 6, NULL);
INSERT INTO `goods_sku` VALUES (62, '1905919339446513665', 5, NULL);
INSERT INTO `goods_sku` VALUES (63, '1905919339446513665', 4, NULL);
INSERT INTO `goods_sku` VALUES (64, '1905919339446513665', 13, 1);
INSERT INTO `goods_sku` VALUES (65, '1905919339446513665', 12, 1);
INSERT INTO `goods_sku` VALUES (66, '1905919339446513665', 11, 2);
INSERT INTO `goods_sku` VALUES (67, '1905919339446513665', 9, 1);
INSERT INTO `goods_sku` VALUES (68, '1905918546202963970', 16, NULL);
INSERT INTO `goods_sku` VALUES (69, '1905918546202963970', 15, NULL);
INSERT INTO `goods_sku` VALUES (70, '1905918546202963970', 14, NULL);
INSERT INTO `goods_sku` VALUES (71, '1905918546202963970', 7, NULL);
INSERT INTO `goods_sku` VALUES (72, '1905918546202963970', 6, NULL);
INSERT INTO `goods_sku` VALUES (73, '1905918546202963970', 5, NULL);
INSERT INTO `goods_sku` VALUES (74, '1905918546202963970', 4, NULL);
INSERT INTO `goods_sku` VALUES (75, '1905923076688818177', 39, NULL);
INSERT INTO `goods_sku` VALUES (76, '1905923076688818177', 15, NULL);
INSERT INTO `goods_sku` VALUES (77, '1905923076688818177', 14, NULL);
INSERT INTO `goods_sku` VALUES (78, '1905923076688818177', 7, NULL);
INSERT INTO `goods_sku` VALUES (79, '1905923076688818177', 6, NULL);
INSERT INTO `goods_sku` VALUES (80, '1905923076688818177', 5, NULL);
INSERT INTO `goods_sku` VALUES (81, '1905923076688818177', 4, NULL);
INSERT INTO `goods_sku` VALUES (82, '1905923076688818177', 13, NULL);
INSERT INTO `goods_sku` VALUES (83, '1905923076688818177', 12, NULL);
INSERT INTO `goods_sku` VALUES (84, '1905923076688818177', 9, NULL);
INSERT INTO `goods_sku` VALUES (85, '1917164495524950017', 39, NULL);
INSERT INTO `goods_sku` VALUES (86, '1917164495524950017', 16, NULL);
INSERT INTO `goods_sku` VALUES (87, '1917164495524950017', 15, NULL);
INSERT INTO `goods_sku` VALUES (88, '1917164495524950017', 14, NULL);
INSERT INTO `goods_sku` VALUES (89, '1917164495524950017', 7, NULL);
INSERT INTO `goods_sku` VALUES (90, '1917164495524950017', 6, NULL);
INSERT INTO `goods_sku` VALUES (91, '1917164495524950017', 5, NULL);
INSERT INTO `goods_sku` VALUES (92, '1917164495524950017', 4, NULL);
INSERT INTO `goods_sku` VALUES (93, '1917164495524950017', 13, 1);
INSERT INTO `goods_sku` VALUES (94, '1917164495524950017', 12, 1);
INSERT INTO `goods_sku` VALUES (95, '1917164495524950017', 11, 2);
INSERT INTO `goods_sku` VALUES (96, '1917164495524950017', 9, 1);
INSERT INTO `goods_sku` VALUES (97, '1917165661201723393', 16, NULL);
INSERT INTO `goods_sku` VALUES (98, '1917165661201723393', 15, NULL);
INSERT INTO `goods_sku` VALUES (99, '1917165661201723393', 14, NULL);
INSERT INTO `goods_sku` VALUES (100, '1917165661201723393', 7, NULL);
INSERT INTO `goods_sku` VALUES (101, '1917165661201723393', 6, NULL);
INSERT INTO `goods_sku` VALUES (102, '1917165661201723393', 5, NULL);
INSERT INTO `goods_sku` VALUES (103, '1917165661201723393', 4, NULL);
INSERT INTO `goods_sku` VALUES (104, '1917166485810278402', 15, NULL);
INSERT INTO `goods_sku` VALUES (105, '1917166485810278402', 14, NULL);
INSERT INTO `goods_sku` VALUES (106, '1917166485810278402', 7, NULL);
INSERT INTO `goods_sku` VALUES (107, '1917166485810278402', 6, NULL);
INSERT INTO `goods_sku` VALUES (108, '1917166485810278402', 5, NULL);
INSERT INTO `goods_sku` VALUES (109, '1917166485810278402', 4, NULL);
INSERT INTO `goods_sku` VALUES (110, '1917166485810278402', 13, 1);
INSERT INTO `goods_sku` VALUES (111, '1917166485810278402', 12, 1);
INSERT INTO `goods_sku` VALUES (112, '1917166485810278402', 11, 2);
INSERT INTO `goods_sku` VALUES (113, '1917166485810278402', 9, 1);
INSERT INTO `goods_sku` VALUES (114, '1917168182657904641', 39, NULL);
INSERT INTO `goods_sku` VALUES (115, '1917168182657904641', 16, NULL);
INSERT INTO `goods_sku` VALUES (116, '1917168182657904641', 15, NULL);
INSERT INTO `goods_sku` VALUES (117, '1917168182657904641', 14, NULL);
INSERT INTO `goods_sku` VALUES (118, '1917168182657904641', 7, NULL);
INSERT INTO `goods_sku` VALUES (119, '1917168182657904641', 6, NULL);
INSERT INTO `goods_sku` VALUES (120, '1917168182657904641', 5, NULL);
INSERT INTO `goods_sku` VALUES (121, '1917168182657904641', 4, NULL);
INSERT INTO `goods_sku` VALUES (122, '1917168182657904641', 13, 1);
INSERT INTO `goods_sku` VALUES (123, '1917168182657904641', 12, 1);
INSERT INTO `goods_sku` VALUES (124, '1917168182657904641', 11, 2);
INSERT INTO `goods_sku` VALUES (125, '1917168182657904641', 9, 1);
INSERT INTO `goods_sku` VALUES (126, '1917172414098272258', 39, NULL);
INSERT INTO `goods_sku` VALUES (127, '1917172414098272258', 15, NULL);
INSERT INTO `goods_sku` VALUES (128, '1917172414098272258', 14, NULL);
INSERT INTO `goods_sku` VALUES (129, '1917172414098272258', 7, NULL);
INSERT INTO `goods_sku` VALUES (130, '1917172414098272258', 6, NULL);
INSERT INTO `goods_sku` VALUES (131, '1917172414098272258', 5, NULL);
INSERT INTO `goods_sku` VALUES (132, '1917172414098272258', 4, NULL);
INSERT INTO `goods_sku` VALUES (133, '1917172414098272258', 13, 1);
INSERT INTO `goods_sku` VALUES (134, '1917172414098272258', 12, 1);
INSERT INTO `goods_sku` VALUES (135, '1917172414098272258', 11, 2);
INSERT INTO `goods_sku` VALUES (136, '1917172414098272258', 9, 1);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `user_id` varchar(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `order_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当用户支付完成后生成订单id',
  `remark` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `total_price` double NULL DEFAULT NULL COMMENT '总价',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '订单创建时间',
  `status` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '订单状态 0 待支付 1 已取消 2 制作中 3 配送中 4 已完成 5 已送达',
  `user_voucher_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '如果使用了优惠券,则记录优惠券id',
  `address` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '如果购买方式为外送下的配送地址',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人电话',
  `gender` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '联系人性别 0 男 1女',
  `shop_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺id',
  `shop_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺名称',
  `delivery_fee` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '购买方式为外送时产生的配送费',
  `packaging_fee` decimal(3, 1) NULL DEFAULT NULL COMMENT '购买方式为外送时产生的打包费',
  `order_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
  `buy_way` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '购买方式 自提0 外送1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES ('18d5cca6a2b74bd8a26a5ce58eb5fb36', 'oo__868CYhei8IqGvhFELutyBe20', 'P8', '', 1.6, '2025-04-28 17:29:43.565000', 4, '1916492951249235970', NULL, NULL, NULL, 0, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', NULL, NULL, '2025-04-28 17:29:45', 0);
INSERT INTO `orders` VALUES ('38500033b7964412b4057946956ca1c0', 'oo__868CYhei8IqGvhFELutyBe20', 'P2', '', 7, '2025-04-28 16:12:56.022000', 4, NULL, NULL, NULL, NULL, NULL, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', NULL, NULL, '2025-04-28 16:12:57', 0);
INSERT INTO `orders` VALUES ('467c1d149d4c437d90cb3403fe0724b3', 'oo__868CYhei8IqGvhFELutyBe20', 'P5', '', 4, '2025-04-28 16:13:44.265000', 4, '1916492944529960961', NULL, NULL, NULL, NULL, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', NULL, NULL, '2025-04-28 16:13:46', 0);
INSERT INTO `orders` VALUES ('566c8e5e822e46d5b2b72e5b28006768', 'oo__868CYhei8IqGvhFELutyBe20', 'D7', '', 4.6, '2025-04-28 17:29:01.595000', 5, NULL, '和馨园B区2期4栋3单元1309', '彭文宇', '18508459785', 0, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', 2, 0.6, '2025-04-28 17:29:03', 1);
INSERT INTO `orders` VALUES ('851251b6e84b48258b8fe0d8a9840001', 'oo__868CYhei8IqGvhFELutyBe20', 'D1', '', 12.6, '2025-04-29 16:32:11.310000', 5, NULL, '和馨园B区2期4栋3单元1309', '彭文宇', '18508459785', 0, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', 2, 0.6, '2025-04-29 16:32:15', 1);
INSERT INTO `orders` VALUES ('8847830a30214dc690e5b04683de73da', 'oo__868CYhei8IqGvhFELutyBe20', 'D2', '', 10.2, '2025-04-29 18:17:07.448000', 5, '1916492980269625345', '和馨园B区2期4栋3单元1309', '彭文宇', '18508459785', 0, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', 2, 1.2, '2025-04-29 18:17:10', 1);
INSERT INTO `orders` VALUES ('a4ee76a2e3d846d6a2e2f673804457ea', 'oo__868CYhei8IqGvhFELutyBe20', 'P1', '', 13, '2025-04-28 16:11:08.072000', 4, NULL, NULL, NULL, NULL, NULL, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', NULL, NULL, '2025-04-28 16:11:09', 0);
INSERT INTO `orders` VALUES ('ba3ecae2c4d848dcb1f56a5a2a04a643', 'oo__868CYhei8IqGvhFELutyBe20', 'D9', '', 7.6, '2025-04-28 23:19:43.028000', 5, '1916492938074927106', '和馨园B区2期4栋3单元1309', '彭文宇', '18508459785', 0, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', 2, 0.6, '2025-04-28 23:19:45', 1);
INSERT INTO `orders` VALUES ('ba84b0ca23be4d24a470face80e2399a', 'oo__868CYhei8IqGvhFELutyBe20', 'D2', '', 10.6, '2025-05-04 12:40:29.829000', 5, NULL, '和馨园B区2期4栋3单元1309', '彭文宇', '18508459785', 0, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', 2, 0.6, '2025-05-04 12:40:34', 1);
INSERT INTO `orders` VALUES ('c7210522156a40b3a3863781b7c69a3c', 'oo__868CYhei8IqGvhFELutyBe20', 'P4', '', 19, '2025-04-28 16:13:27.258000', 4, NULL, NULL, NULL, NULL, NULL, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', NULL, NULL, '2025-04-28 16:13:28', 0);
INSERT INTO `orders` VALUES ('e8dff9e6ce5142fdbe27eeb1a7c6a93b', 'oo__868CYhei8IqGvhFELutyBe20', 'P3', '', 5, '2025-04-29 18:17:47.656000', 4, NULL, NULL, NULL, NULL, 0, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', NULL, NULL, '2025-04-29 18:17:52', 0);
INSERT INTO `orders` VALUES ('ef3251c80de142ce85690bd46265e449', 'oo__868CYhei8IqGvhFELutyBe20', 'D6', '', 4.6, '2025-04-28 17:20:54.096000', 5, NULL, '和馨园B区2期4栋3单元1309', '彭文宇', '18508459785', 0, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', 2, 0.6, '2025-04-28 17:20:56', 1);
INSERT INTO `orders` VALUES ('f2cb63de5afb42448adbbd87b117d4a1', 'oo__868CYhei8IqGvhFELutyBe20', 'P1', '', 1, '2025-05-04 12:18:25.485000', 4, NULL, NULL, NULL, NULL, NULL, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', NULL, NULL, '2025-05-04 12:18:28', 0);
INSERT INTO `orders` VALUES ('fd0ac252680045a381d3a2cd4cb612a0', 'oo__868CYhei8IqGvhFELutyBe20', 'P3', '', 5, '2025-04-28 16:13:06.539000', 4, NULL, NULL, NULL, NULL, NULL, '1911681644323377154', '蜜雪冰城(和馨园商业街店)', NULL, NULL, '2025-04-28 16:13:08', 0);

-- ----------------------------
-- Table structure for orders_goods
-- ----------------------------
DROP TABLE IF EXISTS `orders_goods`;
CREATE TABLE `orders_goods`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `orders_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单表的主键id',
  `goods_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品id',
  `sku_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品sku信息',
  `count` int NULL DEFAULT NULL COMMENT '购买数量',
  `price` int NULL DEFAULT NULL COMMENT '单件价格',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品封面图',
  `goods_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品名称',
  `cart_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '购物车id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单包含的商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders_goods
-- ----------------------------
INSERT INTO `orders_goods` VALUES ('1916767120885071873', 'a4ee76a2e3d846d6a2e2f673804457ea', '1905606593278390273', '正常糖/脆啵啵/正常冰', 1, 9, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/cea22ef790bd476a8c56638c0c4d28bf.png', '满杯百香果', '1916767058750652418');
INSERT INTO `orders_goods` VALUES ('1916767120885071874', 'a4ee76a2e3d846d6a2e2f673804457ea', '1905916961678798850', '', 2, 2, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/b0ab68dfb209425d9a010607d334cb9b.png', '酷炫冰淇淋', '1916767085233487874');
INSERT INTO `orders_goods` VALUES ('1916767573609857025', '38500033b7964412b4057946956ca1c0', '1905917857259171842', '全糖/珍珠/少冰', 1, 7, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/94d5338f253e4746879c343cdc82610b.png', '椰果奶茶', NULL);
INSERT INTO `orders_goods` VALUES ('1916767617717157889', 'fd0ac252680045a381d3a2cd4cb612a0', '1905923076688818177', '半糖/脆啵啵/常温', 1, 5, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/614fd70c6ce14309bad778e52559a0a2.png', '清新绿茶', NULL);
INSERT INTO `orders_goods` VALUES ('1916767704576999425', 'c7210522156a40b3a3863781b7c69a3c', '1905919339446513665', '半糖/脆啵啵/常温', 1, 5, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/d82333662b664918b8194c35d6a0b577.png', '超值柠檬水', '1916767658632593410');
INSERT INTO `orders_goods` VALUES ('1916767704576999426', 'c7210522156a40b3a3863781b7c69a3c', '1905920946414399490', NULL, 1, 7, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/1286b2e3a2b04e3f82c5071eecba9401.png', '草莓芝士', '1916767676814901250');
INSERT INTO `orders_goods` VALUES ('1916767704576999427', 'c7210522156a40b3a3863781b7c69a3c', '1905918546202963970', '三分糖/热', 1, 7, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/5856e0f0ac7c42f2ad8080a6c30903da.png', '拿铁咖啡', '1916767696272277505');
INSERT INTO `orders_goods` VALUES ('1916767775917916162', '467c1d149d4c437d90cb3403fe0724b3', '1905919339446513665', '三分糖/椰果/常温', 1, 5, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/d82333662b664918b8194c35d6a0b577.png', '超值柠檬水', NULL);
INSERT INTO `orders_goods` VALUES ('1916784678342316033', 'ef3251c80de142ce85690bd46265e449', '1905916961678798850', '', 1, 2, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/b0ab68dfb209425d9a010607d334cb9b.png', '酷炫冰淇淋', NULL);
INSERT INTO `orders_goods` VALUES ('1916786723010990081', '566c8e5e822e46d5b2b72e5b28006768', '1905916961678798850', '', 1, 2, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/b0ab68dfb209425d9a010607d334cb9b.png', '酷炫冰淇淋', NULL);
INSERT INTO `orders_goods` VALUES ('1916786899050123266', '18d5cca6a2b74bd8a26a5ce58eb5fb36', '1905916961678798850', '', 1, 2, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/b0ab68dfb209425d9a010607d334cb9b.png', '酷炫冰淇淋', NULL);
INSERT INTO `orders_goods` VALUES ('1916874977156616194', 'ba3ecae2c4d848dcb1f56a5a2a04a643', '1907373192960376834', NULL, 1, 6, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/02/37153fb210674265b5560bf73ffd81e2.png', '经典轻乳', NULL);
INSERT INTO `orders_goods` VALUES ('1917134807167479809', '851251b6e84b48258b8fe0d8a9840001', '1905606593278390273', '三分糖/橙粒/正常冰', 1, 10, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/cea22ef790bd476a8c56638c0c4d28bf.png', '满杯百香果', NULL);
INSERT INTO `orders_goods` VALUES ('1917161215071735809', '8847830a30214dc690e5b04683de73da', '1905919339446513665', '三分糖/常温', 2, 4, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/05/d82333662b664918b8194c35d6a0b577.png', '超值柠檬水', NULL);
INSERT INTO `orders_goods` VALUES ('1917161383670173698', 'e8dff9e6ce5142fdbe27eeb1a7c6a93b', '1905923076688818177', '三分糖/常温', 1, 5, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/614fd70c6ce14309bad778e52559a0a2.png', '清新绿茶', '1917161345824968705');
INSERT INTO `orders_goods` VALUES ('1918882884735754241', 'f2cb63de5afb42448adbbd87b117d4a1', '1917163601848791042', NULL, 1, 1, 'http://192.168.204.133:9000/milky-tea/goods/2025/04/29/d5f25d8ee7694c138dfd4fa941ce42b5.png', '素毛肚', '1918882824664932354');
INSERT INTO `orders_goods` VALUES ('1918888439453904897', 'ba84b0ca23be4d24a470face80e2399a', '1905606593278390273', '七分糖/热', 1, 8, 'http://192.168.204.133:9000/milky-tea/goods/2025/03/29/cea22ef790bd476a8c56638c0c4d28bf.png', '满杯百香果', NULL);

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺名称',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(9, 6) NULL DEFAULT NULL COMMENT '纬度',
  `position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺位置',
  `account` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺logoURL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '店铺表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES ('1911681290529640449', '蜜雪宇城(信息学院校外店)', 113.082578, 28.320408, '湖南省长沙市长沙县湖南信息学院11餐厅1楼10号', 'mxychnuit', '$2a$10$r5wveLb/RGX/f7awPSxnleq18U1liS.LeH9Vi3sICq2oWGgeKxxFm', '18508459785', 'http://192.168.204.133:9000/milky-tea/logo/2025/05/03/5f78cc5deceb4add96a071e6026a8bca.png');
INSERT INTO `shop` VALUES ('1911681644323377154', '蜜雪冰城(和馨园商业街店)', 112.855912, 28.213721, '湖南省长沙市岳麓区麓谷街道嘉运路和馨园商业街一期A区5栋109号', 'mxychxy', '$2a$10$wUly/2WzEP5K0Gaab4rWNOtUUE1wpWm6oI2nJK4S17f49NFkTq7dC', '19374990098', 'http://192.168.204.133:9000/milky-tea/logo/2025/05/03/5f78cc5deceb4add96a071e6026a8bca.png');

-- ----------------------------
-- Table structure for sku
-- ----------------------------
DROP TABLE IF EXISTS `sku`;
CREATE TABLE `sku`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `dimension_id` int NULL DEFAULT NULL COMMENT '维度id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '维度SKU表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sku
-- ----------------------------
INSERT INTO `sku` VALUES (1, '中杯', 1);
INSERT INTO `sku` VALUES (2, '大杯', 1);
INSERT INTO `sku` VALUES (3, '超大桶', 1);
INSERT INTO `sku` VALUES (4, '正常糖', 2);
INSERT INTO `sku` VALUES (5, '七分糖', 2);
INSERT INTO `sku` VALUES (6, '半糖', 2);
INSERT INTO `sku` VALUES (7, '三分糖', 2);
INSERT INTO `sku` VALUES (8, '全糖', 2);
INSERT INTO `sku` VALUES (9, '椰果', 3);
INSERT INTO `sku` VALUES (10, '橙粒', 3);
INSERT INTO `sku` VALUES (11, '芋圆', 3);
INSERT INTO `sku` VALUES (12, '脆啵啵', 3);
INSERT INTO `sku` VALUES (13, '珍珠', 3);
INSERT INTO `sku` VALUES (14, '正常冰', 4);
INSERT INTO `sku` VALUES (15, '少冰', 4);
INSERT INTO `sku` VALUES (16, '热', 4);
INSERT INTO `sku` VALUES (27, '鸡蛋布丁', 3);
INSERT INTO `sku` VALUES (28, '奥利奥饼干碎', 3);
INSERT INTO `sku` VALUES (39, '常温', 4);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` char(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '小程序openId',
  `nickname` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '电话',
  `yu_king_coin` int NULL DEFAULT NULL COMMENT '宇王币',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别 0 男 1女',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '小程序用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('oo__868CYhei8IqGvhFELutyBe20', '阔绰彭老板', 'http://192.168.204.133:9000/milky-tea/avatar/2025/04/26/edd9d74808874290a94528a4898932a2.jpeg', '18508459785', 95401, 1);

-- ----------------------------
-- Table structure for user_voucher
-- ----------------------------
DROP TABLE IF EXISTS `user_voucher`;
CREATE TABLE `user_voucher`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `user_id` varchar(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户openid',
  `voucher_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '优惠券id',
  `status` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '优惠券状态 0 未使用 1 已使用 2 已过期 ',
  `acquire_time` datetime NULL DEFAULT NULL COMMENT '优惠券获取时间',
  `validity_period` int NULL DEFAULT NULL COMMENT '有效期',
  `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_voucher
-- ----------------------------
INSERT INTO `user_voucher` VALUES ('1916492938074927106', 'oo__868CYhei8IqGvhFELutyBe20', '1914964594020257793', 1, '2025-04-27 22:01:38', NULL, '2025-04-28 23:19:45');
INSERT INTO `user_voucher` VALUES ('1916492944529960961', 'oo__868CYhei8IqGvhFELutyBe20', '1914964748756520962', 1, '2025-04-27 22:01:39', NULL, '2025-04-28 16:13:46');
INSERT INTO `user_voucher` VALUES ('1916492951249235970', 'oo__868CYhei8IqGvhFELutyBe20', '1914964966843551746', 1, '2025-04-27 22:01:41', NULL, '2025-04-28 17:29:45');
INSERT INTO `user_voucher` VALUES ('1916492957901402113', 'oo__868CYhei8IqGvhFELutyBe20', '1914964966843551746', 0, '2025-04-27 22:01:43', NULL, NULL);
INSERT INTO `user_voucher` VALUES ('1916492965379846146', 'oo__868CYhei8IqGvhFELutyBe20', '1914964748756520962', 0, '2025-04-27 22:01:44', NULL, NULL);
INSERT INTO `user_voucher` VALUES ('1916492980269625345', 'oo__868CYhei8IqGvhFELutyBe20', '1914964594020257793', 1, '2025-04-27 22:01:48', NULL, '2025-04-29 18:17:10');

-- ----------------------------
-- Table structure for voucher
-- ----------------------------
DROP TABLE IF EXISTS `voucher`;
CREATE TABLE `voucher`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片url',
  `discount_rate` double NULL DEFAULT NULL COMMENT '优惠券类型为折扣型的折扣率',
  `threshold` int NULL DEFAULT NULL COMMENT '优惠券类型为满减型的最小门槛',
  `deduct` int NULL DEFAULT NULL COMMENT '优惠券类型为满减型或者直减型时减去的金额',
  `effective_time` datetime NULL DEFAULT NULL COMMENT '优惠券生效期',
  `expiration_time` datetime NULL DEFAULT NULL COMMENT '优惠券过期时间',
  `number` int NULL DEFAULT NULL COMMENT '优惠券剩余量',
  `type` tinyint UNSIGNED NOT NULL COMMENT '优惠券类型 0 直减型 1 满减型 2 折扣型',
  `coin_need` int NULL DEFAULT NULL COMMENT '兑换时需要的宇王币数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of voucher
-- ----------------------------
INSERT INTO `voucher` VALUES ('1914964594020257793', '满5减1优惠券', 'http://192.168.204.133:9000/milky-tea/voucher/2025/04/23/2f96ad548dbb467a84bafef1bf630149.png', NULL, 5, 1, '2025-04-23 00:00:00', '2025-05-29 00:00:00', 96, 1, 500);
INSERT INTO `voucher` VALUES ('1914964748756520962', '一元券', 'http://192.168.204.133:9000/milky-tea/voucher/2025/04/23/9b7e88fc14bd4a40a1797d7243417a9f.png', NULL, NULL, 1, '2025-04-23 00:00:00', NULL, 664, 0, 800);
INSERT INTO `voucher` VALUES ('1914964966843551746', '8折优惠券', 'http://192.168.204.133:9000/milky-tea/voucher/2025/04/23/c01690f4bcb9480c87af11992e5aa486.png', 0.8, NULL, NULL, '2025-04-23 00:00:00', NULL, 119, 2, 999);

SET FOREIGN_KEY_CHECKS = 1;
