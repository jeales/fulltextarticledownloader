## Doing some downloads ##

### A quick walkthrough ###

This is a simple walkthrough that tells you how to actually use the software.
Firstly always try and run the software on a machine that as an IP address from your institution. Most publishers use IP-based user authentication and therefore you will always get better results when connected to your institutions network.  If you configure a proxy or VPN at the operating system level, then the software will use these details automatically, currently the software does not support it own configuration of proxy server access.

1. Run the software, see [here](http://code.google.com/p/fulltextarticledownloader/) if you don't know how to do this

2. Now lets build a PubMed query, firstly choose `"Title"` from the drop down list on the top left

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/two.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/two.png)

3. Then as an example enter `"ncbi"` in the text box next to the drop down

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/three.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/three.png)

4. Click the `"Add Search Term"` button, this will add the search to the query, a summary will appear below

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/four.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/four.png)

5. Now choose `"Year"` from the drop down and type `"2008"` in the text box, again click the `"Add Search Term"` button, now your query should look like this.

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/five.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/five.png)

6. Now we can run our search, click the `"Search"` button

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/six.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/six.png)

7. After a short time, search results will appear below and will look something like this

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/seven.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/seven.png)

8. Now we want to try and download all of these articles, so click `"Download all articles"`

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/eight.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/eight.png)

9. This brings up the download options, where you can specify where and what you would like to save, if you've not used the software before it is best to create a new directory to use as your filestore

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/nine.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/nine.png)

10. When you click `"Confirm Choices"` the files will begin downloading, and the status of these downloads will appear and be updated in the panel below, like this

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/ten.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/ten.png)

11. Once the downloads have completed you can check the output by navigating to your filestore location.  In this case we specified XML, PDF and plain text to be saved therefore in the filestore location we have a directory for each of the file types and an index file called `"filestore.xml"`.  The index file enables the reuse of filestore directories and stops the software from trying to download files that are already in the filestore.  The filestore structure looks like this

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/eleven.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/eleven.png)

and the `"filestore.xml"` looks like this

![http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/twelve.png](http://fulltextarticledownloader.googlecode.com/svn/trunk/FullTextArticleDownloader/wiki-images/twelve.png)