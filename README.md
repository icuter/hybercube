多 Reactor 多线程 nio 服务器
=====

# 架构图
![Reactor](https://github.com/edwardleejan/images/blob/master/hybercube.png?raw=true)

## 流程说明
1. 通过主 `Reactor` 接收请求连接
2. 再通过子 `Reactors` 处理 `Channel` 中的 `Buffer` 数据
3. 直到数据完整后，通过 `Protocol` 进行数据 decode
4. 继而利用 `Protocol` 中的 `Handler` 进行数据的业务处理
5. 最后通过 `Protocol` 中的 encode 进行编码返回

> 初次提交仅实现 `FixedLengthProtocol`，即固定长度报文协议

