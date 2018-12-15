(require 'http)
(require 'clojure.string)

(def runtime-path (str "http://" (.-AWS_LAMBDA_RUNTIME_API js/process.env) "/2018-06-01/runtime"))

(defn request [{:keys [url method headers body]
                :or   {method :get}}]
  (js/Promise.
    (fn [resolve reject]
        (let [headers (merge headers
                             (when body
                               {"Content-Length" (js/Buffer.byteLength body)}))
              request (http/request
                        url
                        (clj->js {:method  (clojure.string/upper-case (name method))
                                  :headers headers})
                        (fn [response]
                            (let [s (atom nil)]
                                 (.on response "data" (fn [chunk]
                                                          (swap! s conj (.toString chunk "utf8"))))
                                 (.on response "end" (fn []
                                                         (resolve {:body    (apply str @s)
                                                                   :status  (.-statusCode response)
                                                                   :headers (js->clj (.-headers response))})))
                                 (.on response "error" reject))))]
             (.on request "error" reject)
             (when body
               (.write request body))
             (.end request)))))

(def handle
  (eval (symbol (.-_HANDLER js/process.env))))

(defn post-error [{error :error
                   {aws-request-id :aws-request-id} :context}]
  (let [url (str runtime-path "/invocation/" aws-request-id "/error")]
    (-> (request {:url url
                  :headers {"Content-Type" "application/json"
                            "Lambda-Runtime-Function-Error-Type" (.-name error)}
                  :body (js/JSON.stringify #js {:errorType (.-name error)
                                                :errorMessage (.-message error)
                                                :stackTrace (-> (or (.-stack error) "")
                                                                (.split "\n")
                                                                (.slice 1))})})
        (.then (fn [{:keys [status]
                     :as res}]
                 (assert (= status 200)
                         (str "Unexpected " url "response: " (js/JSON.stringify res)))
                 res)))))

(defonce state (atom nil))

(defn start []
  (-> (request {:url (str runtime-path "/invocation/next")})
      (.then (fn [{:keys                                                                       [status body]
                   {aws-request-id                      "lambda-runtime-aws-request-id"
                    lambda-runtime-invoked-function-arn "lambda-runtime-invoked-function-arn"} :headers
                   :as                                                                         response}]
               (let [context {:aws-request-id aws-request-id
                              :lambda-runtime-invoked-function-arn lambda-runtime-invoked-function-arn}]
                 (swap! state assoc :context context)
                 (assert (= status 200) (str "Unexpected /invocation/next response: " (pr-str response)))
                 {:event   (-> (js/JSON.parse body)
                               js->clj)
                  :context context})))
      (.then handle)
      (.then (fn [response]
               (let [{:keys [aws-request-id]} (:context @state)]
                 (request {:url    (str runtime-path "/invocation/" aws-request-id "/response")
                           :method :post
                           :headers {"Content-Type" "application/json"}
                           :body   (-> (clj->js response)
                                       js/JSON.stringify)}))))
      (.then (fn [{:keys [status]
                   :as response}]
               (assert (= status 202) (str "Unexpected /invocation/response response:" (pr-str response)))))
      (.catch (fn [err]
                (post-error {:error err
                             :context (:context @state)})))
      (.then start)))

(start)
