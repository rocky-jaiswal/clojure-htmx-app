(ns htmx-app.routes.core
  (:require [reitit.ring                    :as ring]
            [ring.util.response             :as resp]
            [htmx-app.controllers.auth      :as auth]
            [htmx-app.controllers.todo      :as todo]
            [htmx-app.middleware.auth       :refer [wrap-require-auth wrap-authorization]]))

(defn app-routes [_datasource]
  (ring/ring-handler
    (ring/router
      [["/"        {:get (fn [_] (resp/redirect "/lists"))}]

       ["/register" {:get  auth/register-page
                     :post auth/register}]
       ["/login"    {:get  auth/login-page
                     :post auth/login}]
       ["/logout"   {:post auth/logout}]

       ["/lists"
        {:middleware [[wrap-require-auth]]}
        [""    {:get  todo/lists-page
                :post todo/create-list!}]
        ["/:id"
         [""         {:get    todo/list-detail
                      :put    todo/update-list!
                      :delete todo/delete-list!}]
         ["/row"     {:get todo/list-row}]
         ["/edit"    {:get todo/edit-list-form}]
         ["/items"
          [""                     {:post todo/add-item!}]
          ["/:item-id"
           [""      {:put    todo/update-item!
                     :patch  todo/toggle-item!
                     :delete todo/delete-item!}]
           ["/row"  {:get todo/item-row}]
           ["/edit" {:get todo/edit-item-form}]]]]]

       ["/admin"
        {:middleware [[wrap-authorization "admin"]]}
        [""       {:get (fn [_] {:status 200 :headers {"Content-Type" "text/html"} :body "Admin"})}]]])

    (ring/routes
      (ring/create-resource-handler {:path "/"})
      (ring/create-default-handler
        {:not-found (constantly {:status 404 :body "Not found"})}))))
