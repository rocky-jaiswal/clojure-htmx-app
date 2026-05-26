(ns htmx-app.views.todo
  (:require [htmx-app.views.layout       :as layout]
            [htmx-app.views.partials.todo :as partials]))

(defn lists-page [{:keys [csrf-token identity lists error]}]
  (layout/base
   {:title "My Lists" :csrf-token csrf-token :identity identity}
   [:div {:class "space-y-6"}
    [:h1 {:class "text-2xl font-bold"} "My Lists"]
    [:form {:method "post" :action "/lists" :class "space-y-2"}
     [:div {:class "flex gap-2"}
      [:input {:type "hidden" :name "__anti-forgery-token" :value csrf-token}]
      [:input {:type "text" :name "name" :placeholder "New list name..." :required true
               :class "flex-1 border rounded px-3 py-2 text-sm"}]
      [:button {:type "submit"
                :class "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"}
       "+ Add List"]]
     (when error
       [:p {:class "text-red-500 text-sm"} error])]
    (if (seq lists)
      [:ul {:id "lists-container" :class "space-y-2"}
       (map partials/list-row lists)]
      [:p {:class "text-gray-400 italic text-sm"} "No lists yet. Add one above."])]))

(defn list-detail-page [{:keys [csrf-token identity list items error]}]
  (let [list-id (:todo_lists/id list)]
    (layout/base
     {:title (:todo_lists/name list) :csrf-token csrf-token :identity identity}
     [:div {:class "space-y-6"}
      [:div {:class "flex items-center gap-3"}
       [:a {:href "/lists" :class "text-gray-400 hover:text-gray-600 text-sm"} "← Lists"]
       [:h1 {:class "text-2xl font-bold"} (:todo_lists/name list)]]
      (if (seq items)
        [:ul {:id "items-list" :class "space-y-2"}
         (map #(partials/item-row list-id %) items)]
        [:p {:id "items-list" :class "text-gray-400 italic text-sm"} "No todos yet."])
      [:form {:method "post" :action (str "/lists/" list-id "/items") :class "space-y-2"}
       [:div {:class "flex gap-2"}
        [:input {:type "hidden" :name "__anti-forgery-token" :value csrf-token}]
        [:input {:type "text" :name "title" :placeholder "Add a todo..." :required true
                 :class "flex-1 border rounded px-3 py-2 text-sm"}]
        [:button {:type "submit"
                  :class "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"}
         "+ Add"]]
       (when error
         [:p {:class "text-red-500 text-sm"} error])]])))
