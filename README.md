# jmeter 使用java开发测试内容

#打包(不需要的依赖scope置为provided)
mvnci package
#复制jar包到jmeter下
cp -f target/jmeter-1.0-SNAPSHOT-jar-with-dependencies.jar ~/opt/apache-jmeter-2.13/lib/ext
#重启jmeter
~/opt/apache-jmeter-2.13/bin/jmeter&
