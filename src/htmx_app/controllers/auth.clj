(ns htmx-app.controllers.auth
  (:require [ring.util.response           :as resp]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [htmx-app.commands.auth       :as auth-cmd]
            [htmx-app.views.auth          :as views]))

(defn register-page [_request]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (views/register-page {:csrf-token *anti-forgery-token*})})

(defn register [{:keys [params datasource]}]
  (let [result (auth-cmd/register! datasource
                                   {:email            (:email params)
                                    :password         (:password params)
                                    :confirm-password (:confirm-password params)})]
    (if-let [error (:error result)]
      {:status  200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (views/register-page {:csrf-token *anti-forgery-token* :error error})}
      (-> (resp/redirect "/lists")
          (assoc :session {:user (:ok result)})))))

(defn login-page [_request]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (views/login-page {:csrf-token *anti-forgery-token*})})

(defn login [{:keys [params datasource session]}]
  (let [result (auth-cmd/login datasource
                               {:email    (:email params)
                                :password (:password params)})]
    (if-let [error (:error result)]
      {:status  200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (views/login-page {:csrf-token *anti-forgery-token* :error error})}
      (-> (resp/redirect "/lists")
          (assoc :session (assoc session :user (:ok result)))))))

(defn logout [_request]
  (-> (resp/redirect "/login")
      (assoc :session nil)))
