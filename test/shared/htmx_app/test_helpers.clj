(ns htmx-app.test-helpers
  (:require [next.jdbc            :as jdbc]
            [next.jdbc.connection :as connection]
            [migratus.core        :as migratus])
  (:import [com.zaxxer.hikari HikariDataSource]))

(def db-url
  (or (System/getenv "TEST_DATABASE_URL")
      "jdbc:postgresql://localhost:5433/htmx_app_test?user=htmx_app_test&password=test_secret"))

(defonce test-ds
  (delay
    (let [ds (connection/->pool HikariDataSource {:jdbcUrl db-url :maximumPoolSize 2})]
      (migratus/migrate {:store    :database
                         :db       {:datasource ds}
                         :location "migrations"})
      ds)))

(defn clean-db! []
  (jdbc/execute! @test-ds
                 ["TRUNCATE todo_items, todo_lists, sessions, users RESTART IDENTITY CASCADE"]))

(defn db-fixture [f]
  (clean-db!)
  (f))
