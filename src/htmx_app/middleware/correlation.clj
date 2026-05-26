(ns htmx-app.middleware.correlation
  (:require [taoensso.timbre :as log])
  (:import [java.util UUID]))

(defn wrap-correlation-id [handler]
  (fn [request]
    (let [corr-id (or (get-in request [:headers "x-correlation-id"])
                      (str (UUID/randomUUID)))]
      (log/with-context+ {:correlation-id corr-id}
        (-> request
            (assoc :correlation-id corr-id)
            handler
            (assoc-in [:headers "X-Correlation-ID"] corr-id))))))
