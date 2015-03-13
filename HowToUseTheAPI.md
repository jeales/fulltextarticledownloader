# Starting points #
I'm just going to describe how to download from PubMed IDs and starting URLs.
If you want to automate PubMed queries, it includes using the JavaBioinformaticsOnlineToolkit API, which will detract from the clarity of using the fulltextarticledownloader API.

Lets suppose we have our PMIDs and URLs in lists.
```
LinkedList<String> pmids;
LinkedList<String> urls;
```

If you want to iterate through each URL and try to find a PDF for each one. First you can create a FileStore to store your PDFs, using a FileStore will avoid repeating the same download.

```
File fsDir = new File("filestore");
fsDir.mkdir();
FileStore fs = new FileStore(fsDir, true);
```



# Details #

Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages