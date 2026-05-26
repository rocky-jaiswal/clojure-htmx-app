(ns htmx-app.views.layout
  (:require [hiccup2.core :as h]))

(def doctype "<!DOCTYPE html>")

(defn head [title]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:title title]
   [:script {:src "https://cdn.tailwindcss.com"}]
   [:script {:src "https://unpkg.com/htmx.org@2.0.3"}]
   [:script {:defer true :src "https://unpkg.com/alpinejs@3.x.x/dist/cdn.min.js"}]])

(defn csrf-script [csrf-token]
  [:script
   (h/raw (str "document.addEventListener('htmx:configRequest', (e) => {"
               "  e.detail.headers['X-CSRF-Token'] = '" csrf-token "';"
               "});"))])

(defn base
  [{:keys [title csrf-token identity]} & content]
  (str doctype
       (h/html
         [:html {:lang "en"}
          (head (or title "htmx-app"))
          [:body {:class "bg-gray-50 text-gray-900"}
           (csrf-script csrf-token)
           [:nav {:class "bg-white shadow px-6 py-3 flex gap-4 items-center"}
            [:a {:href "/" :class "font-semibold"} "Home"]
            (if identity
              [:div {:class "flex gap-4 ml-auto items-center"}
               [:span {:class "text-sm text-gray-500"} (:email identity)]
               [:a {:href "/admin" :class "text-gray-600 text-sm"} "Admin"]
               [:form {:method "POST" :action "/logout"}
                [:button {:type "submit" :class "text-sm text-red-500"} "Logout"]]]
              [:a {:href "/login" :class "ml-auto text-sm text-gray-600"} "Login"])]
           [:main {:class "max-w-4xl mx-auto px-4 py-8"}
            content]]])))
