(ns htmx-app.middleware.core
  (:require [ring.middleware.defaults       :refer [wrap-defaults site-defaults]]
            [ring.middleware.anti-forgery   :refer [wrap-anti-forgery]]
            [ring.middleware.session        :refer [wrap-session]]
            [jdbc-ring-session.core         :refer [jdbc-store]]
            [htmx-app.middleware.logging    :refer [wrap-request-logging]]
            [htmx-app.middleware.auth       :refer [wrap-authentication]]))

(defn wrap-datasource [handler datasource]
  (fn [request]
    (handler (assoc request :datasource datasource))))

(defn make-middleware-stack [datasource]
  (fn [handler]
    (-> handler
        wrap-authentication
        wrap-request-logging
        wrap-anti-forgery
        (wrap-session {:store        (jdbc-store datasource {:table :sessions})
                       :cookie-attrs {:http-only true
                                      :same-site :lax}})
        (wrap-datasource datasource)
        (wrap-defaults (-> site-defaults
                           (assoc-in [:security :anti-forgery] false))))))
