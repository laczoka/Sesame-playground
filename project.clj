(defproject sesame-playground "1.0.0-SNAPSHOT"
  :description "Demo project for Sesame Experiments"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.openrdf.sesame/sesame-repository-sail "2.6.3"]     
                 [org.openrdf.sesame/sesame-sail-memory "2.6.3"]
                 [org.openrdf.sesame/sesame-rio-rdfxml "2.6.3"]
                 [org.openrdf.sesame/sesame-rio-n3 "2.6.3"]
                 [org.openrdf.sesame/sesame-queryparser-sparql "2.6.3"]
                 [org.slf4j/slf4j-log4j12 "1.6.4"]]
  :repositories { "sesame" "http://repo.aduna-software.org/maven2/releases/" })

