(ns htmx-app.repository.todo-list
  (:require [next.jdbc :as jdbc]))

(defn find-all-by-user [ds user-id]
  (jdbc/execute! ds
                 ["SELECT * FROM todo_lists WHERE user_id = ? ORDER BY created_at DESC" user-id]))

(defn find-by-id [ds id]
  (jdbc/execute-one! ds
                     ["SELECT * FROM todo_lists WHERE id = ?" id]))

(defn create! [ds user-id name]
  (jdbc/execute-one! ds
                     ["INSERT INTO todo_lists (user_id, name) VALUES (?, ?) RETURNING *" user-id name]))

(defn update-name! [ds id name]
  (jdbc/execute-one! ds
                     ["UPDATE todo_lists SET name = ?, updated_at = NOW() WHERE id = ? RETURNING *" name id]))

(defn delete! [ds id]
  (jdbc/execute-one! ds ["DELETE FROM todo_lists WHERE id = ?" id]))
