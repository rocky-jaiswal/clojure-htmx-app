(ns htmx-app.routes.core
  (:require [reitit.ring                    :as ring]
            [ring.util.response             :as resp]
            [htmx-app.controllers.auth      :as auth]
            [htmx-app.controllers.list      :as list-ctrl]
            [htmx-app.controllers.item      :as item-ctrl]
            [htmx-app.controllers.health    :as health]
            [htmx-app.middleware.auth       :refer [wrap-require-auth wrap-authorization]]
            [htmx-app.middleware.rate-limit :as rate-limit]))

(defn app-routes [datasource]
  (let [auth-limiter (rate-limit/make-middleware 10 60000)]
    (ring/ring-handler
     (ring/router
      [["/"        {:get (fn [_] (resp/redirect "/lists"))}]
       ["/health"  {:get (health/health-handler datasource)}]

       ["/register" {:middleware [auth-limiter]
                     :get  auth/register-page
                     :post auth/register}]
       ["/login"    {:middleware [auth-limiter]
                     :get  auth/login-page
                     :post auth/login}]
       ["/logout"   {:post auth/logout}]

       ["/lists"
        {:middleware [[wrap-require-auth]]}
        [""    {:get  list-ctrl/lists-page
                :post list-ctrl/create-list!}]
        ["/:id"
         [""         {:get    list-ctrl/list-detail
                      :put    list-ctrl/update-list!
                      :delete list-ctrl/delete-list!}]
         ["/row"     {:get list-ctrl/list-row}]
         ["/edit"    {:get list-ctrl/edit-list-form}]
         ["/items"
          [""                     {:post item-ctrl/add-item!}]
          ["/:item-id"
           [""      {:put    item-ctrl/update-item!
                     :patch  item-ctrl/toggle-item!
                     :delete item-ctrl/delete-item!}]
           ["/row"  {:get item-ctrl/item-row}]
           ["/edit" {:get item-ctrl/edit-item-form}]]]]]

       ["/admin"
        {:middleware [[wrap-authorization "admin"]]}
        [""       {:get (fn [_] {:status 200 :headers {"Content-Type" "text/html"} :body "Admin"})}]]])

     (ring/routes
      (ring/create-resource-handler {:path "/"})
      (ring/create-default-handler
       {:not-found (constantly {:status 404 :body "Not found"})})))))
