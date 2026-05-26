(ns htmx-app.services.user
  (:require [next.jdbc :as jdbc]
            [buddy.hashers :as hashers]))

(defn find-by-email [ds email]
  (jdbc/execute-one! ds ["SELECT * FROM users WHERE email = ?" email]))

(defn find-all [ds]
  (jdbc/execute! ds ["SELECT id, email, role, created_at FROM users ORDER BY created_at DESC"]))

(defn verify-password [user raw-password]
  (hashers/check raw-password (:users/password user)))

(defn create-user! [ds {:keys [email password role]}]
  (jdbc/execute-one! ds
    ["INSERT INTO users (email, password, role) VALUES (?, ?, ?) RETURNING *"
     email (hashers/derive password) (or role "user")]))
