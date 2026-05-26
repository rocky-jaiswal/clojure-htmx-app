(ns htmx-app.middleware.core
  (:require [ring.middleware.defaults        :refer [wrap-defaults site-defaults]]
            [ring.middleware.anti-forgery    :refer [wrap-anti-forgery]]
            [ring.middleware.session         :refer [wrap-session]]
            [jdbc-ring-session.core          :refer [jdbc-store]]
            [htmx-app.middleware.correlation :refer [wrap-correlation-id]]
            [htmx-app.middleware.logging     :refer [wrap-request-logging]]
            [htmx-app.middleware.response-headers :refer [wrap-user-headers]]
            [htmx-app.middleware.auth        :refer [wrap-authentication]]
            [htmx-app.middleware.error       :refer [wrap-errors]]))

(defn wrap-datasource [handler datasource]
  (fn [request]
    (handler (assoc request :datasource datasource))))

(defn make-middleware-stack [datasource {:keys [dev?]}]
  (fn [handler]
    (-> handler
        wrap-authentication
        wrap-user-headers
        wrap-request-logging
        wrap-anti-forgery
        (wrap-session {:store        (jdbc-store datasource {:table :sessions})
                       :cookie-attrs (cond-> {:http-only true :same-site :lax}
                                       (not dev?) (assoc :secure true))})
        wrap-correlation-id
        (wrap-datasource datasource)
        (wrap-defaults (-> site-defaults
                           (assoc-in [:security :anti-forgery] false)))
        (wrap-errors {:dev? dev?}))))
