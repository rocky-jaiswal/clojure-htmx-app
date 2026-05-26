(ns htmx-app.db.core
  (:require [next.jdbc.connection :as connection]
            [integrant.core :as ig])
  (:import [com.zaxxer.hikari HikariDataSource]))

(defmethod ig/init-key :htmx-app.db.core/pool [_ {:keys [jdbc-url pool-size]}]
  (connection/->pool HikariDataSource
    {:jdbcUrl         jdbc-url
     :maximumPoolSize pool-size
     :minimumIdle     2}))

(defmethod ig/halt-key! :htmx-app.db.core/pool [_ pool]
  (.close pool))
