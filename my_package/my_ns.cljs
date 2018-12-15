(ns my-package.my-ns)

(defn my-handler [{:keys [_event _context]
                   :as input}]
  {:hello "Hello from my-handler!"
   :input input})
