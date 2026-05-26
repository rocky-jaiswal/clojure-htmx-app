(ns htmx-app.validation
  (:require [malli.core  :as m]
            [malli.error :as me]))

(defn first-error [schema value]
  (when-let [errors (m/explain schema value)]
    (->> errors me/humanize (remove nil?) first)))
