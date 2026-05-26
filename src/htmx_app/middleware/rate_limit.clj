(ns htmx-app.middleware.rate-limit
  (:require [taoensso.timbre :as log]))

(defn- now-ms [] (System/currentTimeMillis))

(defn- rate-limited? [!state ip max-requests window-ms]
  (let [cutoff   (- (now-ms) window-ms)
        limited? (volatile! false)]
    (swap! !state
           (fn [m]
             (let [hits (filterv #(> % cutoff) (get m ip []))]
               (if (>= (count hits) max-requests)
                 (do (vreset! limited? true) m)
                 (assoc m ip (conj hits (now-ms)))))))
    @limited?))

(defn make-middleware
  "Rate-limits by remote IP. max-requests allowed per window-ms milliseconds."
  [max-requests window-ms]
  (let [!state (atom {})]
    (fn [handler]
      (fn [request]
        (let [ip (or (get-in request [:headers "x-forwarded-for"])
                     (:remote-addr request))]
          (if (rate-limited? !state ip max-requests window-ms)
            (do
              (log/warn {:event :rate-limited :ip ip :uri (:uri request)})
              {:status  429
               :headers {"Content-Type" "text/html; charset=utf-8"
                         "Retry-After"  (str (quot window-ms 1000))}
               :body    "Too many requests. Please wait before trying again."})
            (handler request)))))))
