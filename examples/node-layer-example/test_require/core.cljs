(ns test-require.core)

(def is-odd? (js/require "is-odd"))

(defn meaning-of-life []
  (.tell_me (js/require "meaning-of-life")))
  
