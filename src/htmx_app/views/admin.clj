(ns htmx-app.views.admin
  (:require [htmx-app.views.layout :as layout]))

(defn admin-page [{:keys [csrf-token identity]}]
  (layout/base
    {:title "Admin" :csrf-token csrf-token :identity identity}
    [:div {:class "space-y-6"}
     [:h1 {:class "text-2xl font-bold"} "Admin"]
     [:div {:class "flex gap-4"}
      [:a {:href "/admin/users" :class "px-4 py-2 bg-gray-800 text-white rounded"} "Users"]]]))

(defn users-page [{:keys [csrf-token identity users]}]
  (layout/base
    {:title "Admin — Users" :csrf-token csrf-token :identity identity}
    [:div {:class "space-y-4"}
     [:h1 {:class "text-2xl font-bold"} "Users"]
     [:table {:class "w-full border rounded bg-white text-sm"}
      [:thead {:class "bg-gray-100 text-left"}
       [:tr
        [:th {:class "px-4 py-2"} "Email"]
        [:th {:class "px-4 py-2"} "Role"]
        [:th {:class "px-4 py-2"} "Created"]]]
      [:tbody
       (for [u users]
         [:tr {:class "border-t"}
          [:td {:class "px-4 py-2"} (:users/email u)]
          [:td {:class "px-4 py-2"} (:users/role u)]
          [:td {:class "px-4 py-2"} (str (:users/created_at u))]])]]]))
