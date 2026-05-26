(ns htmx-app.controllers.list
  (:require [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.util.response           :as resp]
            [hiccup2.core                 :as h]
            [htmx-app.commands.list       :as list-cmd]
            [htmx-app.views.todo          :as views]
            [htmx-app.views.partials.todo :as partials]))

(defn- html [hiccup]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (str (h/html hiccup))})

(defn- user-id [request]
  (get-in request [:identity :id]))

(defn lists-page [{:keys [datasource] :as req}]
  (let [{:keys [ok]} (list-cmd/get-lists datasource (user-id req))]
    {:status  200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body    (views/lists-page {:csrf-token *anti-forgery-token*
                                 :identity   (:identity req)
                                 :lists      ok})}))

(defn create-list! [{:keys [params datasource] :as req}]
  (let [result (list-cmd/create-list! datasource (user-id req) (:name params))]
    (if-let [error (:error result)]
      (let [{:keys [ok]} (list-cmd/get-lists datasource (user-id req))]
        {:status  422
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body    (views/lists-page {:csrf-token *anti-forgery-token*
                                     :identity   (:identity req)
                                     :lists      ok
                                     :error      error})})
      (resp/redirect "/lists"))))

(defn list-detail [{:keys [path-params datasource] :as req}]
  (let [result (list-cmd/get-list datasource (parse-long (:id path-params)) (user-id req))]
    (case (:error result)
      :not-found {:status 404 :body "Not found"}
      :forbidden {:status 403 :body "Forbidden"}
      {:status  200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (views/list-detail-page {:csrf-token *anti-forgery-token*
                                         :identity   (:identity req)
                                         :list       (:list (:ok result))
                                         :items      (:items (:ok result))})})))

(defn list-row [{:keys [path-params datasource] :as req}]
  (let [result (list-cmd/get-list datasource (parse-long (:id path-params)) (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      (html (partials/list-row (:list (:ok result)))))))

(defn edit-list-form [{:keys [path-params datasource] :as req}]
  (let [result (list-cmd/get-list datasource (parse-long (:id path-params)) (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      (html (partials/list-edit-form (:list (:ok result)))))))

(defn update-list! [{:keys [path-params params datasource] :as req}]
  (let [list-id (parse-long (:id path-params))
        result  (list-cmd/update-list! datasource list-id (user-id req) (:name params))]
    (case (:error result)
      :not-found {:status 404 :body "Not found"}
      :forbidden {:status 403 :body "Forbidden"}
      (if (string? (:error result))
        (html (partials/list-edit-form {:todo_lists/id   list-id
                                        :todo_lists/name (:name params)
                                        :error           (:error result)}))
        (html (partials/list-row (:ok result)))))))

(defn delete-list! [{:keys [path-params datasource] :as req}]
  (let [result (list-cmd/delete-list! datasource (parse-long (:id path-params)) (user-id req))]
    (if (:error result)
      {:status 404 :body "Not found"}
      {:status 200 :body ""})))
