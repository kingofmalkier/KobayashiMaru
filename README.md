# KobayashiMaru
Compute dashboard for grails/AWS fun.

The Basic Design
----------------
A backend REST service that listen for precisely one REST call: '/instances'. That call can accept a handful of parameters to control ordering/paging/etc. It can also be given the parameter "refresh" if it should be pulling fresh data from AWS. When fresh data is pulled it is cached in a local database to reduce network overhead and allow the DB to do nearly all of the work for paging and sorting. Cached data is attached to a requestID (the frontend currently uses the user's name) so that theoretically two different auditors could be looking at our instances at the same time without clobbering each other.

The only required parameter is that same requestID, and the service will return a 400 if it is missing. Other missing parameters receive sensible defaults.

As the still-present logo will show, a simple grails frontend displays results in tabular fashion. After a standard login, auditors will see a pretty familiar table that can be sorted in ascending or descending order by clicking any column header. Sorting is currently all classic alphanumeric ordering so IPs sort a bit strangely. More natural ordering with a comparator is simple enough if desired, but requires the sorting to be done outside of the database. By accepting alphanumeric ordering we can reduce the memory footprint needed to page and sort fairly drastically. The frontend only requests (and receives) one page of data at a time and it matches nicely if the backend behaves similarly.

Security
--------
All pages on the frontend are secured using the grails-recommended spring security core. It is left in a fairly default configuration and stores a username/password in its own database. The backend is left very open, protected merely by aws security groups to communicate solely with the frontend. The only public endpoint (web UI) is protected, but there isn't a large amount of depth to that protection.

Spring security's documentation claims to support a secure REST connection over SSL and using auth tokens. Theoretically, the backend could require auth tokens for every REST call excepting the initial login. This would let us move the user store way down in our tiers and provide a better set of layered protections. Unfortunately, applying that solution proved non-trivial (grails does not like to work in heavily-tiered architectures by default) and after discussing with Product Management (Hi, Denis!) it was decided that my efforts were better spent elsewhere.

Tests
-----
Both application's automated tests can be run by: 
    entering the project directory
    Running 'grails'
    Entering 'test-app -clean' into the interactive shell

AWS Deploy
----------
http://ec2-54-173-182-23.compute-1.amazonaws.com:8080/

The demo console runs against my personal AWS account so there aren't many instances and most aren't running. Also, my apologies for the speed of the t1.micro webservers. Both frontend and backend are deployed in tomcat8 containers on some basic Linux AMIs, but they are not snappy.

Running Locally
---------------
If you chose to, you can mess about with local instances which will still pull data from AWS but with a lot better speed. You'll need grails if you don't have it and in terms of no-thought-required I'd recomend the 'SDKMAN!' instructions at https://grails.org/download.html.

Once you have grails installed, unzip the two projects. Running them is simple:
    Enter the project's directory
    Run the 'grails' command
    Enter 'run-app' in the interactive shell

Three configuration values you are most likely going to want to tweak are in /computeDashboardBE/grails-app/conf/application.yml. There are options for how to override these settings, but the easiest is simply edit the file. Changes will be picked up in a running system. The values are:

elf.aws.accessKeyId
elf.aws.secretKey
elf.aws.endpoints

accessKeyId and secretKey are pretty self-explanatory. endpoints controls which amazon endpoints we will query. By default I've only included east-1 and west-2 to speed up my own testing, but feel free to go crazy if you want to know how your AMIs in Cork are doing.

In Conclusion
-------------
It was quite refreshing to take on a project like this; working at every level once again. I hope you find everything to your liking and I'm obviously available to answer any questions or defend/discuss any decisions.

Finally, after working on this in my non-work hours I now consider anyone who can attend college while caring for a child to be a superhero.

-Josh
