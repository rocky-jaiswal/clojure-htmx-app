(ns htmx-app.views.home
  (:require [htmx-app.views.layout          :as layout]
            [htmx-app.views.partials.items  :as items-partial]))

(defn index-page [{:keys [csrf-token identity items]}]
  (layout/base
    {:title "Home" :csrf-token csrf-token :identity identity}
    [:div {:class "space-y-6"}
     [:h1 {:class "text-2xl font-bold"} "Welcome"]
     [:button {:class     "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
               :hx-get    "/partials/items"
               :hx-target "#items-container"
               :hx-swap   "innerHTML"}
      "Load Items"]
     [:div {:id "items-container"}
      (items-partial/items-list items)]]))

(defn login-page [{:keys [csrf-token error]}]
  (layout/base
    {:title "Login" :csrf-token csrf-token}
    [:div {:class "max-w-sm mx-auto space-y-4"}
     [:h1 {:class "text-xl font-bold"} "Login"]
     (when error
       [:p {:class "text-red-500 text-sm"} error])
     [:form {:method "POST" :action "/login" :class "space-y-3"}
      [:input {:type "hidden" :name "__anti-forgery-token" :value csrf-token}]
      [:div
       [:label {:class "block text-sm font-medium"} "Email"]
       [:input {:type     "email"
                :name     "email"
                :class    "w-full border rounded px-3 py-2 mt-1"
                :required true}]]
      [:div
       [:label {:class "block text-sm font-medium"} "Password"]
       [:input {:type     "password"
                :name     "password"
                :class    "w-full border rounded px-3 py-2 mt-1"
                :required true}]]
      [:button {:type  "submit"
                :class "w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"}
       "Sign In"]]]))
