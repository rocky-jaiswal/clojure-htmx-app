(ns htmx-app.views.partials.todo)

(defn list-row [lst]
  (let [id (:todo_lists/id lst)]
    [:li {:id (str "list-" id)
          :class "flex items-center justify-between p-4 border rounded bg-white"}
     [:a {:href  (str "/lists/" id)
          :class "font-medium hover:underline flex-1"}
      (:todo_lists/name lst)]
     [:div {:class "flex gap-3"}
      [:button {:hx-get    (str "/lists/" id "/edit")
                :hx-target (str "#list-" id)
                :hx-swap   "outerHTML"
                :class     "text-sm text-blue-500 hover:underline"} "Edit"]
      [:button {:hx-delete  (str "/lists/" id)
                :hx-target  (str "#list-" id)
                :hx-swap    "outerHTML"
                :hx-confirm "Delete this list and all its todos?"
                :class      "text-sm text-red-500 hover:underline"} "Delete"]]]))

(defn list-edit-form [lst]
  (let [id (:todo_lists/id lst)]
    [:li {:id (str "list-" id)
          :class "flex items-center gap-2 p-4 border rounded bg-white"}
     [:form {:hx-put    (str "/lists/" id)
             :hx-target (str "#list-" id)
             :hx-swap   "outerHTML"
             :class     "flex gap-2 flex-1"}
      [:input {:type "text" :name "name" :value (:todo_lists/name lst)
               :required true :autofocus true
               :class "flex-1 border rounded px-2 py-1 text-sm"}]
      [:button {:type "submit" :class "text-sm text-green-600 hover:underline"} "Save"]]
     [:button {:hx-get    (str "/lists/" id "/row")
               :hx-target (str "#list-" id)
               :hx-swap   "outerHTML"
               :class     "text-sm text-gray-400 hover:underline"} "Cancel"]]))

(defn item-row [list-id item]
  (let [id   (:todo_items/id item)
        done (:todo_items/done item)]
    [:li {:id (str "item-" id)
          :class "flex items-center gap-3 p-3 border rounded bg-white"}
     [:input {:type      "checkbox"
              :checked   (when done true)
              :hx-patch  (str "/lists/" list-id "/items/" id)
              :hx-target (str "#item-" id)
              :hx-swap   "outerHTML"
              :class     "w-4 h-4 cursor-pointer"}]
     [:span {:class (str "flex-1 text-sm" (when done " line-through text-gray-400"))}
      (:todo_items/title item)]
     [:div {:class "flex gap-2"}
      [:button {:hx-get    (str "/lists/" list-id "/items/" id "/edit")
                :hx-target (str "#item-" id)
                :hx-swap   "outerHTML"
                :class     "text-xs text-blue-500 hover:underline"} "Edit"]
      [:button {:hx-delete  (str "/lists/" list-id "/items/" id)
                :hx-target  (str "#item-" id)
                :hx-swap    "outerHTML"
                :hx-confirm "Remove this item?"
                :class      "text-xs text-red-500 hover:underline"} "Delete"]]]))

(defn item-edit-form [list-id item]
  (let [id (:todo_items/id item)]
    [:li {:id (str "item-" id)
          :class "flex items-center gap-2 p-3 border rounded bg-white"}
     [:form {:hx-put    (str "/lists/" list-id "/items/" id)
             :hx-target (str "#item-" id)
             :hx-swap   "outerHTML"
             :class     "flex gap-2 flex-1"}
      [:input {:type "text" :name "title" :value (:todo_items/title item)
               :required true :autofocus true
               :class "flex-1 border rounded px-2 py-1 text-sm"}]
      [:button {:type "submit" :class "text-xs text-green-600 hover:underline"} "Save"]]
     [:button {:hx-get    (str "/lists/" list-id "/items/" id "/row")
               :hx-target (str "#item-" id)
               :hx-swap   "outerHTML"
               :class     "text-xs text-gray-400 hover:underline"} "Cancel"]]))
