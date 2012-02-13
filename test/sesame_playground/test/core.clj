(ns sesame-playground.test.core
  (:use [sesame-playground.core])
  (:use [clojure.test]))

(defn with-gr-and-perfume [f]
  (insert-triples triple-store (java.io.File. "resources/ontologies/gr.owl"))
  (insert-triples triple-store (java.io.File. "resources/ontologies/perfumes.owl"))
  (f)
  (clear-store triple-store))

(defn from-file [fn-name]
  (slurp (str "resources/queries/" fn-name ".query")))

(defn query-res-to-map [sparql-q]
  (let [result (run-query triple-store sparql-q)
        result-map (map bindingset-to-map (iteration->seq result))]
    result-map))

(use-fixtures :once with-gr-and-perfume)

(deftest list-all-ontologies
  (is (= (query-res-to-map (from-file "list_ontologies"))
         (list {
                :onto "http://purl.org/opdm/perfume#PerfumeVocab",
                :ontolabel "\"Perfume vocabulary\"@en",
                :ontodesc "\"Vocabulary to describe perfumes and fragrance\"@en" }))
      "Couldn't load all expected ontologies"))
