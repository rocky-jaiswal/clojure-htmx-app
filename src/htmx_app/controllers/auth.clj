(ns htmx-app.controllers.auth
  (:require [ring.util.response           :as resp]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [htmx-app.commands.auth       :as auth-cmd]
            [htmx-app.views.auth          :as views]))

(defn- auth-cookie [token]
  {:value     token
   :http-only true
   :path      "/"
   :same-site :lax})

(defn- clear-cookie []
  {:value   ""
   :http-only true
   :path    "/"
   :max-age 0})

(defn register-page [_request]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (views/register-page {:csrf-token *anti-forgery-token*})})

(defn register [{:keys [params datasource keypair]}]
  (let [result (auth-cmd/register! datasource keypair
                                   {:email            (:email params)
                                    :password         (:password params)
                                    :confirm-password (:confirm-password params)})]
    (if-let [error (:error result)]
      {:status  200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (views/register-page {:csrf-token *anti-forgery-token* :error error})}
      (-> (resp/redirect "/lists")
          (assoc-in [:cookies "auth_token"] (auth-cookie (:ok result)))))))

(defn login-page [_request]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (views/login-page {:csrf-token *anti-forgery-token*})})

(defn login [{:keys [params datasource keypair]}]
  (let [result (auth-cmd/login datasource keypair
                               {:email    (:email params)
                                :password (:password params)})]
    (if-let [error (:error result)]
      {:status  200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (views/login-page {:csrf-token *anti-forgery-token* :error error})}
      (-> (resp/redirect "/lists")
          (assoc-in [:cookies "auth_token"] (auth-cookie (:ok result)))))))

(defn logout [_request]
  (-> (resp/redirect "/login")
      (assoc-in [:cookies "auth_token"] (clear-cookie))))
