(ns aws-sdk-example.core)
   
;; Monkey-patch js/require to look for node modules 
;; same place where the default nodejs runtime does 
;; (/opt/nodejs/node_modules)
(let [req' js/require]
  (set! goog.global.require
        (fn [s]
          (try
            (req' s)
            (catch js/Error e
              (try
                (req' (str "/opt/nodejs/node_modules/" s))
                (catch js/Error _
                  ;; throw original error if fallback fails
                  (throw e))))))))

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
