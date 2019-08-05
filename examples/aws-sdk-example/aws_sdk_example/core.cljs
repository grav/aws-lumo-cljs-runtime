(ns aws-sdk-example.core)

;; important to do this outside the handler,
;; else we'll probably get a time-out ->
;; https://stackoverflow.com/a/54823567/202538

#_(let [req' js/require]
  (set! goog.global.require
    (fn [s]
      (req' (str "/opt/nodejs/node_modules/" s)))))

(def aws (js/require "/opt/nodejs/node_modules/aws-sdk"))

(defn list-buckets [_]
  (js/Promise. (fn [resolve reject]
                 (.listBuckets (aws.S3.) 
                   (fn [err data] 
                     (if err 
                       (reject err) 
                       (resolve data)))))))
