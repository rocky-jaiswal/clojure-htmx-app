(ns htmx-app.services.todo-list
  (:require [htmx-app.repository.todo-list :as repo]
            [clojure.string                :as str]))

(defn validate-name [name]
  (cond
    (str/blank? name)      "Name cannot be blank"
    (> (count name) 100)   "Name must be 100 characters or less"
    :else nil))

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
