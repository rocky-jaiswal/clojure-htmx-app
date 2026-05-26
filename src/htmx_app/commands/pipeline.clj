(ns htmx-app.commands.pipeline
  (:require [malli.core :as m]))

(def Result
  [:or
   [:map [:ok  :any]]
   [:map [:error :any]]])

(defn found [v]
  (cond
    (:error v) v
    (nil? v)   {:error :not-found}
    :else      {:ok v}))

(defn owned-by [result owned?-fn]
  (if (:error result)
    result
    (if (owned?-fn (:ok result))
      result
      {:error :forbidden})))

(defn valid [result validate-fn]
  (if (:error result)
    result
    (if-let [err (validate-fn)]
      {:error err}
      result)))

(defn then [result f]
  (if (:error result)
    result
    (f (:ok result))))

(m/=> found    [:=> [:cat :any]        Result])
(m/=> owned-by [:=> [:cat Result fn?]  Result])
(m/=> valid    [:=> [:cat Result fn?]  Result])
(m/=> then     [:=> [:cat Result fn?]  Result])
