(ns lib-example.core
  (:require [promesa.core :as p]))

(defn handler [input]
  (p/resolved input))  
