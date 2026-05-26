(ns htmx-app.views.auth
  (:require [htmx-app.views.layout :as layout]))

(defn register-page [{:keys [csrf-token error]}]
  (layout/base
    {:title "Register" :csrf-token csrf-token}
    [:div {:class "max-w-sm mx-auto space-y-4"}
     [:h1 {:class "text-xl font-bold"} "Create Account"]
     (when error
       [:p {:class "text-red-500 text-sm"} error])
     [:form {:method "POST" :action "/register" :class "space-y-3"}
      [:input {:type "hidden" :name "__anti-forgery-token" :value csrf-token}]
      [:div
       [:label {:class "block text-sm font-medium"} "Email"]
       [:input {:type "email" :name "email" :required true
                :class "w-full border rounded px-3 py-2 mt-1"}]]
      [:div
       [:label {:class "block text-sm font-medium"} "Password"]
       [:input {:type "password" :name "password" :required true
                :class "w-full border rounded px-3 py-2 mt-1"}]]
      [:div
       [:label {:class "block text-sm font-medium"} "Confirm Password"]
       [:input {:type "password" :name "confirm-password" :required true
                :class "w-full border rounded px-3 py-2 mt-1"}]]
      [:button {:type "submit"
                :class "w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"}
       "Create Account"]]
     [:p {:class "text-sm text-gray-500 text-center"}
      "Already have an account? "
      [:a {:href "/login" :class "text-blue-600 hover:underline"} "Sign in"]]]))

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
       [:input {:type "email" :name "email" :required true
                :class "w-full border rounded px-3 py-2 mt-1"}]]
      [:div
       [:label {:class "block text-sm font-medium"} "Password"]
       [:input {:type "password" :name "password" :required true
                :class "w-full border rounded px-3 py-2 mt-1"}]]
      [:button {:type "submit"
                :class "w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"}
       "Sign In"]]
     [:p {:class "text-sm text-gray-500 text-center"}
      "No account? "
      [:a {:href "/register" :class "text-blue-600 hover:underline"} "Register"]]]))
