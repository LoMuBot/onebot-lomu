# 基于onebot协议开发的永恒轮回聊天机器人

Bot并没有接入NLP所以无法理解自然语言 它的所有回复相当于硬编写    
(通过一系列操作让其回复的词尽可能靠近发生的词)     
个人使用bot 屎山堆叠之术

## 项目结构

技术栈 ->
1.spring websocket   
2.redis   
3.mongodb   
4.kotlin 2.0(java 21)    
如果你想要了解项目你可以从package cn.luorenmu.listen.GroupEventListen.kt开始了解    
如果你想要运行本项目需要将src/main/resources/request/移动到jar目录下的request中

## TODO

- 频繁词汇记录并限制回复
- 不再依赖selenium(数据量较大)

由liteLoaderNTQQ LLonebot shiro 强力驱动
