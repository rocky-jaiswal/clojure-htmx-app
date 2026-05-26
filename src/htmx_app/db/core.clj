(ns htmx-app.db.core
  (:require [next.jdbc.connection :as connection]
            [integrant.core :as ig])
  (:import [com.zaxxer.hikari HikariDataSource]))

(defmethod ig/init-key :htmx-app.db.core/pool [_ {:keys [jdbc-url pool-size]}]
  (connection/->pool HikariDataSource
                     {:jdbcUrl               jdbc-url
                      :maximumPoolSize       pool-size
                      :minimumIdle           2
                      :connectionTimeout     5000    ; ms to acquire connection from pool before throwing
                      :idleTimeout           600000  ; ms before idle connection is retired
                      :maxLifetime           1800000 ; ms max connection lifetime
                      :connectionInitSql     "SET statement_timeout = '30s'"}))

(defmethod ig/halt-key! :htmx-app.db.core/pool [_ pool]
  (.close pool))
