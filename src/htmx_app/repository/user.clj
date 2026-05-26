(ns htmx-app.repository.user
  (:require [next.jdbc :as jdbc]))

(defn find-by-email [ds email]
  (jdbc/execute-one! ds ["SELECT * FROM users WHERE email = ?" email]))

(defn find-all [ds]
  (jdbc/execute! ds ["SELECT id, email, role, created_at FROM users ORDER BY created_at DESC"]))

(defn create! [ds {:keys [email password role]}]
  (jdbc/execute-one! ds
                     ["INSERT INTO users (email, password, role) VALUES (?, ?, ?) RETURNING *"
                      email password (or role "user")]))
