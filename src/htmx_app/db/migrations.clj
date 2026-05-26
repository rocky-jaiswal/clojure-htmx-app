(ns htmx-app.db.migrations
  (:require [migratus.core :as migratus]
            [integrant.core :as ig]
            [taoensso.timbre :as log]))

(defn run-migrations! [datasource]
  (log/info "Running database migrations")
  (migratus/migrate {:store    :database
                     :db       {:datasource datasource}
                     :location "migrations"}))

(defmethod ig/init-key :htmx-app.db.migrations/run [_ {:keys [datasource]}]
  (run-migrations! datasource))
