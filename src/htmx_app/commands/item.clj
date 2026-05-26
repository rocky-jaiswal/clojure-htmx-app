(ns htmx-app.commands.item
  (:require [htmx-app.services.todo-list :as list-svc]
            [htmx-app.services.todo-item :as item-svc]))

(defn add-item! [ds list-id user-id title]
  (let [list (list-svc/find-by-id ds list-id)]
    (cond
      (nil? list)                             {:error :not-found}
      (not (list-svc/owned-by? list user-id)) {:error :forbidden}
      :else (if-let [err (item-svc/validate-title title)]
              {:error err}
              {:ok (item-svc/create! ds list-id title)}))))

(defn update-item! [ds list-id item-id user-id title]
  (let [list (list-svc/find-by-id ds list-id)
        item (item-svc/find-by-id ds item-id)]
    (cond
      (or (nil? list) (nil? item))            {:error :not-found}
      (not (list-svc/owned-by? list user-id)) {:error :forbidden}
      :else (if-let [err (item-svc/validate-title title)]
              {:error err}
              {:ok (item-svc/update-title! ds item-id title)}))))

(defn toggle-item! [ds list-id item-id user-id]
  (let [list (list-svc/find-by-id ds list-id)
        item (item-svc/find-by-id ds item-id)]
    (cond
      (or (nil? list) (nil? item))            {:error :not-found}
      (not (list-svc/owned-by? list user-id)) {:error :forbidden}
      :else {:ok (item-svc/toggle-done! ds item-id)})))

(defn delete-item! [ds list-id item-id user-id]
  (let [list (list-svc/find-by-id ds list-id)
        item (item-svc/find-by-id ds item-id)]
    (cond
      (or (nil? list) (nil? item))            {:error :not-found}
      (not (list-svc/owned-by? list user-id)) {:error :forbidden}
      :else (do (item-svc/delete! ds item-id) {:ok true}))))
