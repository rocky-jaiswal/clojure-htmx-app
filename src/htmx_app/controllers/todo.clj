(ns htmx-app.controllers.todo
  (:require [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.util.response           :as resp]
            [hiccup2.core                 :as h]
            [htmx-app.commands.todo       :as todo-cmd]
            [htmx-app.views.todo          :as views]
            [htmx-app.views.partials.todo :as partials]))

(defn- html [hiccup]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (str (h/html hiccup))})

(defn- user-id [request]
  (get-in request [:identity :id]))

(defn lists-page [{:keys [datasource] :as req}]
  (let [{:keys [ok]} (todo-cmd/get-lists datasource (user-id req))]
    {:status  200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body    (views/lists-page {:csrf-token *anti-forgery-token*
                                 :identity   (:identity req)
                                 :lists      ok})}))

(defn create-list! [{:keys [params datasource] :as req}]
  (todo-cmd/create-list! datasource (user-id req) (:name params))
  (resp/redirect "/lists"))

(defn list-row [{:keys [path-params datasource] :as req}]
  (let [result (todo-cmd/get-list datasource (parse-long (:id path-params)) (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      (html (partials/list-row (:list (:ok result)))))))

(defn edit-list-form [{:keys [path-params datasource] :as req}]
  (let [result (todo-cmd/get-list datasource (parse-long (:id path-params)) (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      (html (partials/list-edit-form (:list (:ok result)))))))

(defn update-list! [{:keys [path-params params datasource] :as req}]
  (let [result (todo-cmd/update-list! datasource (parse-long (:id path-params)) (user-id req) (:name params))]
    (case (:error result)
      :not-found {:status 404 :body "Not found"}
      :forbidden {:status 403 :body "Forbidden"}
      (if (string? (:error result))
        {:status 422 :body (:error result)}
        (html (partials/list-row (:ok result)))))))

(defn delete-list! [{:keys [path-params datasource] :as req}]
  (let [result (todo-cmd/delete-list! datasource (parse-long (:id path-params)) (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      {:status 200 :body ""})))

(defn list-detail [{:keys [path-params datasource] :as req}]
  (let [result (todo-cmd/get-list datasource (parse-long (:id path-params)) (user-id req))]
    (case (:error result)
      :not-found {:status 404 :body "Not found"}
      :forbidden {:status 403 :body "Forbidden"}
      {:status  200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (views/list-detail-page {:csrf-token *anti-forgery-token*
                                         :identity   (:identity req)
                                         :list       (:list (:ok result))
                                         :items      (:items (:ok result))})})))

(defn add-item! [{:keys [path-params params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))]
    (todo-cmd/add-item! datasource list-id (user-id req) (:title params))
    (resp/redirect (str "/lists/" list-id))))

(defn item-row [{:keys [path-params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (todo-cmd/get-list datasource list-id (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      (let [item (first (filter #(= (:todo_items/id %) item-id) (:items (:ok result))))]
        (if-not item
          {:status 404 :body "Not found"}
          (html (partials/item-row list-id item)))))))

(defn edit-item-form [{:keys [path-params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (todo-cmd/get-list datasource list-id (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      (let [item (first (filter #(= (:todo_items/id %) item-id) (:items (:ok result))))]
        (if-not item
          {:status 404 :body "Not found"}
          (html (partials/item-edit-form list-id item)))))))

(defn update-item! [{:keys [path-params params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (todo-cmd/update-item! datasource list-id item-id (user-id req) (:title params))]
    (case (:error result)
      :not-found {:status 404 :body "Not found"}
      :forbidden {:status 403 :body "Forbidden"}
      (if (string? (:error result))
        {:status 422 :body (:error result)}
        (html (partials/item-row list-id (:ok result)))))))

(defn toggle-item! [{:keys [path-params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (todo-cmd/toggle-item! datasource list-id item-id (user-id req))]
    (case (:error result)
      :not-found {:status 404 :body "Not found"}
      :forbidden {:status 403 :body "Forbidden"}
      (html (partials/item-row list-id (:ok result))))))

(defn delete-item! [{:keys [path-params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        item-id (parse-long (:item-id path-params))
        result  (todo-cmd/delete-item! datasource list-id item-id (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      {:status 200 :body ""})))
