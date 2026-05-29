(ns htmx-app.middleware.core
  (:require [ring.middleware.defaults        :refer [wrap-defaults site-defaults]]
            [ring.middleware.anti-forgery    :refer [wrap-anti-forgery]]
            [ring.middleware.session         :refer [wrap-session]]
            [ring.middleware.session.memory  :refer [memory-store]]
            [htmx-app.middleware.correlation :refer [wrap-correlation-id]]
            [htmx-app.middleware.logging     :refer [wrap-request-logging]]
            [htmx-app.middleware.response-headers :refer [wrap-user-headers]]
            [htmx-app.middleware.auth        :refer [wrap-authentication]]
            [htmx-app.middleware.error       :refer [wrap-errors]]
            [htmx-app.services.jwt          :as jwt-svc]))

(defn wrap-datasource [handler datasource]
  (fn [request]
    (handler (assoc request :datasource datasource))))

(defn wrap-keypair [handler keypair]
  (fn [request]
    (handler (assoc request :keypair keypair))))

(defn make-middleware-stack [datasource jwt-config {:keys [dev?]}]
  (let [keypair (jwt-svc/load-keypair jwt-config)]
    (fn [handler]
      (-> handler
          (wrap-authentication keypair)
          wrap-user-headers
          wrap-request-logging
          wrap-anti-forgery
          (wrap-session {:store        (memory-store)
                         :cookie-attrs (cond-> {:http-only true :same-site :lax}
                                         (not dev?) (assoc :secure true))})
          wrap-correlation-id
          (wrap-keypair keypair)
          (wrap-datasource datasource)
          (wrap-defaults (-> site-defaults
                             (assoc-in [:security :anti-forgery] false)
                             (assoc-in [:session] false)))
          (wrap-errors {:dev? dev?})))))
