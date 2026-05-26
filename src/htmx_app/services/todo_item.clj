(ns htmx-app.services.todo-item
  (:require [htmx-app.repository.todo-item :as repo]
            [clojure.string                :as str]))

(defn validate-title [title]
  (cond
    (str/blank? title)     "Title cannot be blank"
    (> (count title) 200)  "Title must be 200 characters or less"
    :else nil))

(defn find-all-by-list [ds list-id]
  (repo/find-all-by-list ds list-id))

(defn find-by-id [ds id]
  (repo/find-by-id ds id))

(defn create! [ds list-id title]
  (repo/create! ds list-id title))

(defn update-title! [ds id title]
  (repo/update-title! ds id title))

(defn toggle-done! [ds id]
  (repo/toggle-done! ds id))

(defn delete! [ds id]
  (repo/delete! ds id))
