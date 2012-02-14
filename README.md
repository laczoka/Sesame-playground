# sesame-playground

This is my first attempt to use [Sesame][], a Java-based semantic web toolkit from [Clojure][].
You'll find simple [Leiningen][] project along with three sample queries (see resources/queries) against 3 ontologies:

* [GoodRelations][]: an established vocabulary for e-commerce with wide industry support 
* an early development snapshot two other ontologies for the domain of printers and perfumes developed for the [OPDM][] project

The project contains a few useful functions that will help you create an in-memory triple store 
(a kind of database if you will), load arbitrary RDF/XML data into the triple store and run [SPARQL][] 
queries against the data.

## Requirements

I assume you have at least JDK 1.6 installed on your machine. 

Before you start, make sure you have [Leiningen][] installed. If not, simply download [this script][], place it on your $PATH and run

	lein self-install

Download and unzip the source into an empty folder or simply clone the repository:

	git clone https://github.com/laczoka/sesame-playground.git
	cd sesame-playground
	lein deps

## Usage

### Run the tests

This is not very informative, but it leverages the standard test framework of clojure.

	lein test

Check out the code under in the test directory.

### Experiment with a triple store form the REPL

The REPL (Read-Eval-Print-Loop) is an interactive environment that you can use to experiment with Sesame and the code in this project.

For a start, go to the project's directory and run
	
	lein repl
	
You are now in an interactive programming environment that resembles a shell.
Let's import the central namespace so we can start working:

	(use 'sesame-playground.core)

Let's create a triple store:

	(def ts (create-triple-store))
	
Now that we have a triple store let's insert some triples into it using the [n-triples][] syntax:
	
	(insert-triples ts ["<http://name.me/Laszlo> <http://xmlns.com/foaf/0.1/name> \"Laszlo\"."])

You can also load RDF/XML (e.g. the [GoodRelations][] ontology) files and insert them into the triple store:
	
	(insert-triples ts (java.io.File. "resources/ontologies/gr.owl"))
	
So now we have some data in our triple store, let's issue a few queries. Let's get all foaf:name values in our store:

	(query-result-set ts (with-common-ns-prefixes "SELECT ?name WHERE { ?s foaf:name ?name.}"))
	
and we get
	
	#{{:name "\"Laszlo\""}}

\#{ .. } denotes a set, the result set returned by the query. {:name "\"Laszlo\""} is a map (or dictionary), where *:name* corresponds to *?name* in the SPARQL query
and "Laszlo" is the string literal.
	
Note: the *with-common-ns-prefixes* simply prepends the SPARQL query with the most common namespace prefixes to save you some typing effort.

You can clear a store by calling:
	
	(clear-store ts)

Test this by issuing:

	(.isEmpty (.getConnection ts))
	
should return

	true
	
At this point you now how to create a triple store, how to add triples to it and how to query the triple store.
Check out the code for technical details!

[Sesame]: http://http://www.openrdf.org/
[Clojure]: http://clojure.org/
[Leiningen]: http://github.com/technomancy/leiningen
[GoodRelations]: http://www.goodrelations-vocabulary.org/
[OPDM]: http://www.opdm-project.org/
[SPARQL]: http://www.w3.org/TR/sparql11-query/
[n-triples]: http://www.w3.org/2001/sw/RDFCore/ntriples/
[this script]: https://raw.github.com/technomancy/leiningen/stable/bin/lein

## License

Copyright (C) 2012 Laszlo Török

Provided as is!

Distributed under the Eclipse Public License, the same as Clojure.
