## Problems ##
This software makes use of native code libraries that are compiled for each of the different operating systems supported (Windows, Linux, Mac OS X).  Therefore it is more likely that errors will occur when running the software.

Included in the `native/` directory there are dynamic shared libraries which are compiled for your platform.  All libraries are 32 bit for all operating systems, therefore if you are on a 64 bit operating system they may not load. Although the libraries do work on Mac OS X snow leopard (a mostly 64 bit operating system).

If you see errors like this

```
Operating system : Mac OS X
Warning:Could not load library /Users/user1/ftad/native/libMozillaParser.jnilib Possible reason 
: You have to include both mozilla.dist.bin.macosx And mozilla.dist.bin.macosx 
In the right environment variable (windows:PATH , Linux: LD_LIBRARY_PATH , macosx: DYLD_LIBRARY_PATH
```

or like this

```
com.dappit.Dapper.parser.ParserInitializationException: java.lang.UnsatisfiedLinkError:  
/Users/user1/ftad/native/libMozillaParser.jnilib:  no suitable image found.  Did find:  
./native/libMozillaParser.jnilib: mach-o, but wrong architecture 
/Users/user1/ftad/native/libMozillaParser.jnilib: mach-o, but wrong architecture
```

This is telling you that your operating system is not able to use the libraries provided.  One remedy for this is to try and force Java to use a 32 bit virtual machine (VM).  This can be specified on the command line by including the `-d32` argument, you can specify this by editing the `run` file included, for example, by default the linux `run.sh` file includes.

```
export LD_LIBRARY_PATH=./native:./native/components:$LD_LIBRARY_PATH
echo $LD_LIBRARY_PATH

java -Xmx256m -jar FullTextArticleDownloader.jar
```

To force Java to use a 32 bit VM change the file to

```
export LD_LIBRARY_PATH=./native:./native/components:$LD_LIBRARY_PATH
echo $LD_LIBRARY_PATH

java -d32 -Xmx256m -jar FullTextArticleDownloader.jar
```

Inversely if you find the inclusion of the `-d32` causes this kind of error
```
C:\Documents and Settings\bob>java -d32
Unrecognized option: -d32
Could not create the Java virtual machine.
```
This is telling you that your version of Java only supports one of 32 or 64 bit, and therefore if the the default `run` file does not work then you will most likely not be able to run this software.

If you, like me, find this very annoying, then you might want to consider virtualisation of an operating system that does work with the software, for example [Ubuntu](http://www.ubuntu.com/getubuntu/download) 32-bit (which is free and has only minimal hardware requirements, which makes it good for virtualisation) or Windows XP 32-bit.  If you have an Intel Mac, you should be able to run this software with no problems. Both Ubuntu and Windows XP can be very easily run inside your native operating system using [VirtualBox](http://www.virtualbox.org/), which is free and is available for many different host operating systems.

The testing of this software on different operating systems has been achieved by the virtualisation strategy outlined above on an Intel Mac running Snow Leopard.