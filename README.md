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


项目结构   
* listen -> QQ消息监听
* action ->   
    commandProcess -> 命令处理   
    draw -> 绘图    
    webPageScreenshot -> 调用浏览器进行截图    
    request -> http请求    

## 技术介绍   
以目前的查询功能来说 都是基于selenium自动化打开浏览器并且截图完成的  
最后再通过代码对图片进行裁剪 然后保存发送 也许你也会发现当dak.gg页面发生变动时   
bot裁剪出来的图片是错误的 
      
在查询分段(命令:永恒多少分)这一块则是通过http请求dak.gg的json数据链接获取数据后  
再通过机器人本地进行图片制作并发送  🦌功能同理    
      
之前有考虑到去selenium化 因为他对内存及配置要求过高 并且查询的速度并不可靠 回显的界面也并不稳定   
但分析玩dak.gg的页面数据后发现数据量过于庞大 并且这基本算是体力活 就像打螺丝一样(我真的进厂打过一个月螺丝)    
实在是无趣 后面便不再考虑这方面的移植


## TODO

- 频繁词汇记录并限制回复
- 不再依赖selenium(数据量较大)       
      
     
特别感谢 bilibili-api-collect     

由liteLoaderNTQQ LLonebot shiro 强力驱动
