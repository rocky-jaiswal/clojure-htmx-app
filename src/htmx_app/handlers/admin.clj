(ns htmx-app.handlers.admin
  (:require [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [htmx-app.views.admin         :as views]
            [htmx-app.services.user       :as user-service]))

(defn index [{:keys [identity]}]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (views/admin-page {:csrf-token *anti-forgery-token*
                               :identity   identity})})

(defn users [{:keys [datasource identity]}]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (views/users-page {:csrf-token *anti-forgery-token*
                               :identity   identity
                               :users      (user-service/find-all datasource)})})
