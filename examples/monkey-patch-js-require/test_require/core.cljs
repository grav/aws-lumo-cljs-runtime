(ns test-require.core)

(defn run-tests []

    (assert (= 42 (.tell_me (js/require "./some-location/node_modules/meaning-of-life"))))

    (let [req' js/require]
      (def require'
        (fn [s]
          (try (req' s)
            (catch js/Error e
              (req' (str "./some-location/node_modules/" s)))))))

    (assert (= 42 (.tell_me (require' "meaning-of-life"))))

    #_(let [req' js/require]
       (set! js/require
         (fn [s]
           (try
             (req' s)
             (catch js/Error e
               (try
                 (req' (str "./some-location/node_modules/" s))
                 (catch js/Error _
                   ;; throw original error if fallback fails
                   (throw e))))))))

    (assert (= 42 (.tell_me (js/require "meaning-of-life"))))
    true)

    
