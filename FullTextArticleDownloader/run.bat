set PATH=%PATH%";%~dp0\native;%~dp0\native\components"
echo %PATH%

java -Xmx256m -jar FullTextArticleDownloader.jar
