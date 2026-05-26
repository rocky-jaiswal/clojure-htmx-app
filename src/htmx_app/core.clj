(ns htmx-app.core
  (:require [integrant.core             :as ig]
            [ring.adapter.jetty         :as jetty]
            [taoensso.timbre            :as log]
            [htmx-app.config            :as config]
            [htmx-app.db.core]
            [htmx-app.db.migrations]
            [htmx-app.routes.core       :as routes]
            [htmx-app.middleware.core   :as middleware])
  (:gen-class))

(defmethod ig/init-key :htmx-app.core/jetty [_ {:keys [port datasource]}]
  (log/info "Starting HTTP server on port" port)
  (let [handler (-> (routes/app-routes datasource)
                    ((middleware/make-middleware-stack datasource)))]
    (jetty/run-jetty handler {:port  port
                              :join? false})))

(defmethod ig/halt-key! :htmx-app.core/jetty [_ server]
  (log/info "Stopping HTTP server")
  (.stop server))

(defn -main [& _args]
  (let [system (ig/init (config/system-config :prod))]
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(ig/halt! system)))))
