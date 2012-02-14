(ns sesame-playground.test.core
  (:use [sesame-playground.core])
  (:use [clojure.test]))

;; defines a fixture, see: use-fixtures further down
(defn- with-gr-and-perfume-and-printer [f]
  (insert-triples triple-store (java.io.File. "resources/ontologies/gr.owl"))
  (insert-triples triple-store (java.io.File. "resources/ontologies/perfume.owl"))
  (insert-triples triple-store (java.io.File. "resources/ontologies/printer.owl"))
  (f)
  (clear-store triple-store))

;; activate fixture
(use-fixtures :once with-gr-and-perfume-and-printer)

;; Test 1. query all available ontology elements and related properties
(deftest list-all-ontologies
  (is (= (set [
               {
                :onto "http://purl.org/opdm/perfume#PerfumeVocab",
                :ontolabel "\"Perfume vocabulary\"@en",
                :ontodesc "\"Vocabulary to describe perfumes and fragrance\"@en" }
               {
                :onto "http://purl.org/opdm/printer/ns#",
                :ontolabel "\"Printer vocabulary\"@en",
                :ontodesc "\"Home & Office printers including all-in-one devices with faxing, scanning and copying capabilities.\"@en"}])

         (query-result-set triple-store (from-file "list_ontologies")))
      "Couldn't load all expected ontologies"))
;; Test 2. List all product classes in the the triple store
;; (rdfs:subClassesOf gr:ProductOrService)
(deftest list-all-product-classes
  (is (= (set [{:label "\"Generic printer\"@en"}
               {:label "\"Generic scanner\"@en"}
               {:label "\"Generic fax\"@en"}
               {:label "\"Multiple function laser printer\"@en"}
               {:label "\"Multiple function inkjet printer\"@en"}
               {:label "\"Generic copy machine\"@en"}
               {:label "\"Matrix printer\"@en"}
               {:label "\"Inkjet printer\"@en"}
               {:label "\"Laser printer\"@en"}
               {:label "\"Perfume\""}
               ])
         (query-result-set triple-store (from-file "list_product_classes")))
      "Retrieved set of product classes do not match the expected set"))

;; Test 3. List all "datatype product properties" for
;; http://purl.org/opdm/printer#AllInOneLaserPrinter
(deftest list-all-dt-product-properties
  (is (= (set [{:label "\"Document feeder\"@en"}
               {:label "\"Photo printing\"@en"}
               {:label "\"PictBridge\"@en"}
               {:label "\"Supported cartridge\"@en"}
               {:label "\"Supported print media\"@en"}
               {:label "\"color (0..1)\"@en"}
               ])
         (query-result-set triple-store (from-file "datatype_pos_properties")))
      "Retrieved set of product properties do not match the expected set"))

