(ns runtime-local
  (:require [test-require.core :refer [meaning-of-life is-odd?]]
            http))

(defn -main
  [& _args]
  (assert (false? (is-odd? (meaning-of-life))))
  (println "ok!"))
