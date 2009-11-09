export DYLD_LIBRARY_PATH=./native:./native/components:$DYLD_LIBRARY_PATH
echo $DYLD_LIBRARY_PATH

/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/bin/java -Xmx256m -jar FullTextArticleDownloader.jar
