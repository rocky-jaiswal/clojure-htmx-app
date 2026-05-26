(ns htmx-app.commands.list
  (:require [htmx-app.services.todo-list :as list-svc]
            [htmx-app.services.todo-item :as item-svc]))

(defn get-lists [ds user-id]
  {:ok (list-svc/find-all-by-user ds user-id)})

(defn get-list [ds list-id user-id]
  (let [list (list-svc/find-by-id ds list-id)]
    (cond
      (nil? list)                             {:error :not-found}
      (not (list-svc/owned-by? list user-id)) {:error :forbidden}
      :else {:ok {:list  list
                  :items (item-svc/find-all-by-list ds list-id)}})))

(defn create-list! [ds user-id name]
  (if-let [err (list-svc/validate-name name)]
    {:error err}
    {:ok (list-svc/create! ds user-id name)}))

(defn update-list! [ds list-id user-id name]
  (let [list (list-svc/find-by-id ds list-id)]
    (cond
      (nil? list)                             {:error :not-found}
      (not (list-svc/owned-by? list user-id)) {:error :forbidden}
      :else (if-let [err (list-svc/validate-name name)]
              {:error err}
              {:ok (list-svc/update-name! ds list-id name)}))))

(defn delete-list! [ds list-id user-id]
  (let [list (list-svc/find-by-id ds list-id)]
    (cond
      (nil? list)                             {:error :not-found}
      (not (list-svc/owned-by? list user-id)) {:error :forbidden}
      :else (do (list-svc/delete! ds list-id) {:ok true}))))
