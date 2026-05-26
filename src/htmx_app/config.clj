(ns htmx-app.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [integrant.core :as ig]))

(defn system-config [profile]
  (let [cfg (aero/read-config (io/resource "config.edn") {:profile profile})]
    {:htmx-app.db.core/pool       (:database cfg)
     :htmx-app.db.migrations/run {:datasource (ig/ref :htmx-app.db.core/pool)}
     :htmx-app.core/jetty        {:port       (get-in cfg [:app :port])
                                   :datasource (ig/ref :htmx-app.db.core/pool)}}))
