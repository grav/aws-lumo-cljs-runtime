(ns my-package.my-ns)

(defn my-handler [input]
  (-> {:body (pr-str {:hello "Hello from my-handler!"
                      :input input})
       :statusCode 200
       :headers {"Content-Type" "application/edn"}}
      clj->js))
 
