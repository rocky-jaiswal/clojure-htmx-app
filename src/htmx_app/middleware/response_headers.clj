(ns htmx-app.middleware.response-headers)

(defn wrap-user-headers [handler]
  (fn [request]
    (let [response (handler request)
          identity (:identity request)]
      (cond-> response
        (:id identity)
        (assoc-in [:headers "X-User-ID"] (str (:id identity)))))))
