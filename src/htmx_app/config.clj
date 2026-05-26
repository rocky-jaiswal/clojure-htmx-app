(ns htmx-app.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [integrant.core :as ig]))

(def ^:private required-env-vars
  ["DATABASE_URL" "SESSION_SECRET"])

(defn validate-config! [profile]
  (when (= profile :prod)
    (let [missing (remove #(System/getenv %) required-env-vars)]
      (when (seq missing)
        (throw (ex-info (str "Missing required environment variables: " (str/join ", " missing))
                        {:missing missing}))))))

(defn system-config [profile]
  (validate-config! profile)
  (let [cfg (aero/read-config (io/resource "config.edn") {:profile profile})]
    {:htmx-app.db.core/pool       (:database cfg)
     :htmx-app.db.migrations/run {:datasource (ig/ref :htmx-app.db.core/pool)}
     :htmx-app.core/jetty        {:port       (get-in cfg [:app :port])
                                  :datasource (ig/ref :htmx-app.db.core/pool)
                                  :dev?       (not= profile :prod)}}))
