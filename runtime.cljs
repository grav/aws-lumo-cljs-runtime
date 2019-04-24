(ns runtime
  (:require clojure.string
            http))

(def runtime-path (str "http://" (.-AWS_LAMBDA_RUNTIME_API js/process.env) "/2018-06-01/runtime"))

(defn successful?
  [status]
  (<= 200 status 299))

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
  (do (assert (.-_HANDLER js/process.env) "The _HANDLER env vars must contain the handler location.\n\nSee https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html\n")
      (eval (symbol (.-_HANDLER js/process.env)))))

(defn post-error [{error :error
                   {aws-request-id :aws-request-id} :context}]
  (let [url (str runtime-path "/invocation/" aws-request-id "/error")
        runtime-error #js {:errorType (.-name error)
                           :errorMessage (.-message error)
                           :stackTrace (-> (or (.-stack error) "")
                                           (.split "\n")
                                           (.slice 1))}]
    (-> (request {:url url
                  :method :post
                  :headers {"Content-Type" "application/json"
                            "Lambda-Runtime-Function-Error-Type" (.-name error)}
                  :body (js/JSON.stringify runtime-error)})
        (.then (fn [{:keys [status]
                     :as res}]
                 ;; We treat all errors from the API as an unrecoverable error. This is
                 ;; because the API returns 4xx errors for responses that are too long. In
                 ;; that case, we simply log the output and fail.
                 (assert (successful? status) (str "Error from Runtime API\n"
                                                   (-> {:url url
                                                        :response res
                                                        :error runtime-error}
                                                       (clj->js)
                                                       (js/JSON.stringify nil 2))))
                 res)))))

(defonce state (atom nil))

(defn -main
  [& args]
  (let [url (str runtime-path "/invocation/next")]
    (-> (request {:url url})
        (.then (fn [{:keys [status body]
                     {aws-request-id "lambda-runtime-aws-request-id"
                      lambda-runtime-invoked-function-arn "lambda-runtime-invoked-function-arn"} :headers
                     :as response}]

                 ;; We treat all errors from the API as an unrecoverable error. This is
                 ;; because the API returns 4xx errors for responses that are too long. In
                 ;; that case, we simply log the output and fail.
                 (assert (successful? status) (str "Error from Runtime API\n"
                                                   (-> {:url url
                                                        :response response
                                                        :context @state}
                                                       (clj->js)
                                                       (js/JSON.stringify nil 2))))

                 (let [context {:aws-request-id aws-request-id
                                :lambda-runtime-invoked-function-arn lambda-runtime-invoked-function-arn}]
                   (swap! state assoc :context context)

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
                 (assert (successful? status) (str "Error from Runtime API\n"
                                                   (-> {:context @state
                                                        :response response}
                                                       (clj->js)
                                                       (js/JSON.stringify nil 2))))))
        (.catch (fn [err]
                  (post-error {:error err
                               :context (:context @state)})))
        (.then -main))))
