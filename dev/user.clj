(ns user
  {:clj-kondo/config {:linters {:unused-referred-var {:level :off}
                                :unused-namespace    {:level :off}}}}
  (:require [integrant.repl               :refer [go halt reset reset-all set-prep!]]
            [integrant.repl.state         :refer [system]]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]))

(set-refresh-dirs "src" "dev" "resources")

(set-prep!
  (fn []
    ;; Require core first — it pulls in db.core, db.migrations, and all ig/init-key methods
    (require 'htmx-app.core)
    ((requiring-resolve 'htmx-app.config/system-config) :dev)))

;; (go)       — start system
;; (halt)     — stop system
;; (reset)    — reload changed namespaces and restart
;; (reset-all) — reload everything and restart
