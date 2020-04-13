(ns http.core
  (:require [httpurr.client.node :as http]))

(defn process [{{search "search"} "query"}]
  (/ (->> search
          (map #(get % "wordcount"))
          (apply +))
     (count search)))

(defn handler [{{query "query"} :event}]
  (let [q (js/encodeURIComponent query)]
    (-> (http/send! {:method :get 
                     :url (str "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" q "&format=json")}) 
        (.then (comp js->clj js/JSON.parse str :body)) 
        (.then process))))
