(ns htmx-app.commands.item
  (:require [htmx-app.services.todo-list :as list-svc]
            [htmx-app.services.todo-item :as item-svc]
            [htmx-app.commands.pipeline  :as pipe]
            [malli.core                  :as m]))

(defn add-item! [ds list-id user-id title]
  (-> (list-svc/find-by-id ds list-id)
      pipe/found
      (pipe/owned-by #(list-svc/owned-by? % user-id))
      (pipe/valid    #(item-svc/validate-title title))
      (pipe/then     (fn [_] {:ok (item-svc/create! ds list-id title)}))))

(defn update-item! [ds list-id item-id user-id title]
  (-> (list-svc/find-by-id ds list-id)
      pipe/found
      (pipe/owned-by #(list-svc/owned-by? % user-id))
      (pipe/then     (fn [_] (item-svc/find-by-id ds item-id)))
      pipe/found
      (pipe/valid    #(item-svc/validate-title title))
      (pipe/then     (fn [_] {:ok (item-svc/update-title! ds item-id title)}))))

(defn toggle-item! [ds list-id item-id user-id]
  (-> (list-svc/find-by-id ds list-id)
      pipe/found
      (pipe/owned-by #(list-svc/owned-by? % user-id))
      (pipe/then     (fn [_] (item-svc/find-by-id ds item-id)))
      pipe/found
      (pipe/then     (fn [_] {:ok (item-svc/toggle-done! ds item-id)}))))

(defn delete-item! [ds list-id item-id user-id]
  (-> (list-svc/find-by-id ds list-id)
      pipe/found
      (pipe/owned-by #(list-svc/owned-by? % user-id))
      (pipe/then     (fn [_] (item-svc/find-by-id ds item-id)))
      pipe/found
      (pipe/then     (fn [_]
                       (item-svc/delete! ds item-id)
                       {:ok true}))))

(m/=> add-item!    [:=> [:cat :any pos-int? pos-int? :string]          pipe/Result])
(m/=> update-item! [:=> [:cat :any pos-int? pos-int? pos-int? :string] pipe/Result])
(m/=> toggle-item! [:=> [:cat :any pos-int? pos-int? pos-int?]         pipe/Result])
(m/=> delete-item! [:=> [:cat :any pos-int? pos-int? pos-int?]         pipe/Result])
