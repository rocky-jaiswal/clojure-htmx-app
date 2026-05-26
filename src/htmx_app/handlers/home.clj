(ns htmx-app.handlers.home
  (:require [ring.util.response              :as resp]
            [ring.middleware.anti-forgery    :refer [*anti-forgery-token*]]
            [malli.core                      :as m]
            [malli.error                     :as me]
            [hiccup2.core                    :as h]
            [htmx-app.views.home             :as views]
            [htmx-app.views.partials.items   :as items-partial]
            [htmx-app.services.items         :as item-service]
            [htmx-app.services.user          :as user-service]))

(def LoginSchema
  [:map
   [:email    [:string {:min 1 :max 255}]]
   [:password [:string {:min 8 :max 72}]]])

(defn index [{:keys [datasource identity]}]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (views/index-page
              {:csrf-token *anti-forgery-token*
               :identity   identity
               :items      (item-service/find-all datasource)})})

(defn items-partial-handler [{:keys [datasource]}]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (str (h/html (items-partial/items-list (item-service/find-all datasource))))})

(defn login-page [request]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (views/login-page {:csrf-token *anti-forgery-token*})})

(defn login [{:keys [params datasource session]}]
  (let [errs (m/explain LoginSchema params)]
    (if errs
      {:status  200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (views/login-page {:csrf-token *anti-forgery-token*
                                   :error      (first (vals (me/humanize errs)))})}
      (let [user (user-service/find-by-email datasource (:email params))]
        (if (and user (user-service/verify-password user (:password params)))
          (-> (resp/redirect "/")
              (assoc :session (assoc session :user {:id    (:users/id user)
                                                    :email (:users/email user)
                                                    :role  (:users/role user)})))
          {:status  200
           :headers {"Content-Type" "text/html; charset=utf-8"}
           :body    (views/login-page {:csrf-token *anti-forgery-token*
                                       :error      "Invalid email or password."})})))))

(defn logout [_request]
  (-> (resp/redirect "/login")
      (assoc :session nil)))
