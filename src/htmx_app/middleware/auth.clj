(ns htmx-app.middleware.auth
  (:require [ring.util.response    :as resp]
            [htmx-app.services.jwt :as jwt-svc]))

(defn wrap-authentication [handler keypair]
  (fn [request]
    (let [token    (get-in request [:cookies "auth_token" :value])
          identity (when token (:ok (jwt-svc/verify token keypair)))]
      (handler (assoc request :identity identity)))))

(defn wrap-require-auth [handler]
  (fn [request]
    (if (:identity request)
      (resp/header (handler request) "Cache-Control" "no-store")
      (resp/redirect "/login"))))

(defn wrap-authorization [handler required-role]
  (fn [request]
    (let [role (get-in request [:identity :role])]
      (if (= role required-role)
        (handler request)
        (-> (resp/response "Forbidden")
            (resp/status 403))))))
