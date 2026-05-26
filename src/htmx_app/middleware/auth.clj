(ns htmx-app.middleware.auth
  (:require [ring.util.response :as resp]))

(defn wrap-authentication [handler]
  (fn [request]
    (let [user (get-in request [:session :user])]
      (handler (assoc request :identity user)))))

(defn wrap-require-auth [handler]
  (fn [request]
    (if (:identity request)
      (handler request)
      (resp/redirect "/login"))))

(defn wrap-authorization [handler required-role]
  (fn [request]
    (let [role (get-in request [:identity :role])]
      (if (= role required-role)
        (handler request)
        (-> (resp/response "Forbidden")
            (resp/status 403))))))
