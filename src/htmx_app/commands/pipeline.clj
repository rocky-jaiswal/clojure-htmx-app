(ns htmx-app.commands.pipeline)

(defn found
  "Wraps a nullable DB value: nil → {:error :not-found}, v → {:ok v}.
   Passes an already-failed result through unchanged."
  [v]
  (cond
    (:error v) v
    (nil? v)   {:error :not-found}
    :else      {:ok v}))

(defn owned-by
  "Short-circuits with :forbidden if (owned?-fn value) is falsy."
  [result owned?-fn]
  (if (:error result)
    result
    (if (owned?-fn (:ok result))
      result
      {:error :forbidden})))

(defn valid
  "Short-circuits with the validation error if (validate-fn) returns non-nil."
  [result validate-fn]
  (if (:error result)
    result
    (if-let [err (validate-fn)]
      {:error err}
      result)))

(defn then
  "Passes the unwrapped :ok value to f. Skips f and forwards the error if already failed."
  [result f]
  (if (:error result)
    result
    (f (:ok result))))
