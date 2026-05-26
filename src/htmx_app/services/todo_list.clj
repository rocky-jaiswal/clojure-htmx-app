(ns htmx-app.services.todo-list
  (:require [htmx-app.repository.todo-list :as repo]
            [htmx-app.schemas              :as schemas]
            [htmx-app.validation           :as validation]))

(defn validate-name [name]
  (validation/first-error schemas/name-schema name))

(defn owned-by? [list user-id]
  (= (:todo_lists/user_id list) user-id))

(defn find-all-by-user [ds user-id]
  (repo/find-all-by-user ds user-id))

(defn find-by-id [ds id]
  (repo/find-by-id ds id))

(defn create! [ds user-id name]
  (repo/create! ds user-id name))

(defn update-name! [ds id name]
  (repo/update-name! ds id name))

(defn delete! [ds id]
  (repo/delete! ds id))
