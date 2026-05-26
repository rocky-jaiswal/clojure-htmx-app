(ns htmx-app.middleware.logging
  (:require [taoensso.timbre :as log]))

(defn wrap-request-logging [handler]
  (fn [request]
    (let [start    (System/currentTimeMillis)
          response (handler request)
          elapsed  (- (System/currentTimeMillis) start)]
      (log/info {:method  (name (:request-method request))
                 :uri     (:uri request)
                 :status  (:status response)
                 :elapsed elapsed})
      response)))
