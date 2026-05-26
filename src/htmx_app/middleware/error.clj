(ns htmx-app.middleware.error
  (:require [taoensso.timbre       :as log]
            [htmx-app.views.layout :as layout]))

(defn- stack-trace-str [^Throwable e]
  (let [sw (java.io.StringWriter.)
        pw (java.io.PrintWriter. sw)]
    (.printStackTrace e pw)
    (str sw)))

(defn- dev-error-page [^Throwable e]
  (layout/base
   {:title "500 – Server Error"}
   [:div {:class "space-y-4"}
    [:h1 {:class "text-2xl font-bold text-red-600"} "Internal Server Error"]
    [:p {:class "font-medium text-gray-800"} (.getMessage e)]
    [:pre {:class "bg-gray-100 rounded p-4 text-xs overflow-auto whitespace-pre-wrap text-gray-700"}
     (stack-trace-str e)]]))

(defn- prod-error-page
  ([] (prod-error-page "Please try again later."))
  ([message]
   (layout/base
    {:title "500 – Server Error"}
    [:div {:class "py-20 text-center space-y-3"}
     [:h1 {:class "text-2xl font-bold text-red-600"} "Something went wrong"]
     [:p {:class "text-gray-500"} message]])))

(defn wrap-errors [handler {:keys [dev?]}]
  (fn [request]
    (try
      (handler request)
      (catch java.sql.SQLTransientConnectionException e
        (log/error e "DB connection pool exhausted")
        {:status  503
         :headers {"Content-Type" "text/html; charset=utf-8"
                   "Retry-After" "5"}
         :body    (prod-error-page "Service temporarily unavailable. Please try again shortly.")})
      (catch Exception e
        (log/error e "Unhandled exception")
        {:status  500
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body    (if dev? (dev-error-page e) (prod-error-page))}))))
