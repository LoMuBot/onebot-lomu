# 基于onebot协议开发的永恒轮回聊天机器人

Bot并没有接入NLP所以无法理解自然语言 它的所有回复相当于硬编写    
(通过一系列操作让其回复的词尽可能靠近发生的词)
个人使用bot 屎山堆叠之术

## 项目结构

技术栈 ->    
1.spring websocket   
2.redis   
3.mongodb   
4.kotlin 2.0(java 17)

## 技术介绍
{QQ消息 -> NTqq(oneBotV11) -><- spring framework -> shiro -> commandProcess -> request dak.gg api -> freemarker render -> playwright screenshot}

## TODO

- 接入 developer api 不再依赖dak.gg
- 接入 国服 api

特别感谢 bilibili-api-collect

由liteLoaderNTQQ OneBotV11 shiro 强力驱动
