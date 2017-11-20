# Web Crawler #

Main idea of this task is to create high-performance web-crawler, backed by database.

#### Crawler itself is a simple tool which ####
* starts from some starting URL, downloads page
* parse links (local and external)
* parses page content (filtering out non-displaying content like scripts or hidden parameters or html tags)
* saves page content processed to database
* saves links as a new starting points for later processing.

#### Crawler must ####
* save it's state to database.
* be multiple-threaded instance.
* be able to be started in a few application instances.
* not visit the same page twice.
* support max. depth limits, for example 20 **external** hops from the starting point.
* be able to support HTTPS

If crawler finished all available tasks, it should wait for a new tasks.

If crawler died or was stopped, another crawler instance must continue unfinished work.

Downloaded page must be parsed to separate words, so for every parsed page we need to save list of words and how many times they was used.

Use Hibernate as ORM, microservice architecture and REST.
