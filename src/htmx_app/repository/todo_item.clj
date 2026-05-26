(ns htmx-app.repository.todo-item
  (:require [next.jdbc :as jdbc]))

(defn find-all-by-list [ds list-id]
  (jdbc/execute! ds
                 ["SELECT * FROM todo_items WHERE list_id = ? ORDER BY created_at ASC" list-id]))

(defn find-by-id [ds id]
  (jdbc/execute-one! ds ["SELECT * FROM todo_items WHERE id = ?" id]))

(defn create! [ds list-id title]
  (jdbc/execute-one! ds
                     ["INSERT INTO todo_items (list_id, title) VALUES (?, ?) RETURNING *" list-id title]))

(defn update-title! [ds id title]
  (jdbc/execute-one! ds
                     ["UPDATE todo_items SET title = ?, updated_at = NOW() WHERE id = ? RETURNING *" title id]))

(defn toggle-done! [ds id]
  (jdbc/execute-one! ds
                     ["UPDATE todo_items SET done = NOT done, updated_at = NOW() WHERE id = ? RETURNING *" id]))

(defn delete! [ds id]
  (jdbc/execute-one! ds ["DELETE FROM todo_items WHERE id = ?" id]))
