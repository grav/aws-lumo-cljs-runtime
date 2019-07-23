(ns aws-sdk-example.core)

;; important to do this outside the handler,
;; else we'll probably get a time-out ->
;; https://stackoverflow.com/a/54823567/202538
(def aws (js/require "aws-sdk"))

(defn list-buckets [_]
  (js/Promise. (fn [resolve reject]
                 (.listBuckets (aws.S3.) 
                   (fn [err data] 
                     (if err 
                       (reject err) 
                       (resolve data)))))))
