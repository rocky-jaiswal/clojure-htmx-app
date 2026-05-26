(ns htmx-app.views.auth
  (:require [htmx-app.views.layout :as layout]))

(defn register-page [{:keys [csrf-token error]}]
  (layout/base
   {:title "Register" :csrf-token csrf-token}
   [:div {:class "max-w-sm mx-auto space-y-4"}
    [:h1 {:class "text-xl font-bold"} "Create Account"]
    (when error
      [:p {:class "text-red-500 text-sm"} error])
    [:form {:method "POST" :action "/register" :class "space-y-3"
            :x-data "{ pw: '', confirm: '' }"}
     [:input {:type "hidden" :name "__anti-forgery-token" :value csrf-token}]
     [:div
      [:label {:class "block text-sm font-medium"} "Email"]
      [:input {:type "email" :name "email" :required true :maxlength "254"
               :class "w-full border rounded px-3 py-2 mt-1"}]]
     [:div
      [:label {:class "block text-sm font-medium"} "Password"]
      [:input {:type "password" :name "password" :required true
               :minlength "8" :maxlength "72"
               :x-model "pw"
               :class "w-full border rounded px-3 py-2 mt-1"}]
      [:p {:x-show "pw.length > 0 && !/\\d/.test(pw)"
           :class "text-red-500 text-xs mt-1"} "Must contain at least one number"]
      [:p {:x-show "pw.length > 0 && !/[^a-zA-Z0-9]/.test(pw)"
           :class "text-red-500 text-xs mt-1"} "Must contain at least one special character"]]
     [:div
      [:label {:class "block text-sm font-medium"} "Confirm Password"]
      [:input {:type "password" :name "confirm-password" :required true
               :x-model "confirm"
               :class "w-full border rounded px-3 py-2 mt-1"}]
      [:p {:x-show "confirm.length > 0 && pw !== confirm"
           :class "text-red-500 text-xs mt-1"} "Passwords do not match"]]
     [:button {:type "submit"
               :x-bind:disabled "!/\\d/.test(pw) || !/[^a-zA-Z0-9]/.test(pw) || pw !== confirm"
               :class "w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"}
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
      [:input {:type "email" :name "email" :required true :maxlength "254"
               :class "w-full border rounded px-3 py-2 mt-1"}]]
     [:div
      [:label {:class "block text-sm font-medium"} "Password"]
      [:input {:type "password" :name "password" :required true :maxlength "72"
               :class "w-full border rounded px-3 py-2 mt-1"}]]
     [:button {:type "submit"
               :class "w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"}
      "Sign In"]]
    [:p {:class "text-sm text-gray-500 text-center"}
     "No account? "
     [:a {:href "/register" :class "text-blue-600 hover:underline"} "Register"]]]))
