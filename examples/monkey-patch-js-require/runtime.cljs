(ns runtime
  (:require [test-require.core]
            http))

(defn -main
  [& args]
  (println "running tests")
  (assert (test-require.core/run-tests)))
