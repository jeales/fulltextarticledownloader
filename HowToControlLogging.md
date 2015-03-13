# Logging #
Logging is controlled very, very simply.
There will be a file in the directory where you expanded the archive download.
It is called `fulltextarticledownloader.props` it contains on line which by default will be
```
logging=false
```

If you want to enable logging of the download agent, then change logging value to true like this.
```
logging=true
```
When logging is enabled a file will be created called `MySimpleAgent.log`.
This file will be appended to every time you run the software and can become quite large over time.
If you want to start a new log file just delete the file and it will be automatically created the next you run the software.