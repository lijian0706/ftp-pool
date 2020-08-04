# ftp对象连接池的实现
- 该demo只是简单使用commons-pool中的`GenericObjectPool`对`FTPClient`对象进行管理，默认最大8个连接，使用的时候需要初始化`FTPConfig`对象，指定ip 端口 用户名 密码 路径，使用`FTPDataSource.getFtpClient()`
方法进行ftp连接对象的获取，使用完毕后，使用`FTPDataSource.returnObject()`对连接对象进行归还。
- 该demo不支持分布式场景下的ftp对象管理，只是简单使用map对对象池进行保存到本机内存中。
