export LD_LIBRARY_PATH=./native:./native/components:$LD_LIBRARY_PATH
echo $LD_LIBRARY_PATH

java -Xmx256m -jar FullTextArticleDownloader.jar
