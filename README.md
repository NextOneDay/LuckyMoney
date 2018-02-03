# LuckyMoney
红包助手

​	这是一个能够自动抢微信红包的助手，使用的是AccessibilityService服务。判断红包的方式使用了两种，一个是根据view布局获取到对应的resource-id 来获取对应的AccessibilityNodeInfo对象。这种比较方便简洁，但是每个版本的id都会变。所以需要一直更改。一种是直接通过文本来获取AccessibilityNodeInfo对象。这种只要文本不改变，可以一直使用。

目前匹配的id 是v6.6.1的版本
![S80204-024638](screenshots\S80204-024638.jpg)

### 功能

1.能够延时打开红包

![S80204-024648](screenshots\S80204-024648.jpg)

2.能够匹配红包的上面文字，而过滤掉红包

![S80204-024655](screenshots\S80204-024655.jpg)