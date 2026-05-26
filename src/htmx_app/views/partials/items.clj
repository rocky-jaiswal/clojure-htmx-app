(ns htmx-app.views.partials.items)

(defn item-row [item]
  [:li {:class "p-3 border-b flex justify-between items-center"}
   [:span {:class "font-medium"} (:items/name item)]
   [:span {:x-data "{open: false}" :class "flex items-center gap-2"}
    [:button {:x-on:click "open = !open"
              :class      "text-sm text-blue-500 hover:underline"} "Details"]
    [:span   {:x-show "open"
              :class  "text-sm text-gray-600"} (:items/description item)]]])

(defn items-list [items]
  [:ul {:id "items-list" :class "divide-y border rounded bg-white"}
   (if (seq items)
     (map item-row items)
     [:li {:class "p-3 text-gray-400 italic"} "No items found."])])
