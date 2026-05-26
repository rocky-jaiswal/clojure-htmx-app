(ns htmx-app.commands.list
  (:require [htmx-app.services.todo-list :as list-svc]
            [htmx-app.services.todo-item :as item-svc]
            [htmx-app.commands.pipeline  :as pipe]
            [malli.core                  :as m]))

(defn get-lists [ds user-id]
  {:ok (list-svc/find-all-by-user ds user-id)})

(defn get-list [ds list-id user-id]
  (-> (list-svc/find-by-id ds list-id)
      pipe/found
      (pipe/owned-by #(list-svc/owned-by? % user-id))
      (pipe/then (fn [list]
                   {:ok {:list  list
                         :items (item-svc/find-all-by-list ds list-id)}}))))

(defn create-list! [ds user-id name]
  (if-let [err (list-svc/validate-name name)]
    {:error err}
    {:ok (list-svc/create! ds user-id name)}))

(defn update-list! [ds list-id user-id name]
  (-> (list-svc/find-by-id ds list-id)
      pipe/found
      (pipe/owned-by #(list-svc/owned-by? % user-id))
      (pipe/valid    #(list-svc/validate-name name))
      (pipe/then     (fn [_] {:ok (list-svc/update-name! ds list-id name)}))))

(defn delete-list! [ds list-id user-id]
  (-> (list-svc/find-by-id ds list-id)
      pipe/found
      (pipe/owned-by #(list-svc/owned-by? % user-id))
      (pipe/then     (fn [_]
                       (list-svc/delete! ds list-id)
                       {:ok true}))))

(m/=> get-lists    [:=> [:cat :any pos-int?]                  pipe/Result])
(m/=> get-list     [:=> [:cat :any pos-int? pos-int?]         pipe/Result])
(m/=> create-list! [:=> [:cat :any pos-int? :string]          pipe/Result])
(m/=> update-list! [:=> [:cat :any pos-int? pos-int? :string] pipe/Result])
(m/=> delete-list! [:=> [:cat :any pos-int? pos-int?]         pipe/Result])
