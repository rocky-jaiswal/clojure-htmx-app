(ns htmx-app.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [integrant.core :as ig]))

(def ^:private required-env-vars
  ["DATABASE_URL" "PRIVATE_KEY_PASSPHRASE"])

(defn- load-dotenv! []
  (let [f (io/file ".env")]
    (when (.exists f)
      (doseq [line  (str/split-lines (slurp f))
              :let  [line (str/trim line)]
              :when (and (seq line)
                         (not (str/starts-with? line "#"))
                         (str/includes? line "="))]
        (let [[k v] (str/split line #"=" 2)]
          (when (and (seq k) (nil? (System/getenv k)))
            (System/setProperty k v)))))))

(defn- env-val [k]
  (or (System/getenv k) (System/getProperty k)))

(defn validate-config! [profile]
  (when (= profile :prod)
    (let [missing (remove env-val required-env-vars)]
      (when (seq missing)
        (throw (ex-info (str "Missing required environment variables: " (str/join ", " missing))
                        {:missing missing}))))))

(defn system-config [profile]
  (load-dotenv!)
  (validate-config! profile)
  (let [cfg (aero/read-config (io/resource "config.edn") {:profile profile})]
    {:htmx-app.db.core/pool       (:database cfg)
     :htmx-app.db.migrations/run {:datasource (ig/ref :htmx-app.db.core/pool)}
     :htmx-app.core/jetty        {:port       (get-in cfg [:app :port])
                                  :datasource (ig/ref :htmx-app.db.core/pool)
                                  :jwt        (:jwt cfg)
                                  :dev?       (not= profile :prod)}}))
