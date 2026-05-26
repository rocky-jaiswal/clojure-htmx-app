(ns htmx-app.services.items
  (:require [next.jdbc :as jdbc]))

(defn find-all [ds]
  (jdbc/execute! ds ["SELECT * FROM items ORDER BY created_at DESC"]))
