(ns htmx-app.controllers.health
  (:require [next.jdbc :as jdbc]))

(defn health-handler [datasource]
  (fn [_request]
    (try
      (jdbc/execute-one! datasource ["SELECT 1"])
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body    "{\"status\":\"ok\"}"}
      (catch Exception _
        {:status  503
         :headers {"Content-Type" "application/json"}
         :body    "{\"status\":\"error\"}"}))))
