(ns my-package.my-ns)

(defn my-handler [{:keys [_event _context]
                   :as input}]
  (println "Hello from ClojureScript!")
  {:hello "Hello from my-handler!"
   :input input})
