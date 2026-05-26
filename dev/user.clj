(ns user
  {:clj-kondo/config {:linters {:unused-referred-var {:level :off}
                                :unused-namespace    {:level :off}}}}
  (:require [integrant.repl               :refer [go halt reset reset-all set-prep!]]
            [integrant.repl.state         :refer [system]]
            [clojure.string               :as str]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]))

(set-refresh-dirs "src" "dev" "resources")

(set-prep!
  (fn []
    (require 'htmx-app.core)
    (let [cfg ((requiring-resolve 'htmx-app.config/system-config) :dev)]
      ((requiring-resolve 'malli.instrument/instrument!)
       {:ns-filter #(str/starts-with? (str %) "htmx-app.commands")})
      cfg)))

;; (go)       — start system
;; (halt)     — stop system
;; (reset)    — reload changed namespaces and restart
;; (reset-all) — reload everything and restart
