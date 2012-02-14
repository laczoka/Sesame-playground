(ns sesame-playground.core
  (:import (org.openrdf.model Resource)
           (org.openrdf.repository RepositoryConnection)
           (org.openrdf.repository.sail SailRepository)
           (org.openrdf.sail.memory MemoryStore)
           (org.openrdf.sail.inferencer.fc ForwardChainingRDFSInferencer)
           (org.openrdf.rio RDFFormat)
           (org.openrdf.query QueryLanguage BindingSet)
           (java.io File)
           (java.net URL)))

(def base-uri (URL. "http://example.org/"))

(def onto-files (->> (File. "resources/ontologies")
                    (file-seq )
                    (filter #(.isFile %))))

(def common-ns-prefixes "prefix dcterms: <http://purl.org/dc/terms/> 
PREFIX foaf:<http://xmlns.com/foaf/0.1/> 
PREFIX gr:<http://purl.org/goodrelations/v1#> 
PREFIX owl:<http://www.w3.org/2002/07/owl#> 
PREFIX pto:<http://www.productontology.org/id/> 
PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> 
PREFIX skos:<http://www.w3.org/2004/02/skos/core#> 
PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
PREFIX opdm: <http://purl.org/opdm/utility#>")

(defn with-common-ns-prefixes [sparql-q]
  (str common-ns-prefixes " " sparql-q))


(defn create-triple-store []
        (doto 
            (SailRepository. 
                (ForwardChainingRDFSInferencer. 
                    (MemoryStore. ))) 
            (.initialize)))
(def triple-store (create-triple-store ))

(defmulti insert-triples
  "Insert triples from various sources e.g. an rdf/xml file into the memory-backed triple store\n
     Example: (insert-triples the-store (java.io.File. \"path_to_triple_file\"))"
  (fn [store triples] (class triples)))

;; insert triples from an RDF/XML file
(defmethod insert-triples java.io.File [store triples-file] 
  (with-open [conn (.getConnection store)]
    (.add conn triples-file (str base-uri) RDFFormat/RDFXML (make-array Resource 0))))  

;; insert triples from as a sequence of n-triples statements
(defmethod insert-triples java.util.Collection [store triples]
  (with-open [conn (.getConnection store)]
    (doseq [triple triples]
      (let [triple-str-as-stream (java.io.ByteArrayInputStream. (.getBytes triple "UTF-8")) ]
        (.add conn triple-str-as-stream (str base-uri) RDFFormat/NTRIPLES (make-array Resource 0))))))

(defn clear-store 
    "Remove all triples from the store"
    [store]
    (with-open [conn (.getConnection store)]
        (.clear conn (make-array Resource 0))))

(defn run-query [store sparql-q]
    ;(with-open 
  (let  [conn (.getConnection store)
         q-results (.evaluate (.prepareTupleQuery
                               conn
                               QueryLanguage/SPARQL
                               sparql-q))
         variables (vec (.getBindingNames q-results))]
    q-results))

(defn iteration->seq [iteration]
  (seq
      (reify java.lang.Iterable 
          (iterator [this] 
             (reify java.util.Iterator
               (hasNext [this] (.hasNext iteration))
               (next [this] (.next iteration))
               (remove [this] (.remove iteration)))))))

;; slurp up a SPARQL query from a text file
(defn from-file [fn-name]
  (slurp (str "resources/queries/" fn-name ".query")))

(defn- bindingset-to-map [^BindingSet bs]
  (reduce #(assoc %1 (keyword (.getName %2)) (str (.getValue %2)) ) {} bs))

;; put result of a sparql query into a set so that order-agnostic
;; comparison can be made
(defn query-result-set [triple-store sparql-q]
  (let [results (run-query triple-store sparql-q)
        result-map-seq (map bindingset-to-map (iteration->seq results))]
    (set result-map-seq)))

(defn load-ontologies [triple-store onto-files]
  (doseq [onto-file onto-files]
    (insert-triples triple-store onto-file)))
