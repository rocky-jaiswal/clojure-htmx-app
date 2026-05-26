(ns htmx-app.services.todo-item
  (:require [htmx-app.repository.todo-item :as repo]
            [htmx-app.schemas              :as schemas]
            [htmx-app.validation           :as validation]))

(defn validate-title [title]
  (validation/first-error schemas/title-schema title))

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
