(ns test-require.core)

(defn run-tests []
    (assert (= 42 (.tell_me (js/require "meaning-of-life"))))
    true)
