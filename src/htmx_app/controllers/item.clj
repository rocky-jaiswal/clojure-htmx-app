(ns htmx-app.controllers.item
  (:require [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.util.response           :as resp]
            [hiccup2.core                 :as h]
            [htmx-app.commands.list       :as list-cmd]
            [htmx-app.commands.item       :as item-cmd]
            [htmx-app.views.todo          :as views]
            [htmx-app.views.partials.todo :as partials]))

(defn- html [hiccup]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (str (h/html hiccup))})

(defn- user-id [request]
  (get-in request [:identity :id]))

(defn add-item! [{:keys [path-params params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        result  (item-cmd/add-item! datasource list-id (user-id req) (:title params))]
    (if-let [error (:error result)]
      (let [list-result (list-cmd/get-list datasource list-id (user-id req))]
        (if (:error list-result)
          {:status 404 :body "Not found"}
          {:status  422
           :headers {"Content-Type" "text/html; charset=utf-8"}
           :body    (views/list-detail-page {:csrf-token *anti-forgery-token*
                                             :identity   (:identity req)
                                             :list       (:list (:ok list-result))
                                             :items      (:items (:ok list-result))
                                             :error      error})}))
      (resp/redirect (str "/lists/" list-id)))))

(defn item-row [{:keys [path-params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (list-cmd/get-list datasource list-id (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      (let [item (first (filter #(= (:todo_items/id %) item-id) (:items (:ok result))))]
        (if-not item
          {:status 404 :body "Not found"}
          (html (partials/item-row list-id item)))))))

(defn edit-item-form [{:keys [path-params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (list-cmd/get-list datasource list-id (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      (let [item (first (filter #(= (:todo_items/id %) item-id) (:items (:ok result))))]
        (if-not item
          {:status 404 :body "Not found"}
          (html (partials/item-edit-form list-id item)))))))

(defn update-item! [{:keys [path-params params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (item-cmd/update-item! datasource list-id item-id (user-id req) (:title params))]
    (case (:error result)
      :not-found {:status 404 :body "Not found"}
      :forbidden {:status 403 :body "Forbidden"}
      (if (string? (:error result))
        (html (partials/item-edit-form list-id {:todo_items/id    item-id
                                                :todo_items/title (:title params)
                                                :error            (:error result)}))
        (html (partials/item-row list-id (:ok result)))))))

(defn toggle-item! [{:keys [path-params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (item-cmd/toggle-item! datasource list-id item-id (user-id req))]
    (case (:error result)
      :not-found {:status 404 :body "Not found"}
      :forbidden {:status 403 :body "Forbidden"}
      (html (partials/item-row list-id (:ok result))))))

(defn delete-item! [{:keys [path-params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (item-cmd/delete-item! datasource list-id item-id (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      {:status 200 :body ""})))
