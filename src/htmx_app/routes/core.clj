(ns htmx-app.routes.core
  (:require [reitit.ring              :as ring]
            [htmx-app.handlers.home  :as home]
            [htmx-app.handlers.admin :as admin]
            [htmx-app.middleware.auth :refer [wrap-authorization]]))

(defn app-routes [_datasource]
  (ring/ring-handler
    (ring/router
      [["/"               {:get home/index}]
       ["/login"          {:get  home/login-page
                           :post home/login}]
       ["/logout"         {:post home/logout}]
       ["/partials/items" {:get home/items-partial-handler}]

       ["/admin"
        {:middleware [[wrap-authorization "admin"]]}
        [""       {:get admin/index}]
        ["/users" {:get admin/users}]]])

    (ring/routes
      (ring/create-resource-handler {:path "/"})
      (ring/create-default-handler
        {:not-found (constantly {:status 404 :body "Not found"})}))))
